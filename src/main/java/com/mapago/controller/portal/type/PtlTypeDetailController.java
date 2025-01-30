package com.mapago.controller.portal.type;

import com.mapago.model.type.TypeDetail;
import com.mapago.service.type.TypeDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portal/api/type/detail")
public class PtlTypeDetailController {

    @Autowired
    private TypeDetailService typeDetailService;

    @GetMapping("/list")
    public ResponseEntity<?> list(TypeDetail typeDetail) throws Exception {
        return ResponseEntity.ok(typeDetailService.findTypeDetailList(typeDetail));
    }

}
