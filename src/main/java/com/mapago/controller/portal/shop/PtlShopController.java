package com.mapago.controller.portal.shop;

import com.mapago.model.shop.Shop;
import com.mapago.model.type.TypeDetailShop;
import com.mapago.model.user.User;
import com.mapago.service.shop.ShopService;
import com.mapago.service.type.TypeDetailShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portal/api/shop")
public class PtlShopController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private TypeDetailShopService typeDetailShopService;

    @GetMapping("/list")
    public ResponseEntity<?> list(Shop shop) throws Exception {
        return ResponseEntity.ok(shopService.findShopList(shop));
    }

    @GetMapping("/findDisplayShopList")
    public ResponseEntity<?> findDisplayShopList(TypeDetailShop typeDetailShop) throws Exception {
        return ResponseEntity.ok(typeDetailShopService.findDisplayShopList(typeDetailShop));
    }

    @GetMapping("/findRecommendShopList")
    public ResponseEntity<?> findRecommendShopList(TypeDetailShop typeDetailShop) throws Exception {
        return ResponseEntity.ok(typeDetailShopService.findRecommendShopList(typeDetailShop));
    }

    @GetMapping("/findDisplayShop")
    public ResponseEntity<?> findDisplayShop(Shop shop) throws Exception {
        return ResponseEntity.ok(shopService.findShop(shop));
    }

    @PostMapping("/jump")
    public ResponseEntity<?> jump(@RequestBody Shop shop) throws Exception {
        return ResponseEntity.ok(shopService.updateRefreshDt(shop));
    }

}
