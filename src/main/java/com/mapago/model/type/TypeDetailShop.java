package com.mapago.model.type;

import com.mapago.model.Audit;
import com.mapago.model.shop.Shop;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TypeDetailShop extends Audit {
    private Integer typeDetailShopId;
    private Integer typeDetailId;
    private Integer shopId;
    private Integer dispOrd;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private Shop shop;

    private Integer typeId;
    private String typeDetailNmList;
    private String search;
}