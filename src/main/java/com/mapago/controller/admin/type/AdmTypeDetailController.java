package com.mapago.controller.admin.type;

import com.mapago.model.type.TypeDetail;
import com.mapago.service.type.TypeDetailService;
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
@RequestMapping("/admin/api/type/detail")
public class AdmTypeDetailController {

    @Autowired
    private TypeDetailService typeDetailService;

    @GetMapping("/list")
    public ResponseEntity<?> list(TypeDetail typeDetail) throws Exception {
        return ResponseEntity.ok(typeDetailService.findTypeDetailList(typeDetail));
    }

    @GetMapping("/listWithInfos")
    public ResponseEntity<?> listWithInfos() throws Exception {
        return ResponseEntity.ok(typeDetailService.findTypeDetailListWithInfos());
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody TypeDetail typeDetail) throws Exception {
        return ResponseEntity.ok(typeDetailService.insertTypeDetail(typeDetail));
    }

    @PostMapping("/modify")
    public ResponseEntity<?> modify(@RequestBody TypeDetail typeDetail) throws Exception {
        return ResponseEntity.ok(typeDetailService.updateTypeDetail(typeDetail));
    }

    @PostMapping("/remove")
    public ResponseEntity<?> remove(@RequestBody TypeDetail typeDetail) throws Exception {
        return ResponseEntity.ok(typeDetailService.deleteTypeDetail(typeDetail));
    }

    @PostMapping("/sort")
    public ResponseEntity<?> sort(@RequestBody List<TypeDetail> typeList) throws Exception {
        return ResponseEntity.ok(typeDetailService.sortTypeDetailList(typeList));
    }
}
