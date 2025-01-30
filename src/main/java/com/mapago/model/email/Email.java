package com.mapago.model.email;

import com.mapago.model.Audit;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@Data
@EqualsAndHashCode(callSuper = true)
public class Email extends Audit {
    private Integer emailId;
    private String address;
    private String subject;
    private String content;
}