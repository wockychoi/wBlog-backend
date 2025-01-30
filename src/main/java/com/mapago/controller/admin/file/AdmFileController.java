package com.mapago.controller.admin.file;

import com.mapago.config.exception.ErrorResponse;
import com.mapago.service.file.FileService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/file")
public class AdmFileController {

    private static final String UPLOAD_DIR = "uploads/";
    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam("type") String Type,
            @RequestParam("key") String Key) {
        try {
            if (files.isEmpty() || files.stream().anyMatch(MultipartFile::isEmpty)) {
                // 하나 이상의 파일이 비어 있는 경우
                return ResponseEntity.badRequest().body(
                        new ErrorResponse(LocalDateTime.now(), 400, "One or more files are empty.")
                );
            }

            // 서비스 계층으로 파일 리스트를 전달하여 저장 수행
            List<String> uploadedFileNames = fileService.saveFiles(files, Type, Key);

            // 성공 응답
            return ResponseEntity.ok(uploadedFileNames);

        } catch (IOException e) {
            // 에러 응답
            return ResponseEntity.status(500).body(
                    new ErrorResponse(LocalDateTime.now(), 500, "Failed to upload files.")
            );
        }
    }


    // 여러 개의 파일을 @RequestPart로 처리하는 API
    @PostMapping("/delete")
    public ResponseEntity<?> deleteFiles(@RequestPart("files") List<MultipartFile> files) {

        List<String> deleteFileNames = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    return ResponseEntity.badRequest().body(
                            new ErrorResponse(LocalDateTime.now(), 400, "One or more files are empty."));
                }

                // COS파일 저장
                String deleteFile = fileService.deleteFile(file);
                deleteFileNames.add(deleteFile);

            }

            // 성공 응답
            return ResponseEntity.ok(deleteFileNames);


        } catch (IOException e) {
            // 에러 응답
            return ResponseEntity.status(500).body(
                    new ErrorResponse(LocalDateTime.now(), 500, "Failed to upload files."));
        }
    }

}
