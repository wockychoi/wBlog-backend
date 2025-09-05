package com.mapago.model.user;

import com.mapago.model.Audit;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PostingLog extends Audit {
    private Integer postJobId;
    private String userId;
    private String blogAccountId;
    private String title;
    private String content;
    private String status;
    private String retryCount;
    private String executedAt;
    private String createdAt;
    private Integer historyId;
    private String amount;
    private String type;
    private String description;
}