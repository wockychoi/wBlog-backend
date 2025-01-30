package com.mapago.model.user;

import com.mapago.model.Audit;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserLog extends Audit {
    private Integer userLogId;
    private String userId;
    private String logType;
    private String objId;
    private String ip;
}