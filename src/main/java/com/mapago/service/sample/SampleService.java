package com.mapago.service.sample;

import com.mapago.mapper.sample.SampleMapper;
import com.mapago.model.sample.Sample;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import java.io.File;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class SampleService {

    private final SampleMapper sampleMapper;

    public SampleService(SampleMapper sampleMapper) {
        this.sampleMapper = sampleMapper;
    }

    public List<Sample> getSampleList() {
        return sampleMapper.findSampleList();
    }

    public Sample getSample(Integer sampleId) {
        return sampleMapper.findSampleById(sampleId);
    }

    // COS 인증 정보 설정
    /*
    Todo, dev, qa, prd 구분하여 값 가져오기 (버킷 설정 )
    그안에 폴더 구성 어떻게 해야할지
    1. 일단 cos, 테이블 저장
    2. 구성 확인
       folder : shop, community
    *  */
    @Value("${tencent.cos.status}")
    private String status;
    //list
    private static final String SECRET_ID = "IKID51yvjMnAMPK9szAEYnx41wpbDi5HIh9U"; // Replace with your secretId
    private static final String SECRET_KEY = "Ws32qSHz7GID3vjR2Un06474tbf3HMH5"; // Replace with your secretKey
    private static final String BUCKET_NAME = "mapago-1328580670"; // Replace with your bucket name
    private static final String REGION_NAME = "ap-seoul"; // Replace with your region name
    private static String KEY_STATE = "prd"; // 파일을 저장할 경로 및 이름

    public void migFiles() {
        String basePath = "C:\\mapago\\";  // "shop_1" ~ "shop_n" 폴더가 위치한 기본 경로
        int shopCount = 30;  // "shop_1"부터 "shop_n"까지의 n값, 이 예제에서는 3으로 가정
        String fileList = "";
        if ("dev".equalsIgnoreCase(status)) {
            KEY_STATE = "dev/";
        } else if ("prd".equalsIgnoreCase(status)) {
            KEY_STATE = "prd/";
        }
        // 1. COS 인증 정보 설정
        COSCredentials cred = new BasicCOSCredentials(SECRET_ID, SECRET_KEY);
        ClientConfig clientConfig = new ClientConfig(new Region(REGION_NAME));
        COSClient cosClient = new COSClient(cred, clientConfig);

        for (int i = 1; i <= shopCount; i++) {
            String folderName = "shop_" + i;
            System.out.println("Processing folder: " + folderName);
            File folder = new File(basePath + File.separator + folderName);
            fileList = ""; //초기화
            if (folder.exists() && folder.isDirectory()) {
                // 폴더 내의 파일 목록 가져오기
                File[] files = folder.listFiles();
                if (files != null) {
                    int fileCount = files.length;
                    System.out.println("Total files in " + folderName + ": " + fileCount);

                    for (File file : files) {
                        if (file.isFile()) {
                            System.out.println("Processing file: " + file.getName());

                            // 파일 정보 가져오기
                            String originalFileName = file.getName();  // 원본 파일 이름
                            long fileSize = file.length();  // 파일 크기 (바이트)
                            String fileExt = "";  // 파일 확장자
                            int dotIndex = originalFileName.lastIndexOf('.');
                            if (dotIndex > 0 && dotIndex < originalFileName.length() - 1) {
                                fileExt = originalFileName.substring(dotIndex + 1);  // 확장자 추출
                            }

                            String filePath = file.getAbsolutePath();  // 파일의 전체 경로

                            // 파일 정보를 File 객체에 설정
                            Sample paramfile = new Sample();
                            paramfile.setFileExtM(fileExt);
                            paramfile.setFilePathM(filePath);
                            paramfile.setSysFileNameM(originalFileName);
                            paramfile.setUserFileNameM(originalFileName);
                            paramfile.setFileSizeM((int) fileSize);

                            // 파일 정보 DB에 저장
                            try {
                                int generatedId = sampleMapper.migFile(paramfile);  // 데이터베이스에 파일 정보 저장
                                fileList += paramfile.getFileId().toString() + ",";

                                System.out.println("File saved successfully with ID: " + generatedId);

                            } catch (Exception e) {
                                System.err.println("Unexpected error occurred: " + e.getMessage());
                                e.printStackTrace();  // 다른 예외에 대한 처리
                            }
                        }
                    }
                }
            } else {
                System.out.println("Directory " + folderName + " does not exist or is not a directory.");
            }
            try {
                System.out.println("fileList : " + fileList);
                if (fileList.endsWith(",")) {
                    // 마지막 쉼표를 제거
                    fileList = fileList.substring(0, fileList.length() - 1);
                }
                Sample paramfile2 = new Sample();
                paramfile2.setShopId(String.valueOf(i));
                paramfile2.setFileIdList(fileList);
                sampleMapper.saveShop(paramfile2);
            } catch (Exception e) {
                System.err.println("Unexpected error occurred: " + e.getMessage());
                e.printStackTrace();  // 다른 예외에 대한 처리
            }
        }
    }

    public void migFiles2() {
        // 로컬 파일 경로 설정
        String basePath = "C:\\mapago\\";  // "shop_1" ~ "shop_n" 폴더가 위치한 기본 경로
        int shopCount = 1317;  // "shop_1"부터 "shop_n"까지의 n값
        String fileList = "";

        // 환경에 따라 경로 설정 (개발 / 운영)
        if ("dev".equalsIgnoreCase(status)) {
            KEY_STATE = "dev/shop/";
        } else if ("prd".equalsIgnoreCase(status)) {
            KEY_STATE = "prd/shop/";
        }

        // COS 인증 정보 설정
        COSCredentials cred = new BasicCOSCredentials(SECRET_ID, SECRET_KEY);
        ClientConfig clientConfig = new ClientConfig(new Region(REGION_NAME));
        COSClient cosClient = new COSClient(cred, clientConfig);

        for (int i = 1; i <= shopCount; i++) {
            String folderName = "shop_" + i;
            System.out.println("Processing folder: " + folderName);
            File folder = new File(basePath + File.separator + folderName);
            fileList = ""; // 초기화

            if (folder.exists() && folder.isDirectory()) {
                // 폴더 내의 파일 목록 가져오기
                File[] files = folder.listFiles();
                if (files != null) {
                    int fileCount = files.length;
                    System.out.println("Total files in " + folderName + ": " + fileCount);

                    for (File file : files) {
                        if (file.isFile()) {
                            System.out.println("Processing file: " + file.getName());

                            // 파일 정보 가져오기
                            String originalFileName = file.getName();  // 원본 파일 이름
                            long fileSize = file.length();  // 파일 크기 (바이트)
                            String fileExt = "";  // 파일 확장자
                            int dotIndex = originalFileName.lastIndexOf('.');
                            if (dotIndex > 0 && dotIndex < originalFileName.length() - 1) {
                                fileExt = originalFileName.substring(dotIndex + 1);  // 확장자 추출
                            }
                            // UUID로 고유한 파일명 생성
                            String fileExtension = getFileExtension(originalFileName); // 파일 확장자 추출
                            String uuidFileName = UUID.randomUUID().toString() + "." + fileExtension; // UUID + 확장자

                            String filePath = file.getAbsolutePath();  // 파일의 전체 경로

                            // COS 경로 설정
                            String cosFolderPath = KEY_STATE + "shop_" + i + "/";
                            String cosFileKey = cosFolderPath + uuidFileName;

                            // 파일 업로드
                            try {
                                PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, cosFileKey, file);
                                PutObjectResult result = cosClient.putObject(putObjectRequest);
                                System.out.println("File uploaded successfully to COS: " + cosFileKey);
                            } catch (Exception e) {
                                System.err.println("Failed to upload file to COS: " + originalFileName + ". Error: "
                                        + e.getMessage());
                                e.printStackTrace();
                                continue;  // COS 업로드 실패 시 다음 파일로 진행
                            }

                            // 파일 정보를 Sample 객체에 설정하여 데이터베이스에 저장
                            Sample paramfile = new Sample();
                            paramfile.setFileExtM(fileExt);
                            paramfile.setFilePathM(cosFolderPath);  // 로컬 경로를 저장할지, COS 경로를 저장할지 선택 (권장: COS 경로)
                            paramfile.setSysFileNameM(uuidFileName);
                            paramfile.setUserFileNameM(originalFileName);
                            paramfile.setFileSizeM((int) fileSize);

                            try {
                                // 데이터베이스에 파일 정보 저장
                                int generatedId = sampleMapper.migFile(paramfile);
                                fileList += paramfile.getFileId().toString() + ",";
                                System.out.println("File saved successfully with ID: " + generatedId);
                            } catch (Exception e) {
                                System.err.println(
                                        "Unexpected error occurred while saving file to DB: " + e.getMessage());
                                e.printStackTrace();  // 다른 예외에 대한 처리
                            }
                        }
                    }
                }
            } else {
                System.out.println("Directory " + folderName + " does not exist or is not a directory.");
            }

            try {
                System.out.println("fileList : " + fileList);
                if (fileList.endsWith(",")) {
                    // 마지막 쉼표를 제거
                    fileList = fileList.substring(0, fileList.length() - 1);
                }

                // Shop과 파일 리스트를 저장하는 로직
                Sample paramfile2 = new Sample();
                paramfile2.setShopId(String.valueOf(i));
                paramfile2.setFileIdList(fileList);  // 파일 ID 목록을 저장
                sampleMapper.saveShop(paramfile2);  // Shop 정보와 파일 목록을 DB에 저장
            } catch (Exception e) {
                System.err.println("Unexpected error occurred while saving shop to DB: " + e.getMessage());
                e.printStackTrace();  // 다른 예외에 대한 처리
            }
        }

        // COS 클라이언트 종료
        cosClient.shutdown();
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