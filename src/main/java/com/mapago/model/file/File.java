package com.mapago.model.file;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mapago.model.Audit;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class File extends Audit {
    private Integer fileId;
    private String sysFileName;
    private String userFileName;
    private String filePath;
    private Integer fileSize;
    private String fileExt;
    private String fileUrl;
}
