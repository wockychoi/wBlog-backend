package com.mapago.service.user;

import com.mapago.config.exception.CustomException;
import com.mapago.mapper.user.UserMapper;
import com.mapago.model.user.PostingLog;
import com.mapago.model.user.User;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        // 데이터베이스에서 사용자 정보 조회
        User user = userMapper.findAuth(userId);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with userId: " + userId);
        }

        // 사용자의 롤 정보 조회
        List<String> roles = userMapper.findRolesByUserId(userId);

        // 롤을 GrantedAuthority로 변환
        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.getUserId(), user.getPassword(),
                authorities);
    }

    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(
                authentication.getName())) {
            return "";
        }
        return authentication.getName();
    }

    public List<User> getUserList(User user) throws Exception {
        return userMapper.getUserList(user);
    }

    public User findByUserId(String userId) {
        return userMapper.findByUserId(userId);
    }

    public User findByNickname(String nickname) {
        return userMapper.findByNickname(nickname);
    }

    @Transactional
    public User updateUser(User user) throws Exception {
        int result = userMapper.updateUser(user);
        int resultpoint = userMapper.updatePointUser(user);
        return Optional.ofNullable(user)
                .filter(t -> result > 0)
                .orElseThrow(() -> new CustomException("삭제할 사용자가 없습니다."));
    }

    public User insertUser(User userRequest) throws Exception {
        userMapper.insertUser(userRequest);
        userMapper.insertWallet(userRequest);
        return userRequest;
    }

    public void insertUserRole(User userRequest) {
        for (String roleName : userRequest.getRoles()) {
            userRequest.setRoleName(roleName);
            userMapper.insertUserRole(userRequest);
        }

    }

    public PostingLog insertPostingLog(PostingLog postingLog) throws Exception {
        userMapper.insertPostingLog(postingLog);
        userMapper.insertPointlog(postingLog);
        return postingLog;
    }

    @Transactional
    public User updatePointWallet(User user) throws Exception {
        int result = userMapper.updatePointWallet(user);
        return userMapper.findByUserId(user.getUserId());
    }

    @Transactional
    public User savePostKey(User user) throws Exception {
        int result = userMapper.savePostKey(user);
        return userMapper.findByUserId(user.getUserId());
    }
}