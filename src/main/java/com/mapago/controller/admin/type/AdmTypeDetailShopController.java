package com.mapago.controller.admin.type;

import com.mapago.model.type.TypeDetailShop;
import com.mapago.service.type.TypeDetailShopService;
import java.util.List;
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
@RequestMapping("/admin/api/type/detail/shop")
public class AdmTypeDetailShopController {

    @Autowired
    private TypeDetailShopService typeDetailShopService;

    @GetMapping("/listWithInfos")
    public ResponseEntity<?> listWithInfos(TypeDetailShop typeDetailShop) throws Exception {
        return ResponseEntity.ok(typeDetailShopService.findTypeDetailShopListWithInfos(typeDetailShop));
    }

    @PostMapping("/adds")
    public ResponseEntity<?> add(@RequestBody List<TypeDetailShop> typeDetailShopList) throws Exception {
        return ResponseEntity.ok(typeDetailShopService.insertTypeDetailShops(typeDetailShopList));
    }

    @PostMapping("/remove")
    public ResponseEntity<?> remove(@RequestBody TypeDetailShop typeDetailShop) throws Exception {
        return ResponseEntity.ok(typeDetailShopService.deleteTypeDetailShop(typeDetailShop));
    }

    @PostMapping("/sort")
    public ResponseEntity<?> sort(@RequestBody List<TypeDetailShop> typeDetailShopList) throws Exception {
        return ResponseEntity.ok(typeDetailShopService.sortTypeDetailShopList(typeDetailShopList));
    }

}
