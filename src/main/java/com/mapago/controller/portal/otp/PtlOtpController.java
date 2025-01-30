package com.mapago.controller.portal.otp;

import com.mapago.model.email.Email;
import com.mapago.model.otp.Otp;
import com.mapago.service.email.EmailService;
import com.mapago.service.otp.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portal/api/otp")
public class PtlOtpController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/emailOtp")
    public ResponseEntity<?> emailOtp(@RequestBody Email email) throws Exception {
        Otp otp = Otp.builder()
                .objId(email.getAddress())
                .build();
        otpService.insertOtp(otp);
        email.setSubject(String.format("MAPAGO 인증번호 [%d]", otp.getOtpValue()));
        email.setContent(String.format("%d", otp.getOtpValue()));
        emailService.sendEmail(email);
        return ResponseEntity.ok(otp.getOtpId());
    }

    @PostMapping("/certifyOtp")
    public ResponseEntity<?> certifyOtp(@RequestBody Otp otp) throws Exception {
        return ResponseEntity.ok(otpService.certifyOtp(otp));
    }

}