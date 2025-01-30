package com.mapago.model.area;

import com.mapago.model.Audit;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AreaCity extends Audit {
    private Integer areaCityId;
    private String areaCityNm;
    private Integer areaProvinceId;
    private List<AreaStreet> areaStreetList;
}
