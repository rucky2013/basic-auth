package com.github.ququzone.basicauth.service;

import com.github.ququzone.basicauth.model.Resource;
import com.github.ququzone.basicauth.model.User;
import com.github.ququzone.basicauth.model.UserFact;
import com.github.ququzone.basicauth.model.UserVO;
import com.github.ququzone.basicauth.persistence.ResourceMapper;
import com.github.ququzone.basicauth.persistence.UserFactMapper;
import com.github.ququzone.basicauth.persistence.UserMapper;
import com.github.ququzone.common.MD5;
import com.github.ququzone.common.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.management.counter.perf.PerfInstrumentation;

/**
 * auth service implement.
 *
 * @author Yang XuePing
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserFactMapper userFactMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Value("${password.salt}")
    private String salt;

    @Override
    public User login(String username, String password) {
        password = MD5.digestHexString(salt, password);
        User user = userMapper.findByUsernameAndPassword(username, password);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        if (user.getStatus() != User.Status.NORMAL) {
            throw new ServiceException("用户状态异常");
        }
        return user;
    }

    @Override
    public boolean auditing(String userId, String pattern) {
        Resource resource = resourceMapper.findByPattern(pattern);
        if (resource == null || resource.getStatus() != Resource.Status.NORMAL) {
            return true;
        }
        return resourceMapper.countByUserId(userId, resource.getId()) > 0;
    }

    @Override
    public UserVO getUserVO(String userId) {
        User user = userMapper.find(userId);
        if (user != null) {
            UserVO result = new UserVO();
            result.setId(user.getId());
            result.setUsername(user.getUsername());
            UserFact userFact = userFactMapper.findByUserIdAndName(user.getId(), UserFact.Field.DISPLAY_NAME);
            if (userFact != null) {
                result.setDisplayName(userFact.getValue());
            }
            return result;
        }
        return null;
    }
}
