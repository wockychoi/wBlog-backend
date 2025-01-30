package com.mapago.mapper.otp;

import com.mapago.model.otp.Otp;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OtpMapper {
    Otp findOtp(Otp otp);

    void insertOtp(Otp otp);
}