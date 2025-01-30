package com.mapago.model.shop;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mapago.model.Audit;
import com.mapago.model.file.File;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Shop extends Audit {
    private Integer shopId;
    private String shopNm;
    private String shopTitle;
    private String shopTitleColor;
    private String content;
    private Integer areaProvinceId;
    private String areaProvinceNm;
    private Integer areaCityId;
    private String areaCityNm;
    private Integer areaStreetId;
    private String areaStreetNm;
    private String shopType;
    private String phoneNum;
    private String address;
    private String dtlAddress;
    private String operationTime;
    private String email;
    private Integer viewCnt;
    private Integer likeCnt;
    private String closeYn;
    private String fileIdList;
    private String commentIdList;
    private String oneOwnerYn;
    private Boolean oneOwner;
    private String manOnlyYn;
    private Boolean manOnly;
    private String womanOnlyYn;
    private Boolean womanOnly;
    private String useYn;
    private List<File> fileList;
    private String firstFileUrl;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String phoneBizNum;
    private LocalDateTime refreshDt;
    private String typeDetailNmList;
}