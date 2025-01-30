package com.mapago.controller.admin.sample;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TencentCOSMultipartUpload {


    // 생성자에서 COSClient와 버킷 이름을 설정
    public String MultipartUpload(String SECRET_ID, String SECRET_KEY, String REGION_NAME, String BUCKET_NAME, String KEY, String LOCAL_FILE_PATH) {
        // 1. 인증 정보 설정
        COSCredentials cred = new BasicCOSCredentials(SECRET_ID, SECRET_KEY);
        ClientConfig clientConfig = new ClientConfig(new Region(REGION_NAME));
        COSClient cosClient = new COSClient(cred, clientConfig);

        // 2. 업로드할 파일과 파일 사이즈 설정
        File localFile = new File(LOCAL_FILE_PATH);
        long fileSize = localFile.length();
        long partSize = 5 * 1024 * 1024; // 각 파트의 크기를 5MB로 설정

        // 3. 멀티파트 업로드 초기화
        InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(BUCKET_NAME, KEY);
        InitiateMultipartUploadResult initiateMultipartUploadResult = cosClient.initiateMultipartUpload(initiateMultipartUploadRequest);
        String uploadId = initiateMultipartUploadResult.getUploadId();

        // 4. 파일을 파트로 분할하여 업로드
        List<PartETag> partETagList = new ArrayList<>();
        long filePosition = 0;
        for (int i = 1; filePosition < fileSize; i++) {
            // 파트 크기 계산
            long currentPartSize = Math.min(partSize, fileSize - filePosition);

            // 각 파트 업로드
            UploadPartRequest uploadPartRequest = new UploadPartRequest()
                    .withBucketName(BUCKET_NAME)
                    .withKey(KEY)
                    .withUploadId(uploadId)
                    .withFile(localFile)
                    .withPartSize(currentPartSize)
                    .withFileOffset(filePosition)
                    .withPartNumber(i);
            UploadPartResult uploadPartResult = cosClient.uploadPart(uploadPartRequest);
            partETagList.add(uploadPartResult.getPartETag());

            filePosition += currentPartSize;
        }

        // 5. 모든 파트 업로드 완료 후 멀티파트 업로드 완료
        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(BUCKET_NAME, KEY, uploadId, partETagList);
        try {
            CompleteMultipartUploadResult completeMultipartUploadResult = cosClient.completeMultipartUpload(completeMultipartUploadRequest);
            System.out.println("Upload completed successfully. ETag: " + completeMultipartUploadResult.getETag());
        } catch (CosServiceException e) {
            e.printStackTrace();
        } catch (CosClientException e) {
            e.printStackTrace();
        }

        // 6. COS 클라이언트 종료
        cosClient.shutdown();
        return "S";
    }

}
