package com.mapago.controller.admin.bizcall;

import com.mapago.model.shop.Shop;
import com.mapago.service.bizcall.BizcallService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/bizcall")
public class AdmBizcallController {

    @Autowired
    private BizcallService bizcallService;

    //가상번호 리스트
    @PostMapping("/get_vns")
    public void get_vns(Shop shop) throws Exception {
        bizcallService.get_vns(shop);
        return;
    }

    //가상번호 설정
    @PostMapping("/set_vn")
    public void set_vn(Shop shop) throws Exception {
        bizcallService.set_vn(shop);
        return;
    }


}
