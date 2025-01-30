package com.mapago.model.otp;

import com.mapago.model.Audit;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@Data
@EqualsAndHashCode(callSuper = true)
public class Otp extends Audit {
    private Integer otpId;
    private Integer otpValue;
    private String objId;
    private String confirmYn;
}