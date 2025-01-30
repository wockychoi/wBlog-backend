package com.mapago.controller.portal.notice;

import com.mapago.model.type.TypeNotice;
import com.mapago.service.type.TypeNoticeService;
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
@RequestMapping("/portal/api/notice")
public class PtlNoticeController {

    @Autowired
    private TypeNoticeService typeNoticeService;

    @GetMapping("/findTypeNoticeList")
    public ResponseEntity<?> findTypeNoticeList(TypeNotice typeNotice) throws Exception {
        return ResponseEntity.ok(typeNoticeService.findTypeNoticeList(typeNotice));
    }

    @GetMapping("/findTypeNotice")
    public ResponseEntity<?> findTypeNotice(TypeNotice typeNotice) throws Exception {
        return ResponseEntity.ok(typeNoticeService.findTypeNotice(typeNotice));
    }
    
    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody TypeNotice typeNotice) throws Exception {
        return ResponseEntity.ok(typeNoticeService.insertTypeNotice(typeNotice));
    }

    @PostMapping("/modify")
    public ResponseEntity<?> modify(@RequestBody TypeNotice typeNotice) throws Exception {
        return ResponseEntity.ok(typeNoticeService.updateTypeNotice(typeNotice));
    }

    @PostMapping("/remove")
    public ResponseEntity<?> remove(@RequestBody TypeNotice typeNotice) throws Exception {
        return ResponseEntity.ok(typeNoticeService.deleteTypeNotice(typeNotice));
    }
}
