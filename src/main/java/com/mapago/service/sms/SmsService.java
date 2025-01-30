package com.mapago.service.sms;

import com.mapago.model.sms.Sms;
import com.mapago.service.otp.OtpService;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Autowired
    private OtpService otpService;

    final DefaultMessageService messageService;

    private SmsService() {
        // 반드시 계정 내 등록된 유효한 API 키, API Secret Key를 입력해주셔야 합니다!
        this.messageService = NurigoApp.INSTANCE.initialize("NCSP9WXAA7B74VFB", "NOLIK1AZ9B50VARXC9OCJBYLBXAAPFPB",
                "https://api.coolsms.co.kr");
    }

    public Sms sendSms(Sms sms) {
        Message message = new Message();
        // 발신번호 및 수신번호는 반드시 01012345678 형태로 입력되어야 합니다.
        message.setFrom("01074844377");
        message.setTo(sms.getToNumber());
        message.setText(sms.getContent());
        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        System.out.println(response);

        return sms;
    }
}