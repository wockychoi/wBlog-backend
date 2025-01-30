package com.mapago.mapper.user;

import com.mapago.model.user.UserLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserLogMapper {

    Integer insertUserLog(UserLog userLog);

}