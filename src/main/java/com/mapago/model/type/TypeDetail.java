package com.mapago.model.type;

import com.mapago.model.Audit;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TypeDetail extends Audit {
    private Integer typeDetailId;
    private String typeDetailNm;
    private Integer typeId;
    private Integer dispOrd;
    private Type type;
    private List<TypeDetailShop> typeDetailShopList;
}