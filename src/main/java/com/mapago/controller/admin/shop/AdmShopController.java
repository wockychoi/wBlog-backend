package com.mapago.controller.admin.shop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapago.model.shop.Shop;
import com.mapago.service.shop.ShopService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/shop")
public class AdmShopController {

    @Autowired
    private ShopService shopService;

    @GetMapping("/list")
    public ResponseEntity<?> list(Shop shop) throws Exception {
        return ResponseEntity.ok(shopService.findShopList(shop));
    }

    @GetMapping("/listAll")
    public ResponseEntity<?> listAll() throws Exception {
        return ResponseEntity.ok(shopService.findShopListAll());
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestPart("shop") String shopData,
                                 @RequestPart(value = "files", required = true) List<MultipartFile> files)
            throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Shop shop = objectMapper.readValue(shopData, Shop.class);

        return ResponseEntity.ok(shopService.insertShop(shop, files));
    }

    @PostMapping("/modify")
    public ResponseEntity<?> modify(@RequestPart("shop") String shopData,
                                    @RequestPart(value = "files", required = false) List<MultipartFile> files)
            throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Shop shop = objectMapper.readValue(shopData, Shop.class);

        return ResponseEntity.ok(shopService.updateShop(shop, files));
    }

    @PostMapping("/remove")
    public ResponseEntity<?> remove(@RequestBody Shop shop) throws Exception {
        return ResponseEntity.ok(shopService.deleteShop(shop));
    }

    @PostMapping("/sort")
    public ResponseEntity<?> sort(@RequestBody Shop shop) throws Exception {
        return ResponseEntity.ok(shopService.sortShopFileList(shop));
    }


}
