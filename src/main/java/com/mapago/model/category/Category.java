package com.mapago.model.category;

import com.mapago.model.Audit;
import com.mapago.model.type.Type;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Category extends Audit {
    private Integer categoryId;
    private String categoryNm;
    private Integer dispOrd;
    private String iconImg;
    private Integer typeCnt;
    private Integer typeDetailCnt;
    private Integer typeDetailShopCnt;
    private List<Type> typeList;
    private String topYn;
    private String leftYn;
    private String route;
    private Boolean noCount;
}