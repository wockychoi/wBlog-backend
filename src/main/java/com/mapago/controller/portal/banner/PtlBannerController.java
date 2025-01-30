package com.mapago.controller.portal.banner;

import com.mapago.model.banner.Banner;
import com.mapago.service.banner.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portal/api/banner")
public class PtlBannerController {

    @Autowired
    private BannerService bannerService;

    @GetMapping("/list")
    public ResponseEntity<?> list(Banner banner) throws Exception {
        return ResponseEntity.ok(bannerService.getBannerList(banner));
    }

}
