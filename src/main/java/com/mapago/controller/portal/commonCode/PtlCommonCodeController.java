package com.mapago.controller.portal.commonCode;

import com.mapago.model.commonCode.CommonCode;
import com.mapago.service.commonCode.CommonCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portal/api/common-code")
public class PtlCommonCodeController {

    @Autowired
    private CommonCodeService commonCodeService;

    @GetMapping("/list")
    public ResponseEntity<?> list(CommonCode commonCode) throws Exception {
        return ResponseEntity.ok(commonCodeService.getCommonCodeList(commonCode));
    }

}
