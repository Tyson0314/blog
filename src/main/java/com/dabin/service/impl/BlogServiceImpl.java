package com.dabin.service.impl;

import com.dabin.common.base.BaseService;
import com.dabin.common.base.Result;
import com.dabin.common.constants.*;
import com.dabin.common.exception.BusinessException;
import com.dabin.common.utils.DateCalUtils;
import com.dabin.common.utils.JsonUtils;
import com.dabin.common.utils.RedisUtils;
import com.dabin.dao.BlogMapper;
import com.dabin.dao.BlogTagMapper;
import com.dabin.dao.UserMapper;
import com.dabin.dto.*;
import com.dabin.entity.*;
import com.dabin.service.BlogService;
import com.dabin.service.CommentService;
import com.dabin.service.CommonService;
import com.dabin.service.VoteService;
import com.dabin.vo.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.dabin.common.constants.SystemConstant.USER_ID;

/**
 * 博客管理的service
 *
 * @author 大彬
 * @date 2021-08-18 11:00
 */
@Slf4j
@Service
public class BlogServiceImpl extends BaseService implements BlogService {

    @Resource
    private BlogMapper blogMapper;

    @Resource
    private BlogTagMapper blogTagMapper;

    @Autowired
    private TagServiceImpl tagService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private VoteService voteService;

    @Value("${qiniu.url-prefix}")
    private String imgPrefix;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    CommonService commonService;

    @Autowired
    UserMapper userMapper;

    /**
     * 添加新的博客
     *
     * @param addRequest 添加数据请求
     * @return
     */
    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public Result<Boolean> createBlogInfo(HttpServletRequest request, BlogAddRequest addRequest) {

        String userId = request.getAttribute(USER_ID) == null ? "" : request.getAttribute(USER_ID).toString();

        if (StringUtils.isEmpty(userId)) {
            return Result.createByErrorMessage("用户id为空");
        }

        addRequest.setAuthor(userId);

        BlogWithBLOBs blogDO = new BlogWithBLOBs();
        // 校验参数
        Result<Boolean> validateResult = validateAndInitCreateRequest(addRequest, blogDO);
        if (!validateResult.isOk()) {
            return validateResult;
        }
        // 添加到数据库
        if (blogMapper.insertSelective(blogDO) == 0) {
            return resultError4DB();
        }

        List<Integer> tags = addRequest.getTags();
        if (CollectionUtils.isNotEmpty(tags)) {
            boolean bindResult = tagService.bindTags2Blog(blogDO.getId(), tags);
            if (!bindResult) {
                throw new BusinessException("添加博客标签绑定失败");
            }
        }

        return resultOk();
    }

    /**
     * 更新博客
     *
     * @param id            主鍵id
     * @param updateRequest 更新请求参数
     * @return
     */
    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public Result<Boolean> updateBlog(Integer id, BlogUpdateRequest updateRequest) {
        Preconditions.checkNotNull(id, "更新时id参数不能为null");

        BlogWithBLOBs blogWithBLOBs = blogMapper.selectByIdAndStatus(id, null);
        Result<Boolean> validateResult = validateAndInitUpdateRequest(updateRequest, blogWithBLOBs);
        if (!validateResult.isOk()) {
            return validateResult;
        }

        // 删除旧的标签关联
        tagService.deleteTagsByBlogId(id);

        // 添加新的标签关联
        List<Integer> tagIds = updateRequest.getTags();
        for (Integer tagId : tagIds) {
            if (blogTagMapper.insert(new BlogTag(null, tagId, id)) == 0) {
                throw new BusinessException(ResultCode.DATABASE_ERROR.getCode(), "添加标签过程失败");
            }
        }

        if (blogMapper.updateByPrimaryKeyWithBLOBs(blogWithBLOBs) == 0) {
            throw new BusinessException(ResultCode.DATABASE_ERROR.getCode(), "更新过程失败");
        }

        return resultOk();
    }


    /**
     * 校验添加参数，然后规整参数
     *
     * @param addRequest 添加请求信息
     * @param blogDO     要添加的do
     * @return
     */
    private Result<Boolean> validateAndInitCreateRequest(BlogAddRequest addRequest, BlogWithBLOBs blogDO) {
        Preconditions.checkNotNull(blogDO, "添加参数不能为null");

        fillCreateTime(blogDO);
        return validateAndInitUpdateRequest(addRequest, blogDO);
    }

