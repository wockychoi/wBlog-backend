package com.mapago.mapper.user;

import com.mapago.model.user.PostingLog;
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

    void insertWallet(User user);

    Integer updateUser(User user);

    Integer updatePointUser(User user);

    void insertUserRole(User userRequest);

    void insertPostingLog(PostingLog postingLog);

    void insertPointlog(PostingLog postingLog);

    Integer updatePointWallet(User user);

    Integer savePostKey(User user);
}