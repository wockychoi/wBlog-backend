package com.mapago.controller.portal.user;

import com.mapago.model.user.UserLog;
import com.mapago.service.user.UserLogService;
import com.mapago.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portal/api/user/log")
public class PtlUserLogController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserLogService userLogService;

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody UserLog userLog) throws Exception {
        return ResponseEntity.ok(userLogService.insertUserLog(userLog));
    }

}