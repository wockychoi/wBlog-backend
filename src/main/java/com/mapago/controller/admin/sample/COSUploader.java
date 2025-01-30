package com.mapago.controller.admin.sample;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.DeleteObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import java.io.File;

public class COSUploader {

    private final COSClient cosClient;
    private final String bucketName;

    // 생성자에서 COSClient와 버킷 이름을 설정
    public COSUploader(String secretId, String secretKey, String regionName, String bucketName) {
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(regionName));
        this.cosClient = new COSClient(cred, clientConfig);
        this.bucketName = bucketName;
    }

    public String uploadFile(String key, String filePath) {
        File localFile = new File(filePath);

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
            cosClient.putObject(putObjectRequest);

            // 업로드된 파일의 URL 생성
            String fileUrl = "https://" + bucketName + ".cos." + cosClient.getClientConfig().getRegion().getRegionName()
                    + ".myqcloud.com/" + key;
            System.out.println("File uploaded successfully. File URL: " + fileUrl);
            return fileUrl;

        } catch (CosServiceException e) {
            System.err.println("Service Exception: " + e.getErrorMessage());
            throw new RuntimeException("Failed to upload file to COS", e);
        } catch (CosClientException e) {
            System.err.println("Client Exception: " + e.getMessage());
            throw new RuntimeException("Failed to upload file to COS", e);
        }
    }

    // 파일 삭제 함수
    public String deleteFile(String key) {
        try {
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, key);
            cosClient.deleteObject(deleteObjectRequest);
            System.out.println("File deleted successfully: " + key);
            return "File deleted successfully: " + key;
        } catch (CosServiceException e) {
            System.err.println("Service Exception: " + e.getErrorMessage());
            throw new RuntimeException("Failed to delete file from COS", e);
        } catch (CosClientException e) {
            System.err.println("Client Exception: " + e.getMessage());
            throw new RuntimeException("Failed to delete file from COS", e);
        }
    }

    // COSClient 종료 함수
    public void shutdown() {
        cosClient.shutdown();
    }
}
