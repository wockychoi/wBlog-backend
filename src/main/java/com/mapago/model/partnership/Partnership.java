package com.mapago.model.partnership;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mapago.model.Audit;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class Partnership extends Audit {
    private String userId;
    private String partnerYn;
    private String bizLicFileId;
    private String fileIdList;
}