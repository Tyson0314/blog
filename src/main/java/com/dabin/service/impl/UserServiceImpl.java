package com.dabin.service.impl;

import com.dabin.common.base.BaseService;
import com.dabin.common.base.Result;
import com.dabin.common.security.SecurityUserDetail;
import com.dabin.common.utils.JwtTokenUtil;
import com.dabin.dao.UserMapper;
import com.dabin.dto.UserLoginRequest;
import com.dabin.entity.User;
import com.dabin.entity.UserExample;
import com.dabin.service.UserService;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 访客信息管理servcie
 *
 * @author 大彬
 * @date 2021-07-05 23:30
 **/
@Service
@Slf4j
public class UserServiceImpl extends BaseService implements UserService {

    @Resource
    private UserMapper userMapper;

    @Value("${user.username}")
    private String username;

    @Value("${user.password}")
    private String password;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 根据邮箱获取访客信息
     * @param visitorEmail
     * @return
     */
    @Override
    public User getUserDOByEmail(String visitorEmail){
        Preconditions.checkArgument(StringUtils.isNotEmpty(visitorEmail), "邮箱不能为空");

        UserExample visitorExample = new UserExample();
        visitorExample.createCriteria().andUserEmailEqualTo(visitorEmail);
        List<User> visitorDOS = userMapper.selectByExample(visitorExample);
        if (CollectionUtils.isNotEmpty(visitorDOS)) {
            return visitorDOS.get(0);
        }
        return null;
    }

    @Override
     public Result<Boolean> createUserInfo(User user){
        Preconditions.checkNotNull(user);
        if (StringUtils.isEmpty(user.getUserEmail()) || StringUtils.isEmpty(user.getUserName())) {
            return resultError4Param("添加访客失败,参数" + user.toString());
        }
        if (userMapper.insert(user) == 0) {
            return resultError4DB("添加访客失败,参数" + user.toString());
        }
        return resultOk();
     }

    @Override
    public Result<Boolean> updateVisitorInfo(User user){
        Preconditions.checkNotNull(user);
        if (StringUtils.isEmpty(user.getUserEmail()) || StringUtils.isEmpty(user.getUserName())) {
            return resultError4Param("更新访客失败,参数" + user.toString());
        }
        if (userMapper.updateByPrimaryKeySelective(user) == 0) {
            return resultError4DB("更用户信息失败,参数" + user.toString());
        }
        return resultOk();
    }

    @Override
    public Result<String> login(UserLoginRequest userLoginRequest) {
        System.out.println(username + password);
        User user = getUserById(userLoginRequest.getUserName());
        if (user == null || !StringUtils.equals(userLoginRequest.getPassword(), user.getPassword())) {
            return Result.createByErrorMessage("用户名或密码错误");
        }
        // 2.2 将合法的权限重新赋值到用户信息中，生成token并返回
        SecurityUserDetail securityUserDetail = new SecurityUserDetail();
        securityUserDetail.setId(1L);
        securityUserDetail.setUserName("大彬");
        securityUserDetail.setRole("ROLE_ADMIN");
        String token = jwtTokenUtil.generateToken(securityUserDetail);
        return resultOk(token);
    }

    @Override
    public Result edit(User user) {
        log.info("edit user:{}", user);
        userMapper.updateByPrimaryKeySelective(user);
        return resultOk();
    }

    @Override
    public User getUserById(String userId) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdEqualTo(userId);
        List<User> userList = userMapper.selectByExample(userExample);
        if (CollectionUtils.isNotEmpty(userList)) {
            return userList.get(0);
        }
        return null;
    }
}
