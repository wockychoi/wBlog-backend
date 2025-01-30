package com.mapago.mapper.user;

import com.mapago.model.user.User;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User findAuth(String userId);

    User findByUserId(String userId);

    User findByNickname(String nickname);

    List<User> getUserList(User user);

    List<String> findRolesByUserId(String userId);

    void insertUser(User user);

    Integer updateUser(User user);

    void insertUserRole(User userRequest);

}