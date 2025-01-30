package com.mapago.controller.admin.type;

import com.mapago.model.type.Type;
import com.mapago.service.type.TypeService;
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
@RequestMapping("/admin/api/type")
public class AdmTypeController {

    @Autowired
    private TypeService typeService;

    @GetMapping("/list")
    public ResponseEntity<?> list(Type type) throws Exception {
        return ResponseEntity.ok(typeService.findTypeList(type));
    }

    @GetMapping("/listWithInfos")
    public ResponseEntity<?> listWithInfos() throws Exception {
        return ResponseEntity.ok(typeService.findTypeListWithInfos());
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody Type type) throws Exception {
        return ResponseEntity.ok(typeService.insertType(type));
    }

    @PostMapping("/modify")
    public ResponseEntity<?> modify(@RequestBody Type type) throws Exception {
        return ResponseEntity.ok(typeService.updateType(type));
    }

    @PostMapping("/remove")
    public ResponseEntity<?> remove(@RequestBody Type type) throws Exception {
        return ResponseEntity.ok(typeService.deleteType(type));
    }

    @PostMapping("/sort")
    public ResponseEntity<?> sort(@RequestBody List<Type> typeList) throws Exception {
        return ResponseEntity.ok(typeService.sortTypeList(typeList));
    }
}
