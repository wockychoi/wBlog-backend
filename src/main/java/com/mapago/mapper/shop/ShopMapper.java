package com.mapago.mapper.shop;

import com.mapago.model.shop.Shop;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShopMapper {
    List<Shop> findShopList(Shop shop);

    List<Shop> findShopListAll();

    void insertShop(Shop shop);

    Integer updateShop(Shop shop);

    void updateShopFileIdList(Shop shop);

    Integer deleteShop(Shop shop);

    Integer sortShopFileList(Shop shop);

    Integer shopBizCall(Shop shop);

    Shop findShop(Shop shop);

    List<Shop> findOwnerShopList(String ownerUserId);

    Integer updateRefreshDt(Shop shop);

}