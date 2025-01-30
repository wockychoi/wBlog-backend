package com.mapago.mapper.area;

import com.mapago.model.area.AreaCity;
import com.mapago.model.area.AreaProvince;
import com.mapago.model.area.AreaStreet;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AreaMapper {
    List<AreaProvince> findAreaProvinceList(AreaProvince areaProvince);

    List<AreaCity> findAreaCityList(AreaCity areaCity);

    List<AreaStreet> findAreaStreetList(AreaStreet areaStreet);
}