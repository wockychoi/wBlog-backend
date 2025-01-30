package com.mapago.controller.admin.commonCode;

import com.mapago.model.commonCode.CommonCode;
import com.mapago.service.commonCode.CommonCodeService;
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
@RequestMapping("/admin/api/common-code")
public class AdmCommonCodeController {

    @Autowired
    private CommonCodeService commonCodeService;

    @GetMapping("/list")
    public ResponseEntity<?> list(CommonCode commonCode) throws Exception {
        return ResponseEntity.ok(commonCodeService.getCommonCodeList(commonCode));
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody CommonCode commonCode) throws Exception {
        return ResponseEntity.ok(commonCodeService.insertCommonCode(commonCode));
    }

    @PostMapping("/modify")
    public ResponseEntity<?> modify(@RequestBody CommonCode commonCode) throws Exception {
        return ResponseEntity.ok(commonCodeService.updateCommonCode(commonCode));
    }

    @PostMapping("/remove")
    public ResponseEntity<?> remove(@RequestBody CommonCode commonCode) throws Exception {
        return ResponseEntity.ok(commonCodeService.deleteCommonCode(commonCode));
    }

    @PostMapping("/sort")
    public ResponseEntity<?> sort(@RequestBody List<CommonCode> commonCodeList) throws Exception {
        return ResponseEntity.ok(commonCodeService.sortCommonCodeList(commonCodeList));
    }

}
