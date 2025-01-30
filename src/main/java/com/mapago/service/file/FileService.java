package com.mapago.service.file;

import com.mapago.mapper.file.FileMapper;
import com.mapago.model.file.File;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.CompleteMultipartUploadRequest;
import com.qcloud.cos.model.CompleteMultipartUploadResult;
import com.qcloud.cos.model.DeleteObjectRequest;
import com.qcloud.cos.model.InitiateMultipartUploadRequest;
import com.qcloud.cos.model.InitiateMultipartUploadResult;
import com.qcloud.cos.model.PartETag;
import com.qcloud.cos.model.UploadPartRequest;
import com.qcloud.cos.model.UploadPartResult;
import com.qcloud.cos.region.Region;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {
    // COS 인증 정보 설정
    /*
    Todo, dev, qa, prd 구분하여 값 가져오기 (버킷 설정 )
    그안에 폴더 구성 어떻게 해야할지
    1. 일단 cos, 테이블 저장
    2. 구성 확인
       folder : shop, community
    *  */
    private final FileMapper fileMapper;


    @Value("${tencent.cos.status}")
    private String KEY_STATE;

    @Value("${tencent.cos.secret-id}")
    private String SECRET_ID;

    @Value("${tencent.cos.secret-key}")
    private String SECRET_KEY;

    @Value("${tencent.cos.bucket-name}")
    private String BUCKET_NAME;

    @Value("${tencent.cos.region-name}")
    private String REGION_NAME;


    /*
    private final FileMapper fileMapper;
    private static final String SECRET_ID = "IKID51yvjMnAMPK9szAEYnx41wpbDi5HIh9U"; // Replace with your secretId
    private static final String SECRET_KEY = "Ws32qSHz7GID3vjR2Un06474tbf3HMH5"; // Replace with your secretKey
    private static final String BUCKET_NAME = "mapago-1328580670"; // Replace with your bucket name
    private static final String REGION_NAME = "ap-seoul"; // Replace with your region name
     */


    public FileService(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public List<String> saveFiles(List<MultipartFile> files, String type, String key) throws IOException {

        List<String> fileIds = new ArrayList<>();
        // 1. COS 인증 정보 설정
        COSCredentials cred = new BasicCOSCredentials(SECRET_ID, SECRET_KEY);
        ClientConfig clientConfig = new ClientConfig(new Region(REGION_NAME));
        COSClient cosClient = new COSClient(cred, clientConfig);

        for (MultipartFile file : files) {
            // 파일 정보
            String originalFileName = file.getOriginalFilename(); // 원본 파일명
            if (originalFileName == null) {
                throw new IllegalArgumentException("File name is invalid.");
            }

            // UUID로 고유한 파일명 생성
            String fileExtension = getFileExtension(originalFileName); // 파일 확장자 추출
            String uuidFileName = UUID.randomUUID().toString() + "." + fileExtension; // UUID + 확장자

            String KEY = "";
            String filePath = "";
            // 업무 구분에 따른 폴더명 변경 및 파일 경로 설정
            /*if (type.equalsIgnoreCase("S")) {
                filePath = KEY_STATE + "shop/";
                KEY = KEY_STATE + "shop/" + uuidFileName;
            } else if (type.equalsIgnoreCase("C")) {
                filePath = KEY_STATE + "community/";
                KEY = KEY_STATE + "community/" + uuidFileName;
            }*/
            //shop/shop_n
            filePath = KEY_STATE + type + "/" + type + "_" + key + "/";
            KEY = KEY_STATE + type + "/" + type + "_" + key + "/" + uuidFileName;

            // 2. 업로드할 파일과 파일 사이즈 설정
            long fileSize = file.getSize(); // 파일 크기
            long partSize = 5 * 1024 * 1024; // 각 파트의 크기를 5MB로 설정

            // 3. 멀티파트 업로드 초기화
            InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(
                    BUCKET_NAME, KEY);
            InitiateMultipartUploadResult initiateMultipartUploadResult = cosClient.initiateMultipartUpload(
                    initiateMultipartUploadRequest);
            String uploadId = initiateMultipartUploadResult.getUploadId();

            // 4. 파일을 파트로 분할하여 업로드
            List<PartETag> partETagList = new ArrayList<>();
            long filePosition = 0;

            // InputStream을 사용하여 파일 읽기
            try (InputStream inputStream = file.getInputStream()) {
                for (int i = 1; filePosition < fileSize; i++) {
                    // 파트 크기 계산
                    long currentPartSize = Math.min(partSize, fileSize - filePosition);

                    // InputStream에서 읽어올 바이트 배열
                    byte[] buffer = new byte[(int) currentPartSize];
                    int bytesRead = inputStream.read(buffer);

                    if (bytesRead == -1) {
                        break; // 파일 끝에 도달
                    }

                    // 각 파트 업로드
                    UploadPartRequest uploadPartRequest = new UploadPartRequest()
                            .withBucketName(BUCKET_NAME)
                            .withKey(KEY)
                            .withUploadId(uploadId)
                            .withInputStream(new ByteArrayInputStream(buffer))
                            .withPartSize(bytesRead)
                            .withPartNumber(i);
                    UploadPartResult uploadPartResult = cosClient.uploadPart(uploadPartRequest);
                    partETagList.add(uploadPartResult.getPartETag());

                    filePosition += currentPartSize;
                }
            } catch (IOException e) {
                e.printStackTrace();
                //cosClient.abortMultipartUpload(new InitiateMultipartUploadRequest(BUCKET_NAME, KEY));
                throw new IOException("File read error", e);
            }

            // 5. 모든 파트 업로드 완료 후 멀티파트 업로드 완료
            CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(
                    BUCKET_NAME, KEY, uploadId, partETagList);

            // 6. 데이터베이스에 파일 정보 저장

            File paramfile = new File();
            paramfile.setFileExt(fileExtension);
            paramfile.setFilePath(filePath);
            paramfile.setSysFileName(uuidFileName); // 시스템에 저장될 파일명은 UUID로 변경
            paramfile.setUserFileName(originalFileName); // 사용자가 업로드한 원본 파일명
            paramfile.setFileSize((int) fileSize);
            int generatedId = fileMapper.saveFile(paramfile);

            // 7. 업로드 결과 확인 및 처리
            try {
                CompleteMultipartUploadResult completeMultipartUploadResult = cosClient.completeMultipartUpload(
                        completeMultipartUploadRequest);
                System.out.println("Upload completed successfully. ETag: " + completeMultipartUploadResult.getETag());
            } catch (CosServiceException e) {
                e.printStackTrace();
                throw new RuntimeException("COS Service error", e);
            } catch (CosClientException e) {
                e.printStackTrace();
                throw new RuntimeException("COS Client error", e);
            }

            // 저장된 파일 ID 리스트에 추가
            fileIds.add(paramfile.getFileId().toString());
        }

        // 8. COS 클라이언트 종료
        cosClient.shutdown();

        return fileIds;
    }

    public String deleteFile(MultipartFile file) throws IOException {
        // 1. 자격 증명 설정 SECRET_ID, SECRET_KEY
        COSCredentials cred = new BasicCOSCredentials(SECRET_ID, SECRET_KEY);

        // 2. 지역 설정 (예: ap-seoul)
        ClientConfig clientConfig = new ClientConfig(new Region(REGION_NAME));

        // 3. COSClient 생성
        COSClient cosClient = new COSClient(cred, clientConfig);

        // 4. 삭제할 객체의 버킷 이름과 키 설정

        String bucketName = BUCKET_NAME;
        String objectKey = "your-object-key";
        String reutrnText = "";

        try {
            // 5. 객체 삭제 요청 생성
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, objectKey);

            // 6. 객체 삭제 수행
            cosClient.deleteObject(deleteObjectRequest);

            // tb 삭제 실행
            File paramfile = new File();
            paramfile.setFileId(1);
            // fileMapper.deleteFile(paramfile);

            System.out.println("Object deleted successfully.");
            reutrnText = "File deleted successfully.";

        } catch (Exception e) {
            System.err.println("Failed to delete object: " + e.getMessage());
            reutrnText = "Failed to delete object: " + e.getMessage();
        } finally {
            // 7. COSClient 종료
            cosClient.shutdown();
        }
        return reutrnText;
    }

    // Helper method to get the file extension
    private String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex == -1) {
            return ""; // No extension found
        }
        return fileName.substring(lastIndex + 1);
    }


}
