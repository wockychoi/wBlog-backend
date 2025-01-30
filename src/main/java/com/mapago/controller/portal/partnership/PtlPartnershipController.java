package com.mapago.controller.portal.partnership;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapago.model.partnership.Partnership;
import com.mapago.model.user.User;
import com.mapago.service.email.EmailService;
import com.mapago.service.otp.OtpService;
import com.mapago.service.partnership.PartnershipService;
import com.mapago.service.user.UserService;
import com.mapago.util.AesUtil;
import com.mapago.util.JwtUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portal/api/partnership")
public class PtlPartnershipController {

    @Autowired
    private PartnershipService partnershipService;

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestPart(value = "imageFiles", required = true) List<MultipartFile> imageFiles,
                                  @RequestPart(value = "bizLicFile", required = true) List<MultipartFile> bizLicFile,
                                  @RequestPart(value = "data", required = true) String data) throws Exception {

        User userRequest = new ObjectMapper().readValue(data, User.class);
        User validateUser = userService.findByUserId(userRequest.getUserId());
        if (validateUser != null) {
            throw new DataIntegrityViolationException("이미 사용중인 아이디 입니다.[" + userRequest.getUserId() + "]");
        }

        validateUser = userService.findByNickname(userRequest.getNickname());
        if (validateUser != null) {
            throw new DataIntegrityViolationException("이미 사용중인 닉네임 입니다.[" + userRequest.getNickname() + "]");
        }
        String decryptPassword = AesUtil.decrypt(userRequest.getPassword());
        userRequest.setPassword(passwordEncoder.encode(decryptPassword));
        userRequest.setRoles(List.of("ROLE_USER"));

        Partnership partnership = new ObjectMapper().readValue(data, Partnership.class);
        partnershipService.insertUserPartnership(userRequest, partnership, imageFiles, bizLicFile);
        User user = userService.findByUserId(userRequest.getUserId());
        String accessToken = jwtUtil.generatePortalAccessToken(user.getUserId());
        String refreshToken = jwtUtil.generatePortalRefreshToken(user.getUserId());
        user.setRoles(List.of("ROLE_USER"));

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("user", user);

        return ResponseEntity.ok(response);

    }

}