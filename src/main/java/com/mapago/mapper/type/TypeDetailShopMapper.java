package com.mapago.mapper.type;

import com.mapago.model.type.TypeDetailShop;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TypeDetailShopMapper {

    List<TypeDetailShop> findTypeDetailShopListWithInfos(TypeDetailShop typeDetailShop);

    Integer insertTypeDetailShop(TypeDetailShop typeDetailShop);

    Integer deleteTypeDetailShop(TypeDetailShop typeDetailShop);

    Integer sortTypeDetailShop(TypeDetailShop typeDetailShop);

    List<TypeDetailShop> findDisplayShopList(TypeDetailShop typeDetailShop);

    List<TypeDetailShop> findDisplayRecommendShopList(TypeDetailShop typeDetailShop);

    List<TypeDetailShop> findRecommendShopList(TypeDetailShop typeDetailShop);
}