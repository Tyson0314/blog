package com.dabin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dabin.common.base.Result;
import com.dabin.common.constants.GenderConstant;
import com.dabin.common.constants.RedisConstant;
import com.dabin.common.constants.SystemConstant;
import com.dabin.common.utils.FileUtils;
import com.dabin.common.utils.JsonUtils;
import com.dabin.common.utils.RedisUtils;
import com.dabin.common.utils.SnowFlakeWorker;
import com.dabin.dao.UserMapper;
import com.dabin.entity.User;
import com.dabin.entity.UserExample;
import com.dabin.service.AuthService;
import com.dabin.vo.FileVO;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthGithubRequest;
import me.zhyd.oauth.request.AuthRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author: 程序员大彬
 * @time: 2021-11-09 23:17
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    RedisUtils redisUtils;

    @Value(value = "${justAuth.clientId.gitee}")
    private String giteeClienId;

    @Value(value = "${justAuth.clientSecret.gitee}")
    private String giteeClientSecret;

    @Value(value = "${justAuth.clientId.github}")
    private String githubClienId;

    @Value(value = "${justAuth.clientSecret.github}")
    private String githubClientSecret;


    @Value(value = "${webSite.url}")
    private String webSiteUrl;

    @Value(value = "${blog.web.url}")
    private String blogUrl;

    @Resource
    private UserMapper userMapper;

    @Autowired
    private FileUtils fileUtils;

    @Override
    public Result<Map<String, Object>> verify(String token) {
        String userInfo = redisUtils.get(RedisConstant.TOKEN + token);
        log.info("verify:{}", userInfo);
        if (StringUtils.isEmpty(userInfo)) {
            return Result.createByErrorMessage("token无效");
        } else {
            Map<String, Object> map = JsonUtils.jsonToMap(userInfo);
            return Result.createBySuccess(map);
        }
    }

    @Override
    public void callback(String source, AuthCallback callback, HttpServletResponse httpServletResponse) throws IOException {
        AuthRequest authRequest = getAuthRequest(source);
        AuthResponse response = authRequest.login(callback);
        if (response.getCode() == SystemConstant.RESULT_CODE_5000) {
            // 跳转到500错误页面
            httpServletResponse.sendRedirect(webSiteUrl + SystemConstant.ERROR_500);
            return;
        }
        String result = JSONObject.toJSONString(response);
        Map<String, Object> map = JsonUtils.jsonToMap(result);
        Map<String, Object> data = JsonUtils.jsonToMap(JsonUtils.objectToJson(map.get(SystemConstant.DATA)));
        log.info("data:{}", data);
        Map<String, Object> token;
        String accessToken;
        if (data == null || data.get(SystemConstant.TOKEN) == null) {
            // 跳转到500错误页面
            httpServletResponse.sendRedirect(webSiteUrl + SystemConstant.ERROR_500);
            return;
        } else {
            token = JsonUtils.jsonToMap(JsonUtils.objectToJson(data.get(SystemConstant.TOKEN)));
            accessToken = token.get(SystemConstant.ACCESS_TOKEN).toString();
        }

        //用户信息
        Boolean exist = false;
        User user;
        //判断user是否存在
        if (data.get(SystemConstant.UUID) != null && data.get(SystemConstant.SOURCE) != null) {
            UserExample userExample = new UserExample();
            UserExample.Criteria criteria = userExample.createCriteria();
            criteria.andSourceEqualTo(data.get(SystemConstant.SOURCE).toString())
                    .andUuidEqualTo(data.get(SystemConstant.UUID).toString());
            List<User> userList = userMapper.selectByExample(userExample);
            if (CollectionUtils.isNotEmpty(userList)) {
                exist = true;
                user = userList.get(0);
            } else {
                user = new User();
            }
        } else {
            return;
        }

        // 判断邮箱是否存在
        if (data.get(SystemConstant.EMAIL) != null) {
            String email = data.get(SystemConstant.EMAIL).toString();
            user.setUserEmail(email);
        }

        // 判断用户性别
        if (data.get(SystemConstant.GENDER) != null && !exist) {
            String gender = data.get(SystemConstant.GENDER).toString();
            if (GenderConstant.MALE.equals(gender)) {
                user.setGender(GenderConstant.MALE);
            } else if (GenderConstant.FEMALE.equals(gender)) {
                user.setGender(GenderConstant.FEMALE);
            } else {
                user.setGender(GenderConstant.UNKNOWN);
            }
        }

        if (data.get(SystemConstant.NICK_NAME) != null) {
            user.setNickName(data.get(SystemConstant.NICK_NAME).toString());
        }

        String avatarUrl;
        if (exist) {
            if (StringUtils.isEmpty(user.getAvatarUrl())) {
                //头像信息
                avatarUrl = getAvatarUrl(data, user.getId());
                user.setAvatarUrl(avatarUrl);
            }
            userMapper.updateByPrimaryKeySelective(user);
        } else {
            SnowFlakeWorker idWorker = new SnowFlakeWorker(0, 0);
            user.setId(idWorker.nextId() + "");
            user.setUuid(data.get(SystemConstant.UUID).toString());
            user.setSource(data.get(SystemConstant.SOURCE).toString());
            String userName = user.getSource().concat(SystemConstant.UNDERLINE).concat(user.getUuid());
            user.setUserName(userName);
            // 如果昵称为空，那么直接设置用户名
            if (StringUtils.isEmpty(user.getNickName())) {
                user.setNickName(userName);
            }
            //头像信息
            avatarUrl = getAvatarUrl(data, user.getId());
            if (StringUtils.isNotEmpty(avatarUrl)) {
                user.setAvatarUrl(avatarUrl);
            }
            userMapper.insertSelective(user);
        }

        if (user != null) {
            //将从数据库查询的数据缓存到redis中
            //token7天有效期
            redisUtils.set(RedisConstant.TOKEN + accessToken, JsonUtils.objectToJson(user), 60 * 60 * 24 * 7);
        }

        httpServletResponse.sendRedirect(webSiteUrl + "?token=" + accessToken);
    }

    private String getAvatarUrl(Map<String, Object> data, String userId) {
        String avatarUrl = "";
        if (data.get(SystemConstant.AVATAR) != null) {
            FileVO fileVO = new FileVO();
            fileVO.setUserId(userId);
            fileVO.setUrl(data.get(SystemConstant.AVATAR).toString());
            avatarUrl = fileUtils.uploadPicture(fileVO);
        }
        log.info("avatar url:{}", avatarUrl);
        return avatarUrl;
    }

    public AuthRequest getAuthRequest(String source) {
        log.info("source:{}", source);
        AuthRequest authRequest = null;
        switch (source.toLowerCase()) {
            case SystemConstant.GITEE:
                authRequest = new AuthGiteeRequest(AuthConfig.builder()
                        .clientId(giteeClienId)
                        .clientSecret(giteeClientSecret)
                        .redirectUri(blogUrl + "/oauth/callback/gitee")
                        .build());
                break;
            case SystemConstant.GITHUB:
                authRequest = new AuthGithubRequest(AuthConfig.builder()
                        .clientId(githubClienId)
                        .clientSecret(githubClientSecret)
                        .redirectUri(blogUrl + "/oauth/callback/github")
                        .build());
                break;
            default:
                break;
        }
        if (null == authRequest) {
            throw new AuthException("不存在的认证方式");
        }
        return authRequest;
    }
}
