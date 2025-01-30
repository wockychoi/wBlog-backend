package com.mapago.service.otp;

import com.mapago.mapper.otp.OtpMapper;
import com.mapago.model.otp.Otp;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    @Autowired
    private OtpMapper otpMapper;

    public Otp findOtp(Otp otp) throws Exception {
        return otpMapper.findOtp(otp);
    }

    public Otp insertOtp(Otp otp) throws Exception {
        otp.setOtpValue(generateOtp());
        otpMapper.insertOtp(otp);
        return otp;
    }

    public Integer generateOtp() throws Exception {
        return 100000 + new Random().nextInt(900000);
    }

    public Boolean certifyOtp(Otp otp) throws Exception {
        return Optional.ofNullable(findOtp(otp))
                .map(foundOtp -> Objects.equals(otp.getOtpValue(), foundOtp.getOtpValue()))
                .orElse(false);
    }
}