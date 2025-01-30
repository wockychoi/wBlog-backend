package com.mapago.model.area;

import com.mapago.model.Audit;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AreaStreet extends Audit {
    private Integer areaStreetId;
    private String areaStreetNm;
    private Integer areaCityId;
}
