package com.mapago.controller.admin.sms;

import com.mapago.model.otp.Otp;
import com.mapago.model.sms.Sms;
import com.mapago.service.otp.OtpService;
import com.mapago.service.sms.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/sms")
public class AdmSmsController {

    @Autowired
    private SmsService smsService;

    @Autowired
    private OtpService otpService;

    @PostMapping("/smsOtp")
    public ResponseEntity<?> smsOtp(@RequestBody Sms sms) throws Exception {
        Otp otp = Otp.builder()
                .objId(sms.getToNumber())
                .build();
        otpService.insertOtp(otp);
        sms.setSubject("MAPAGO 인증번호");
        sms.setContent(String.format("MAPAGO 인증번호 [%d]", otp.getOtpValue()));
        smsService.sendSms(sms);

        return ResponseEntity.ok(sms);
    }


}
