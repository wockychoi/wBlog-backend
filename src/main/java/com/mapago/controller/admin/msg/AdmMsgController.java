package com.mapago.controller.admin.msg;

import com.mapago.model.msg.Msg;
import com.mapago.service.msg.MsgService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/msg")
public class AdmMsgController {

    @Autowired
    private MsgService msgService;

    @PostMapping("/list")
    public ResponseEntity<?> list(@RequestBody Msg msg) throws Exception {
        return ResponseEntity.ok(msgService.getMsgList(msg));
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody Msg msg) throws Exception {
        return ResponseEntity.ok(msgService.insertMsg(msg));
    }

    @PostMapping("/remove")
    public ResponseEntity<?> remove(@RequestBody Msg msg) throws Exception {
        return ResponseEntity.ok(msgService.deleteMsg(msg));
    }


}
