package com.dabin.service.impl;

import com.dabin.common.base.Result;
import com.dabin.common.constants.RedisConstant;
import com.dabin.common.constants.ResultCode;
import com.dabin.common.constants.VoteStatus;
import com.dabin.common.constants.VoteType;
import com.dabin.common.utils.RedisUtils;
import com.dabin.dao.BlogMapper;
import com.dabin.dao.CommentMapper;
import com.dabin.dao.VoteMapper;
import com.dabin.entity.BlogWithBLOBs;
import com.dabin.entity.Comment;
import com.dabin.entity.Vote;
import com.dabin.entity.VoteExample;
import com.dabin.service.CommonService;
import com.dabin.service.VoteService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: 程序员大彬
 * @time: 2021-12-23 22:22
 */
@Service
@Slf4j
public class VoteServiceImpl implements VoteService {

    @Autowired
    VoteMapper voteMapper;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    BlogMapper blogMapper;

    @Autowired
    CommonService commonService;

    @Autowired
    CommentMapper commentMapper;

    private static final String LOCK = "lock";

    @Override
    public Result<Integer> vote(Integer targetId, String userId, String type) {
        if (StringUtils.isEmpty(userId)) {
            return Result.createByErrorCode(ResultCode.USER_ID_EMPTY);
        }

        if (StringUtils.isEmpty(type)) {
            return Result.createByErrorMessage("未指定点赞类型");
        }

        String newVoteStatus;
        int finalCount;

        //todo 分布式锁
        synchronized (LOCK) {
            Vote vote = new Vote();
            vote.setType(type);
            vote.setUserId(userId);
            vote.setTargetId(targetId);

            VoteExample voteExample = new VoteExample();
            voteExample.createCriteria().andTargetIdEqualTo(targetId)
                    .andTypeEqualTo(type)
                    .andUserIdEqualTo(userId);

            List<Vote> voteList = voteMapper.selectByExample(voteExample);
            if (CollectionUtils.isNotEmpty(voteList)) {
                Vote oldVote = voteList.get(0);
                newVoteStatus = StringUtils.equals(oldVote.getStatus(), VoteStatus.LIKE) ? VoteStatus.UNLIKE : VoteStatus.LIKE;
                vote.setId(voteList.get(0).getId());
                vote.setStatus(newVoteStatus);
                voteMapper.updateByPrimaryKeySelective(vote);
            } else {
                newVoteStatus = VoteStatus.LIKE;
                vote.setStatus(newVoteStatus);
                voteMapper.insertSelective(vote);
            }

            String voteCountKey = type + RedisConstant.VOTE_KEY_SPLIT + targetId;

            //缓存不存在，查询数据库，然后存入Redis，key为targetId，value为点赞数
            if (!redisTemplate.opsForHash().hasKey(RedisConstant.MAP_VOTE_COUNT_KEY, voteCountKey)) {
                if (StringUtils.equals(VoteType.ARTICLE, type)) {
                    BlogWithBLOBs blog = blogMapper.selectByPrimaryKey(targetId);
                    if (blog == null) {
                        return Result.createByErrorCode(ResultCode.VOTE_TARGET_NOT_EXIST);
                    }
                    finalCount = blog.getVoteCount() + (StringUtils.equals(newVoteStatus, VoteStatus.LIKE) ? 1 : -1);
                } else {
                    Comment comment = commentMapper.selectByPrimaryKey(targetId);
                    if (comment == null) {
                        return Result.createByErrorCode(ResultCode.VOTE_TARGET_NOT_EXIST);
                    }
                    finalCount = comment.getVoteCount() + (StringUtils.equals(newVoteStatus, VoteStatus.LIKE) ? 1 : -1);
                }
                redisTemplate.opsForHash().put(RedisConstant.MAP_VOTE_COUNT_KEY, voteCountKey, String.valueOf(finalCount));
            } else {
                finalCount = Math.toIntExact(redisTemplate.opsForHash().increment(
                        RedisConstant.MAP_VOTE_COUNT_KEY, voteCountKey, StringUtils.equals(VoteStatus.LIKE, newVoteStatus) ? 1 : -1));
            }
            log.info("newVoteStatus:{}, finalCount:{}", newVoteStatus, finalCount);
        }
        return Result.createBySuccess(finalCount);
    }

    @Override
    public int blogVoteCount(Integer blogId, String type) {
        return voteMapper.blogVoteCount(blogId, type);
    }

    @Override
    public List<Vote> queryVote(VoteExample example) {
        return voteMapper.selectByExample(example);
    }


}
