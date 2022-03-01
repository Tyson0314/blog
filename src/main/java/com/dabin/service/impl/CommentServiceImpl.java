package com.dabin.service.impl;

import com.dabin.common.base.BaseService;
import com.dabin.common.base.Result;
import com.dabin.common.constants.*;
import com.dabin.common.utils.RedisUtils;
import com.dabin.common.utils.SnowFlakeWorker;
import com.dabin.dao.CommentMapper;
import com.dabin.dao.UserMapper;
import com.dabin.dao.VoteMapper;
import com.dabin.dto.CommentAddRequest;
import com.dabin.dto.CommentListRequest;
import com.dabin.entity.*;
import com.dabin.service.BlogService;
import com.dabin.service.CommentService;
import com.dabin.service.UserService;
import com.dabin.vo.CommentListItemVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.annotation.concurrent.NotThreadSafe;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.dabin.common.constants.SystemConstant.USER_ID;

/**
 * 评论相关service
 *
 * @author 大彬
 * @date 2021-07-04 23:12
 */
@Slf4j
@Service
@NotThreadSafe
public class CommentServiceImpl extends BaseService implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private UserService userService;

    @Resource
    private BlogService blogService;

    @Resource
    private VoteMapper voteMapper;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private UserMapper userMapper;

    /**
     * 添加评论
     *
     * @param commentAddRequest
     * @return
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Result<Boolean> addCommentInfo(HttpServletRequest request, CommentAddRequest commentAddRequest) {
//        if (!isValidComment()){
//            return resultError("评论过于频繁，请稍后再试");
//        }
        Comment comment = new Comment();
        Result<Boolean> validateResult = validateAndInitAddRequest(request, commentAddRequest, comment);
        if (!validateResult.isOk()) {
            return validateResult;
        }
        if (commentMapper.insertSelective(comment) == 0) {
            return resultError4DB("评论失败");
        }
        return resultOk();
    }

    /**
     * 校验添加参数
     *
     * @param commentAddRequest
     * @param comment
     */
    private Result<Boolean> validateAndInitAddRequest(HttpServletRequest request, CommentAddRequest commentAddRequest, Comment comment) {
        String email = commentAddRequest.getUserEmail();
        Integer targetId = commentAddRequest.getTargetId();
        CommentType targetType = commentAddRequest.getTargetType();
        String content = commentAddRequest.getContent();

        User user = null;
        String userId = request.getAttribute(USER_ID) == null ? "" : request.getAttribute(USER_ID).toString();

        if (StringUtils.isEmpty(userId)) {
            return Result.createByErrorCode(ResultCode.USER_ID_EMPTY);
        }

        if (targetId == null) {
            return Result.createByErrorCode(ResultCode.COMMENT_TARGET_ID_EMPTY);
        }

        if (StringUtils.isEmpty(content)) {
            return Result.createByErrorCode(ResultCode.COMMENT_CONTENT_EMPTY);
        }

        comment.setContent(commentAddRequest.getContent());

        user = userService.getUserById(userId);
        Integer pid = commentAddRequest.getPid();
        // 1.pid不为null表明是回复信息，需要判断被回复的评论是否存在
        if (Objects.nonNull(pid)) {
            if (Objects.isNull(commentMapper.selectByPrimaryKey(pid))) {
                return resultError4Param("被回复的主体不存在");
            }
        }
        // 2. 如果是博客留言
        switch (targetType) {
            case ARTICLE:
                // 判断博客是否存在
                if (Objects.isNull(blogService.getBlogById(targetId))) {
                    return resultError4Param("被评论的主体不存在");
                }
                break;
            //3. 如果是留言板, 被评论的是一个留言，如果targetId不存在则等于pid
            case MESSAGE_BOARD:

                break;
            case COMMENT_REPLY:
                if (Objects.isNull(pid)) {
                    return resultError4Param("父级评论不存在");
                }
                if (Objects.isNull(commentMapper.selectByPrimaryKey(targetId))) {
                    return resultError4Param("被回复的主体不存在");
                }
                break;
            default:
                return resultError4Param("被评论的主体不明确");
        }

        // 4. 校验用户信息，并更新或添加用户数据
        Result<Boolean> updateResult = resultOk();
        if (user == null && Objects.isNull(user = userService.getUserDOByEmail(email))) {
            String userName = commentAddRequest.getUserName();
            if (StringUtils.isEmpty(userName)) {
                return resultError4Param("首次评论必须填写用户名");
            }
            SnowFlakeWorker idWorker = new SnowFlakeWorker(0, 0);

            user = new User();
            user.setUserEmail(email);
            user.setUserName(userName);
            user.setUserStatus(StatusEnum.VALID.getCode());
            user.setId(idWorker.nextId() + "");
            // 添加用户信息
            updateResult = userService.createUserInfo(user);
            // 判断用户名是否存在
        } else {
            if (Objects.nonNull(commentAddRequest.getUserName()) && !Objects.equals(user.getUserName(), commentAddRequest.getUserName())) {
                user.setUserName(commentAddRequest.getUserName());
                user.setUpdateTime(new Date());
                updateResult = userService.updateVisitorInfo(user);
            }
        }
        if (!updateResult.isOk()) {
            return updateResult;
        }

        BeanUtils.copyProperties(commentAddRequest, comment);
        comment.setUserName(user.getUserName());
        comment.setTargetType(targetType.getCode());
        comment.setUserId(userId);
        comment.setCommentStatus(StatusEnum.VALID.getCode());
        return resultOk();
    }

    /**
     * 获取评论列表
     * 1.如果是博客评论
     *
     * @param targetId
     * @param commentListRequest
     * @return
     */
    @Override
    public Result<PageInfo<CommentListItemVO>> getCommentInfos(Integer targetId, String userId, CommentListRequest commentListRequest) {
        Preconditions.checkNotNull(commentListRequest, "评论请求参数不能为null");
        Integer pid = commentListRequest.getPid();
        CommentType targetType = commentListRequest.getTargetType();
        Preconditions.checkNotNull(targetType, "主体类型不能为null");
        // 如果时留言板消息的话，当pid为null时，targetId可以为null
        Page page = getPage(commentListRequest);
        CommentExample commentExample = new CommentExample();
        CommentExample.Criteria criteria = commentExample.createCriteria();
        criteria.andTargetTypeEqualTo(targetType.getCode())
                .andCommentStatusEqualTo(StatusEnum.VALID.getCode());
        commentExample.setOrderByClause(page.getOrderBy());
        List<Comment> commentList;
        if (Objects.nonNull(pid)) {
            criteria.andTargetIdEqualTo(targetId);
        } else if (Objects.nonNull(targetId)) {
            criteria.andTargetIdEqualTo(targetId);
        } else if (!Objects.equals(targetType, CommentType.MESSAGE_BOARD)) {
            return resultError4Param("参数不合法");
        }
        commentList = commentMapper.selectByExample(commentExample);
        List<CommentListItemVO> resultList = commentList.stream().map(CommentListItemVO::createFrom).collect(Collectors.toList());

        //查询用户头像
        Set<String> userIds = resultList.stream().map(CommentListItemVO::getUserId).collect(Collectors.toSet());
        List<User> userInfos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userIds)) {
            userInfos = userMapper.selectByUserIds(userIds);
        }
        Map<String, String> avatarMap = new HashMap<>();
        for (User user : userInfos) {
            avatarMap.put(user.getId(), user.getAvatarUrl());
        }

        /**
         * 查询每个留言的评论
         */
        resultList.forEach(item -> {
            item.setAvatarUrl(avatarMap.get(item.getUserId()));
            item.setChildrenComments(getCommentReplyById(item.getId()).stream().map(CommentListItemVO::createFrom).collect(Collectors.toList()));
        });

        List<Integer> commentIdList = getCommentIds(resultList);

        List<Vote> voteList = new ArrayList<>();
        if (!StringUtils.isEmpty(userId) && CollectionUtils.isNotEmpty(commentIdList)) {
            voteList = voteMapper.selectByTargetIds(commentIdList, VoteType.COMMENT);
        }
        //key为targetId，value为点赞状态
        Map<Integer, Boolean> voteStatusMap = new HashMap<>();
        for (Vote vote : voteList) {
            voteStatusMap.put(vote.getTargetId(), StringUtils.equals(vote.getStatus(), VoteStatus.LIKE));
        }

        setVoteStatusForCommentList(resultList, voteStatusMap);

        PageInfo pageInfo = page.toPageInfo();
        pageInfo.setList(resultList);

        return resultOk(pageInfo);
    }

    private void setVoteStatusForCommentList(List<CommentListItemVO> resultList, Map<Integer, Boolean> voteStatusMap) {
        for (CommentListItemVO commentListItem : resultList) {
            if (voteStatusMap.get(commentListItem.getId()) != null) {
                commentListItem.setVoteStatus(voteStatusMap.get(commentListItem.getId()));
            }
            //设置子评论点赞状态
            for (CommentListItemVO commentListItemVO : commentListItem.getChildrenComments()) {
                commentListItemVO.setVoteStatus(voteStatusMap.get(commentListItemVO.getId()));
            }
        }
    }

    private List<Integer> getCommentIds(List<CommentListItemVO> resultList) {
        List<Integer> commentIds = resultList.stream().map(CommentListItemVO::getId).collect(Collectors.toList());

        List<List<CommentListItemVO>> childCommendList = resultList.stream()
                .map(CommentListItemVO::getChildrenComments)
                .collect(Collectors.toList());
        for (List<CommentListItemVO> commentListItemVOList : childCommendList) {
            commentIds.addAll(getCommentIds(commentListItemVOList));
        }

        return commentIds;
    }

    public List<Comment> getCommentReplyById(int id) {
        CommentExample commentExample = new CommentExample();
        CommentExample.Criteria criteria = commentExample.createCriteria();
        criteria.andTargetTypeEqualTo(CommentType.COMMENT_REPLY.getCode())
                .andCommentStatusEqualTo(StatusEnum.VALID.getCode());
        commentExample.setOrderByClause("create_time");

        return commentMapper.selectByExample(commentExample);
    }


    /**
     * 删除评论
     *
     * @param commentId 评论id
     * @return
     */
    @Override
    public Result<Boolean> deleteCommentInfo(Integer commentId) {
        Preconditions.checkNotNull(commentId, "要删除的评论id不能为null");
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria().andPidEqualTo(commentId);
        if (CollectionUtils.isNotEmpty(commentMapper.selectByExample(commentExample))) {
            return resultError4Param("当前评论有回复哦");
        }
        if (commentMapper.deleteByPrimaryKey(commentId) == 0) {
            return resultError4DB("删除失败");
        }
        return resultOk();
    }

    /**
     * 将Redis评论点赞数存储到数据库，定时任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transVoteDataFromRedis2DB() {
        List<Comment> commentList = redisUtils.getCommentVoteCountFromRedis();
        if (CollectionUtils.isNotEmpty(commentList)) {
            commentMapper.updateBatch(commentList);
        } else {
            log.info("transtCommentVoteDataFromRedis2DB, blog list empty");
        }
    }

}