    /**
     * 校验更新参数， 然后规整参数
     *
     * @param updateRequest
     * @param blogDO
     * @return
     */
    private Result<Boolean> validateAndInitUpdateRequest(BlogUpdateRequest updateRequest, BlogWithBLOBs blogDO) {
        Preconditions.checkNotNull(updateRequest, "更新参数不能为null");
        Preconditions.checkNotNull(blogDO, "博客不存在");

        // 标题是否存在/发生变化
        String newTitle = updateRequest.getTitle();
        String oldTitle = blogDO.getTitle();
        BlogExample blogDOExample = new BlogExample();
        blogDOExample.createCriteria().andTitleEqualTo(newTitle);
        if (Objects.equals(newTitle, oldTitle) || CollectionUtils.isNotEmpty(blogMapper.selectByExample(blogDOExample))) {
            resultError4Param("标题[" + updateRequest.getTitle() + "]已存在");
        }

        // 分类是否存在
        Integer categoryId = updateRequest.getCategoryId();
        if (Objects.nonNull(categoryId)) {
            if (CollectionUtils.isEmpty(tagService.getTagListByTypeAndIds(TagType.CATEGORY, Lists.newArrayList(categoryId)))) {
                return resultError4Param("分类信息不存在");
            }
        }

        // 标签是否存在
        List<Integer> tags = updateRequest.getTags();
        if (CollectionUtils.isNotEmpty(tags)) {
            List<Tag> tagEntities = tagService.getTagListByTypeAndIds(TagType.TAG, Lists.newArrayList(tags));
            if (CollectionUtils.isNotEmpty(tagEntities)) {
                updateRequest.setTags(tagEntities.stream().map(Tag::getId).collect(Collectors.toList()));
            }
        }
        BeanUtils.copyProperties(updateRequest, blogDO);
        // todo  code、status规整
        fillUpdateTime(blogDO);
        blogDO.setBlogStatus(updateRequest.getStatus());
        //设置用户id
        blogDO.setAuthor(updateRequest.getAuthor());

        return resultOk();
    }

