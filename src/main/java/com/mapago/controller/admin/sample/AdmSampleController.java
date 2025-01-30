package com.mapago.controller.admin.sample;

import com.mapago.service.sample.SampleService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.Bucket;
import com.qcloud.cos.region.Region;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/sample")
public class AdmSampleController {

    @Autowired
    private SampleService sampleService;

    @GetMapping("/list")
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(sampleService.getSampleList());
    }

    @GetMapping("/{sampleId}")
    public ResponseEntity<?> allListByUserId(@PathVariable("sampleId") Integer sampleId) {
        return ResponseEntity.ok(sampleService.getSample(sampleId));
    }

    @GetMapping("/listFile")
    public ResponseEntity<?> list2() {
        String status = "";
        COSCredentials cred = new BasicCOSCredentials("IKID51yvjMnAMPK9szAEYnx41wpbDi5HIh9U",
                "Ws32qSHz7GID3vjR2Un06474tbf3HMH5");
        ClientConfig clientConfig = new ClientConfig(new Region("ap-guangzhou"));
        COSClient cosClient = new COSClient(cred, clientConfig);
        List<Bucket> buckets = cosClient.listBuckets();
        for (Bucket bucketElement : buckets) {
            String bucketName = bucketElement.getName();
            String bucketLocation = bucketElement.getLocation();
        }

        return ResponseEntity.ok(sampleService.getSampleList());
    }


    @GetMapping("/upload")
    public ResponseEntity<?> list3() {
        String secretId = "IKID51yvjMnAMPK9szAEYnx41wpbDi5HIh9U";
        String secretKey = "Ws32qSHz7GID3vjR2Un06474tbf3HMH5";
        String regionName = "ap-seoul";  // 리전 이름 예시
        String bucketName = "mapago-1328580670";

        // COSUploader 객체 생성
        COSUploader uploader = new COSUploader(secretId, secretKey, regionName, bucketName);

        // 업로드할 파일 설정
        String key = "folder/하바나 올드타운 스퀘어.png";   // COS에 저장될 파일 경로 및 이름
        String filePath = "C:/Users/최우석/Desktop/하바나 올드타운 스퀘어.png";  // 로컬 파일 경로

        // 파일 업로드 및 URL 출력
        String fileUrl = uploader.uploadFile(key, filePath);
        System.out.println("Uploaded file URL: " + fileUrl);

        // COSClient 종료
        uploader.shutdown();
        return ResponseEntity.ok(sampleService.getSampleList());
    }

    @GetMapping("/delete")
    public ResponseEntity<?> list4() {
        String secretId = "IKID51yvjMnAMPK9szAEYnx41wpbDi5HIh9U";
        String secretKey = "Ws32qSHz7GID3vjR2Un06474tbf3HMH5";
        String regionName = "ap-seoul";  // 리전 이름 예시
        String bucketName = "mapago-1328580670";

        // COSUploader 객체 생성
        COSUploader uploader = new COSUploader(secretId, secretKey, regionName, bucketName);

        // 업로드할 파일 설정
        String key = "folder/하바나 올드타운 스퀘어.png";   // COS에 저장될 파일 경로 및 이름
        String filePath = "C:/Users/최우석/Desktop/하바나 올드타운 스퀘어.png";  // 로컬 파일 경로

        // 파일 업로드 및 URL 출력
        String fileUrl = uploader.deleteFile(key);
        System.out.println("Uploaded file URL: " + fileUrl);

        // COSClient 종료
        uploader.shutdown();
        return ResponseEntity.ok(sampleService.getSampleList());
    }


    @GetMapping("/multiupload")
    public ResponseEntity<?> multiupload() {
        String secretId = "IKID51yvjMnAMPK9szAEYnx41wpbDi5HIh9U";
        String secretKey = "Ws32qSHz7GID3vjR2Un06474tbf3HMH5";
        String regionName = "ap-seoul";  // 리전 이름 예시
        String bucketName = "mapago-1328580670";
        String key = "folder/하바나 올드타운 스퀘어.png";   // COS에 저장될 파일 경로 및 이름
        String filePath = "C:/Users/최우석/Desktop/하바나 올드타운 스퀘어.png";  // 로컬 파일 경로
        // COSUploader 객체 생성
        TencentCOSMultipartUpload multiuploader = new TencentCOSMultipartUpload();

        // 업로드할 파일 설정

        // 파일 업로드 및 URL 출력
        String fileUrl = multiuploader.MultipartUpload(secretId, secretKey, regionName, bucketName, key, filePath);
        System.out.println("Uploaded file URL: " + fileUrl);

        return ResponseEntity.ok(sampleService.getSampleList());
    }

    @PostMapping("/mig")
    public String migFiles(
            @RequestParam("type") String Type) {
        try {
            // 서비스 계층으로 파일 리스트를 전달하여 저장 수행
            sampleService.migFiles2();

            // 성공 응답
            return "S";

        } catch (Exception e) {
            // 에러 응답
            return "Error";

        }
    }


}
