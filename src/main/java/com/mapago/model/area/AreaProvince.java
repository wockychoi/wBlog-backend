package com.mapago.model.area;

import com.mapago.model.Audit;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AreaProvince extends Audit {
    private Integer areaProvinceId;
    private String areaProvinceNm;
    private List<AreaCity> areaCityList;
}