    /**
     * 分页查询博客
     *
     * @param queryRequest 查询条件
     * @return
     */
    @Override
    public Result<PageInfo<BlogDetail4UserVO>> getBlogListInfos(BlogListRequest queryRequest) {
        Preconditions.checkNotNull(queryRequest, "查询参数不能为null");

        Page page = getPage(queryRequest);
        queryRequest.setStatus(DataStatus.NO_DELETED);
        List<Blog> blogList = blogMapper.selectBlogs(queryRequest);

        List<Integer> blogIdList = blogList.stream().map(Blog::getId).collect(Collectors.toList());
        List<BlogTagVo> tagList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(blogIdList)) {
            tagList = tagService.selectTagsByBlogIds(blogIdList);
        }
        Map<Integer, Set<TagVO>> tagMap = new HashMap<>();
        for (BlogTagVo blogTagVo : tagList) {
            Set<TagVO> blogTagVoSet = tagMap.get(blogTagVo.getBlogId());
            TagVO tagVo = new TagVO();
            BeanUtils.copyProperties(blogTagVo, tagVo);
            if (CollectionUtils.isNotEmpty(blogTagVoSet)) {
                blogTagVoSet.add(tagVo);
            } else {
                blogTagVoSet = new HashSet<>();
                blogTagVoSet.add(tagVo);
                tagMap.put(blogTagVo.getBlogId(), blogTagVoSet);
            }
        }
        Map<String, String> authorNameMap = new HashMap<>();
        Map<String, String> avatarUrlMap = new HashMap<>();
        Set<String> authorIdSet = blogList.stream().map(Blog::getAuthor).collect(Collectors.toSet());
        List<User> userList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(authorIdSet)) {
            userList = userMapper.selectByUserIds(authorIdSet);
        }
        for (User user : userList) {
            authorNameMap.put(user.getId() + "", user.getNickName());
            avatarUrlMap.put(user.getId() +"", user.getAvatarUrl());
        }
        List<BlogDetail4UserVO> blogDetailList = blogList.stream().map(blog -> {
            BlogDetail4UserVO blogDetail = convertBlogDOToVO(blog);
            blogDetail.setAuthorName(authorNameMap.get(blog.getAuthor()));
            blogDetail.setAvatarUrl(avatarUrlMap.get(blog.getAuthor()));
            blogDetail.setTagList(new ArrayList<>(tagMap.get(blog.getId())));
            if (StringUtils.isNotBlank(blog.getImgUrl())) {
                blogDetail.setImgUrl(imgPrefix + blog.getImgUrl());
            }
            return blogDetail;
        }).collect(Collectors.toList());

        PageInfo pageInfo = page.toPageInfo();
        pageInfo.setList(blogDetailList);
        return Result.createBySuccess(pageInfo);
    }

    /**
     * 获取博客详情
     *
     * @param blogId
     * @return
     */
    @Override
    public Result<BlogDetail4UserVO> getBlogDetailsInfo(Integer blogId, String userId) {
        BlogWithBLOBs blogDO = blogMapper.selectByIdAndStatus(blogId, DataStatus.NO_DELETED);
        if (Objects.isNull(blogDO)) {
            return resultError4Param("数据不存在");
        }

        BlogDetail4UserVO blogDetail4UserVO = convertBlogToVO(blogDO);
        // 博客的标签
        blogDetail4UserVO.setTagList(tagService.getTagListByBlogId(blogId));

        // 博客分类名称
        Result<TagVO> tagVo = tagService.getTagDetailInfoById(blogDetail4UserVO.getCategoryId());
        if (tagVo != null && tagVo.getData() != null) {
            TagVO tag = tagVo.getData();
            blogDetail4UserVO.setCategoryName(tag.getTagName());
        }

        // 查询上一篇、下一篇
        blogDetail4UserVO.setLast(getlastOrNext(blogId, false));
        blogDetail4UserVO.setNext(getlastOrNext(blogId, true));

        //查询评论列表
        CommentListRequest commentListRequest = new CommentListRequest();
        commentListRequest.setTargetType(CommentType.ARTICLE);
        blogDetail4UserVO.setComments(commentService.getCommentInfos(blogId, userId, commentListRequest).getData());

        //博客点赞数
        blogDetail4UserVO.setVoteCount(voteService.blogVoteCount(blogId, VoteType.ARTICLE));

        String voteStatus;
        // 获取用户点赞状态
        if (StringUtils.isNotEmpty(userId)) {
            VoteExample voteExample = new VoteExample();
            voteExample.createCriteria().andUserIdEqualTo(userId)
                    .andTargetIdEqualTo(blogId)
                    .andTypeEqualTo(VoteType.ARTICLE);
            List<Vote> voteList = voteService.queryVote(voteExample);
            if (CollectionUtils.isNotEmpty(voteList)) {
                voteStatus = voteList.get(0).getStatus();
                blogDetail4UserVO.setVoteStatus(StringUtils.equals(voteStatus, VoteStatus.LIKE));
            }
        }

        //浏览次数加1
        if (isValidViewOrLike(blogId.toString())) {
            updateBlogCount(blogId, "", StatisticType.VIEW);
        }

        //博客作者昵称
        User user = userMapper.selectByPrimaryKey(blogDetail4UserVO.getAuthor());
        if (user != null) {
            blogDetail4UserVO.setAuthorName(user.getNickName());
        }

        return Result.createBySuccess(blogDetail4UserVO);
    }

    /**
     * 根据id获取博客
     *
     * @param id
     * @return
     */
    @Override
    public Blog getBlogById(Integer id) {
        if (Objects.isNull(id)) {
            return null;
        }

        return blogMapper.selectByIdAndStatus(id, null);
    }

    @Override
    public Result updateBlogCount(Integer id, String userId, String type) {
        if (StringUtils.isEmpty(userId)) {
            return Result.createByErrorMessage("userId为空");
        }
        Blog blog = blogMapper.selectByIdAndStatus(id, DataStatus.NO_DELETED);

        if (!isValidViewOrLike(type + "_blog_" + id)) {
            return resultError();
        }

        if (Objects.isNull(blog)) {
            return Result.createByErrorCodeMessage(ResultCode.PARAMAS_ERROR.getCode(), ResultCode.PARAMAS_ERROR.getDescription());
        }
        int result = blogMapper.updateBlogCount(id, type);
        if (result == 0) {
            return Result.createByError();
        } else {
            return Result.createBySuccess();
        }
    }

    /**
     * 获取首页所需数据
     *
     * @param withBlogs 结果集是否包含博客列表
     * @return
     */
    @Override
    public Result<IndexVO> getIndexVO(boolean withBlogs) {
        //查询推荐或热门博客
        List<BlogDetail4UserVO> recommendList = blogMapper.selectHotOrRecommendBlogs(StatisticType.RECOMMEND_BLOG, 5);
        List<BlogDetail4UserVO> clickRankList = blogMapper.selectHotOrRecommendBlogs(StatisticType.HOT_BLOG, 5);

        // 标签分类信息
        List<TagWithCountVO> tagVOS = tagService.getTagListByTagType(TagType.TAG);
        List<TagWithCountVO> categoryVOS = tagService.getTagListByTagType(TagType.CATEGORY);

        // 博客内容
        IndexVO indexVO = new IndexVO();
        if (withBlogs) {
            BlogListRequest blogListRequest = new BlogListRequest();
            blogListRequest.setStatus(DataStatus.NO_DELETED);
            PageInfo<BlogDetail4UserVO> blogs = getBlogListInfos(blogListRequest).getData();
            indexVO.setBlogList(blogs);
        }

        indexVO.setCategoryList(categoryVOS);
        indexVO.setTagList(tagVOS);
        indexVO.setRecommendList(recommendList);
        indexVO.setClickRankList(clickRankList);

        return Result.createBySuccess(indexVO);
    }

    /**
     * 查询博客详情
     *
     * @param blogId
     * @return
     */
    @Override
    public Result<BlogDetail4AdminVO> getBlogDetailInfo(Integer blogId) {
        Preconditions.checkNotNull(blogId, "博客id不能为null");

        BlogWithBLOBs blogDO = blogMapper.selectByPrimaryKey(blogId);
        if (Objects.isNull(blogDO)) {
            return resultError4Param("博客不存在");
        }
        // 获取博客的标签
        List<Integer> tagIds = tagService.getTagListByBlogId(blogId)
                .stream()
                .map(TagVO::getId)
                .collect(Collectors.toList());

        return resultOk(BlogDetail4AdminVO.createFrom(blogDO, tagIds));
    }

    /**
     * 获取博客的上一篇或者下一篇
     *
     * @param id     当前博客主键id
     * @param isLast 是否是上一篇
     * @return
     */
    private BlogDetail4AdminVO getlastOrNext(Integer id, boolean isLast) {
        Preconditions.checkNotNull(id, "主键id不能为null");

        Blog blog = blogMapper.selectLastOrNext(id, isLast);
        if (Objects.isNull(blog)) {
            return null;
        }

        BlogDetail4AdminVO blogDetail = new BlogDetail4AdminVO();
        blogDetail.setId(blog.getId());
        blogDetail.setTitle(blog.getTitle());
        blogDetail.setCategoryId(blog.getCategoryId());
        return blogDetail;
    }

    private BlogDetail4UserVO convertBlogToVO(BlogWithBLOBs blogDO) {

        BlogDetail4UserVO blogDetail4UserVo = convertBlogDOToVO(blogDO);
        blogDetail4UserVo.setHtmlContent(blogDO.getHtmlContent());
        List<CatalogItem> catalogItems = JsonUtils.jsonArrayToArrayList(blogDO.getBlogCatalog(),
                new TypeToken<ArrayList<CatalogItem>>() {}.getType());
        blogDetail4UserVo.setCatalogs(catalogItems);

        return blogDetail4UserVo;
    }

    private BlogDetail4UserVO convertBlogDOToVO(Blog blog) {
        BlogDetail4UserVO blogDetail4UserVo = new BlogDetail4UserVO();
        blogDetail4UserVo.setCalcTime(DateCalUtils.format(blog.getCreateTime()));
        blogDetail4UserVo.setCreateTime(blog.getCreateTime());
        blogDetail4UserVo.setUpdateTime(blog.getUpdateTime());
        blogDetail4UserVo.setId(blog.getId());
        blogDetail4UserVo.setSummary(blog.getSummary());
        blogDetail4UserVo.setImgUrl(blog.getImgUrl());
        blogDetail4UserVo.setViewCount(blog.getViewCount());
        blogDetail4UserVo.setVoteCount(blog.getVoteCount());
        blogDetail4UserVo.setTitle(blog.getTitle());
        blogDetail4UserVo.setCode(blog.getCode());
        blogDetail4UserVo.setCategoryId(blog.getCategoryId());
        blogDetail4UserVo.setVoteCount(blog.getVoteCount());
        blogDetail4UserVo.setAuthor(blog.getAuthor());

        return blogDetail4UserVo;
    }

    /**
     * 逻辑删除博客
     *
     * @param id 博客主键id
     * @return
     */
    @Override
    public Result<Boolean> deleteBlogInfo(Integer id) {
        Preconditions.checkNotNull(id, "要删除的博客id不能未空");

        Blog blog = getBlogById(id);
        if (Objects.isNull(blog)) {
            return resultOk();
        }

        fillUpdateTime(blog);
        blog.setBlogStatus(DataStatus.DELETED);
        if (blogMapper.updateByPrimaryKey(blog) == 0) {
            return resultError4DB("删除失败");
        }

        return resultOk();
    }

    /**
     *  将Redis博文点赞数存储到数据库，定时任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transVoteDataFromRedis2DB() {
        List<Blog> blogList = redisUtils.getBlogVoteCountFromRedis();
        if (CollectionUtils.isNotEmpty(blogList)) {
            blogMapper.batchInsert(blogList);
        } else {
            log.info("transBlogVoteDataFromRedis2DB, blog list empty");
        }
    }
}
