package com.mapago.service.user;

import com.mapago.mapper.user.UserLogMapper;
import com.mapago.model.user.UserLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserLogService {

    private final UserLogMapper userLogMapper;

    public UserLogService(UserLogMapper userLogMapper) {
        this.userLogMapper = userLogMapper;
    }

    @Transactional
    public UserLog insertUserLog(UserLog userLog) throws Exception {
        userLog.setUserId(UserService.getCurrentUserId());
        userLogMapper.insertUserLog(userLog);
        return userLog;
    }

}