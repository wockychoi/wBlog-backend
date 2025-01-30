package com.mapago.service.area;


import com.mapago.mapper.area.AreaMapper;
import com.mapago.model.area.AreaCity;
import com.mapago.model.area.AreaProvince;
import com.mapago.model.area.AreaStreet;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AreaService {

    private final AreaMapper areaMapper;

    public AreaService(AreaMapper areaMapper) {
        this.areaMapper = areaMapper;
    }

    public List<AreaProvince> getAreaProvinceList(AreaProvince areaProvince) throws Exception {
        return areaMapper.findAreaProvinceList(areaProvince);
    }

    public List<AreaCity> getAreaCityList(AreaCity areaCity) throws Exception {
        return areaMapper.findAreaCityList(areaCity);
    }

    public List<AreaStreet> getAreaStreetList(AreaStreet areaStreet) throws Exception {
        return areaMapper.findAreaStreetList(areaStreet);
    }

}