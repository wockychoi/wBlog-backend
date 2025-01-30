package com.mapago.controller.portal.area;

import com.mapago.model.area.AreaCity;
import com.mapago.model.area.AreaProvince;
import com.mapago.model.area.AreaStreet;
import com.mapago.service.area.AreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portal/api/area")
public class PtlAreaController {

    @Autowired
    private AreaService areaService;

    @GetMapping("/province/list")
    public ResponseEntity<?> provinceList(AreaProvince AreaProvince) throws Exception {
        return ResponseEntity.ok(areaService.getAreaProvinceList(AreaProvince));
    }

    @GetMapping("/city/list")
    public ResponseEntity<?> cityList(AreaCity areaCity) throws Exception {
        return ResponseEntity.ok(areaService.getAreaCityList(areaCity));
    }

    @GetMapping("/street/list")
    public ResponseEntity<?> streetList(AreaStreet areaStreet) throws Exception {
        return ResponseEntity.ok(areaService.getAreaStreetList(areaStreet));
    }

}
