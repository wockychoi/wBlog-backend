package com.mapago.model.sample;

import com.mapago.model.Audit;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Sample extends Audit {
    private Integer sampleId;
    private String sampleNm;

    private Integer fileId;
    private String sysFileNameM;
    private String userFileNameM;
    private String filePathM;
    private Integer fileSizeM;
    private String fileExtM;

    private String shopId;
    private String fileIdList;
}
