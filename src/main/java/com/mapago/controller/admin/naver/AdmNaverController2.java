package com.mapago.controller.admin.naver;

import com.mapago.controller.admin.sample.COSUploader;
import com.mapago.controller.admin.sample.TencentCOSMultipartUpload;
import com.mapago.service.sample.SampleService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.Bucket;
import com.qcloud.cos.region.Region;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/naver")
public class AdmNaverController2 {

    @Autowired
    private SampleService sampleService;
    private static final Map<String, String> cookieStorage = new HashMap<>();

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

    @PostMapping("/naverlogin")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String id = loginRequest.get("userId");
        String password = loginRequest.get("password");
        // WebDriver 설정
        //System.setProperty("webdriver.chrome.driver", "C:\\Users\\최우석\\.cache\\selenium\\chromedriver\\win64\\131.0.6778.204\\chromedriver.exe");
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        options.addArguments("--disable-gpu", "window-size=1920x1080", "--disable-extensions", "disable-infobars",
                "--incognito", "--headless");
        options.addArguments("--disable-blink-features=AutomationControlled"); // 자동화 메시지 제거
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"}); // 메시지 숨기기
        options.setExperimentalOption("useAutomationExtension", false); // 확장 비활성화

        WebDriver driver = new ChromeDriver(options);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            // 네이버 로그인 페이지로 이동
            String url = "https://nid.naver.com/nidlogin.login?mode=form&url=https%3A%2F%2Fwww.naver.com";
            driver.get(url);

            // 잠시 대기 (페이지 로딩)
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

            // 5초 대기
            try {
                Thread.sleep(5000); // 5000 밀리초 = 5초
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 아이디 입력 폼 찾기
            WebElement idField = driver.findElement(By.name("id"));

            // 비밀번호 입력 폼 찾기
            WebElement pwField = driver.findElement(By.name("pw"));

            // 아이디 입력
            //String uid = "nglemfk7";  // 실제 입력할 아이디로 변경
            idField.click();
            js.executeScript("arguments[0].value = '" + id + "';", idField);
            // 잠시 대기 (페이지 로딩)
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            // 5초 대기
            try {
                Thread.sleep(5000); // 5000 밀리초 = 5초
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 비밀번호 입력
            //String upw = "dntjr1@suwon35";  // 실제 입력할 비밀번호로 변경
            pwField.click();
            js.executeScript("arguments[0].value = '" + password + "';", pwField);

            // 잠시 대기 (페이지 로딩)
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            // 5초 대기
            try {
                Thread.sleep(5000); // 5000 밀리초 = 5초
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 로그인 버튼 클릭 (로그인 버튼을 찾고 클릭)
            WebElement loginButton = driver.findElement(By.id("log.login"));
            loginButton.click();

            // 로그인 후 페이지가 로드될 때까지 대기 (여기서는 로그인 성공 여부를 페이지 내 요소로 확인할 수 있음)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.urlContains("naver.com"));

            // 로그인 성공 여부 체크 (로그인 후 특정 요소를 확인하는 방법)
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("naver.com")) {
                System.out.println("로그인 성공");
            } else {
                System.out.println("로그인 실패");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //WebElement loginButton = driver.findElement(By.id("log.login"));
        //loginButton.click();
        // 쿠키 가져오기
        try {
            Thread.sleep(5000); // 5000 밀리초 = 5초
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Set<Cookie> cookies = driver.manage().getCookies();
        Map<String, String> cookieStorage = new HashMap<>();
        try {
            // 페이지가 로드될 때까지 기다리기 (특정 요소가 로드될 때까지)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Duration 사용
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("query"))); // 대기할 요소 ID (페이지 로딩 완료를 의미하는 요소)

            for (Cookie cookie : cookies) {
                cookieStorage.put(cookie.getName(), cookie.getValue());
            }

            // 쿠키 출력 (확인용)
            cookieStorage.forEach((key, value) -> System.out.println(key + ": " + value));
        } catch (TimeoutException e) {
            System.out.println("페이지 로딩이 너무 오래 걸려서 실패했습니다.");
            e.printStackTrace();
        }

        // 로그인 성공 여부 확인
        String loginCheck = "로그인성공";
        try {
            WebElement protectionCheck = driver.findElement(By.className("btn_next.detect_clear"));
            loginCheck = protectionCheck.getText().contains("보호조치") ? "보호조치" : "로그인성공";
        } catch (Exception e) {
            // 로그인 성공
        }

        driver.quit();

        // 응답 맵에 로그인 상태와 쿠키를 포함시켜 반환
        Map<String, Object> response = new HashMap<>();
        response.put("status", loginCheck);
        response.put("cookies", cookieStorage);

        String url = "https://apis.naver.com/cafe-web/cafe-mobile/CommentPost.json";
        String tempTarget = "28126727";
        String tempArticleId = "3662";
        String comment = "안녕하세요.ㅎㅎ ";

        // 요청 데이터 설정
        Map<String, String> data = new HashMap<>();
        data.put("cafeId", tempTarget);
        data.put("articleId", tempArticleId);
        data.put("content", comment);
        data.put("stickerId", "");
        data.put("requestFrom", "B");

        // MultiValueMap으로 변환
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.setAll(data);

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.82 Safari/537.36");
        headers.set("Accept", "application/json, text/plain, */*");
        headers.set("Accept-Encoding", "gzip, deflate, br");
        headers.set("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        headers.set("Content-Type", "application/x-www-form-urlencoded");
        headers.set("Origin", "https://m.cafe.naver.com");
        headers.set("Referer", "https://m.cafe.naver.com/ca-fe/web/cafes/" + tempTarget + "/articles/" + tempArticleId
                + "/comments?page=1&focus=50024535");
        headers.set("sec-ch-ua", "\"Google Chrome\";v=\"119\", \"Chromium\";v=\"119\", \"Not?A_Brand\";v=\"24\"");
        headers.set("sec-ch-ua-mobile", "?0");
        headers.set("sec-ch-ua-platform", "\"Windows\"");
        headers.set("sec-fetch-dest", "empty");
        headers.set("sec-fetch-mode", "cors");
        headers.set("sec-fetch-site", "same-site");
        headers.set("X-Cafe-Product", "mweb");

        HttpHeaders cookieHeaders = new HttpHeaders();

        // headers에서 key와 value를 가져올 때 value는 List<String>일 수 있으므로 이를 처리
        headers.forEach((key, value) -> {
            // value가 List<String>일 수 있기 때문에, 각 값을 순회하여 cookieHeaders에 추가
            for (String v : value) {
                cookieHeaders.add(key, v);  // 헤더 값이 여러 개일 수 있으므로 add로 추가
            }
        });

        // 쿠키를 추가할 때도 동일하게 처리
        cookies.forEach(cookie -> cookieHeaders.add("Cookie", cookie.getName() + "=" + cookie.getValue()));

        // HttpEntity 설정
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, cookieHeaders);

        // RestTemplate을 이용한 POST 요청
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> apiResponse = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            int statusCode = apiResponse.getStatusCodeValue();
            if (statusCode == 500) {
                System.out.println("실패");
                response.put("status", "실패");
            } else {
                System.out.println("등록완료");
                response.put("status", "등록완료");
            }
        } catch (RestClientException e) {
            System.out.println("요청 중 오류 발생: " + e.getMessage());
            e.printStackTrace();  // 예외의 자세한 스택 트레이스를 출력
            response.put("status", "요청 오류");
        }

        return ResponseEntity.ok(response);
    }

    //login > 쿠키값
    @PostMapping("/login3")
    public ResponseEntity<Map<String, Object>> login3(@RequestBody Map<String, String> loginRequest) {
        String id = loginRequest.get("userId");
        String password = loginRequest.get("password");
        // WebDriver 설정
        //System.setProperty("webdriver.chrome.driver", "C:\\Users\\최우석\\.cache\\selenium\\chromedriver\\win64\\131.0.6778.204\\chromedriver.exe");
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        options.addArguments("--disable-gpu", "window-size=1920x1080", "--disable-extensions", "disable-infobars",
                "--incognito", "--headless");
        options.addArguments("--disable-blink-features=AutomationControlled"); // 자동화 메시지 제거
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"}); // 메시지 숨기기
        options.setExperimentalOption("useAutomationExtension", false); // 확장 비활성화

        WebDriver driver = new ChromeDriver(options);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            // 네이버 로그인 페이지로 이동
            String url = "https://nid.naver.com/nidlogin.login?mode=form&url=https%3A%2F%2Fwww.naver.com";
            driver.get(url);

            // 잠시 대기 (페이지 로딩)
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

            // 5초 대기
            try {
                Thread.sleep(5000); // 5000 밀리초 = 5초
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 아이디 입력 폼 찾기
            WebElement idField = driver.findElement(By.name("id"));

            // 비밀번호 입력 폼 찾기
            WebElement pwField = driver.findElement(By.name("pw"));

            // 아이디 입력
            //String uid = "nglemfk7";  // 실제 입력할 아이디로 변경
            idField.click();
            js.executeScript("arguments[0].value = '" + id + "';", idField);
            // 잠시 대기 (페이지 로딩)
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            // 5초 대기
            try {
                Thread.sleep(5000); // 5000 밀리초 = 5초
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 비밀번호 입력
            //String upw = "dntjr1@suwon35";  // 실제 입력할 비밀번호로 변경
            pwField.click();
            js.executeScript("arguments[0].value = '" + password + "';", pwField);

            // 잠시 대기 (페이지 로딩)
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            // 5초 대기
            try {
                Thread.sleep(5000); // 5000 밀리초 = 5초
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 로그인 버튼 클릭 (로그인 버튼을 찾고 클릭)
            WebElement loginButton = driver.findElement(By.id("log.login"));
            loginButton.click();

            // 로그인 후 페이지가 로드될 때까지 대기 (여기서는 로그인 성공 여부를 페이지 내 요소로 확인할 수 있음)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.urlContains("naver.com"));

            // 로그인 성공 여부 체크 (로그인 후 특정 요소를 확인하는 방법)
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("naver.com")) {
                System.out.println("로그인 성공");
            } else {
                System.out.println("로그인 실패");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //WebElement loginButton = driver.findElement(By.id("log.login"));
        //loginButton.click();
        // 쿠키 가져오기
        try {
            Thread.sleep(5000); // 5000 밀리초 = 5초
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Set<Cookie> cookies = driver.manage().getCookies();
        Map<String, String> cookieStorage = new HashMap<>();
        try {
            // 페이지가 로드될 때까지 기다리기 (특정 요소가 로드될 때까지)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Duration 사용
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("query"))); // 대기할 요소 ID (페이지 로딩 완료를 의미하는 요소)

            for (Cookie cookie : cookies) {
                cookieStorage.put(cookie.getName(), cookie.getValue());
            }

            // 쿠키 출력 (확인용)
            cookieStorage.forEach((key, value) -> System.out.println(key + ": " + value));
        } catch (TimeoutException e) {
            System.out.println("페이지 로딩이 너무 오래 걸려서 실패했습니다.");
            e.printStackTrace();
        }

        // 로그인 성공 여부 확인
        String loginCheck = "로그인성공";
        try {
            WebElement protectionCheck = driver.findElement(By.className("btn_next.detect_clear"));
            loginCheck = protectionCheck.getText().contains("보호조치") ? "보호조치" : "로그인성공";
        } catch (Exception e) {
            // 로그인 성공
        }

        driver.quit();

        // 응답 맵에 로그인 상태와 쿠키를 포함시켜 반환
        Map<String, Object> response = new HashMap<>();
        response.put("status", loginCheck);
        response.put("cookies", cookieStorage);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/cafeComment")
    public ResponseEntity<Map<String, Object>> cafeComment(@RequestBody Map<String, Object> CafeInfo) {
        // 변수 선언
        String listUrl = "";
        Map<String, Object> response = new HashMap<>();
        String target = String.valueOf(CafeInfo.get("target"));
        String comment = String.valueOf(CafeInfo.get("comment"));
        String targetArticleNum = String.valueOf(CafeInfo.get("targetarticlenum"));
        String preArticleId = "";
        String tempTarget = "";

        // 쿠키를 Map으로 받음
        Map<String, String> cookies = (Map<String, String>) CafeInfo.get("cookies");

        try {
            // 쿠키 저장
            if (cookies != null) {
                for (Map.Entry<String, String> entry : cookies.entrySet()) {
                    cookieStorage.put(entry.getKey(), entry.getValue());
                }
                // 쿠키 출력 (확인용)
                cookieStorage.forEach((key, value) -> System.out.println(key + ": " + value));
            }
        } catch (Exception e) {
            System.out.println("쿠키 처리 중 오류 발생");
            e.printStackTrace();
        }

        //카페 설정

        if ("연습용-자유".equals(target)) {
            tempTarget = "28126727";
            listUrl = "https://apis.naver.com/cafe-web/cafe2/ArticleListV2dot1.json?search.clubid=" + tempTarget
                    + "&search.queryType=lastArticle&search.page=1&search.perPage=5&search.menuid=1&ad=false&adUnit=MW_CAFE_ARTICLE_LIST_RS";
        } else if ("휴싸방".equals(target)) {
            tempTarget = "25382183";
            if (targetArticleNum == null || targetArticleNum.isEmpty()) {
                targetArticleNum = "121";
            }
            listUrl = "https://apis.naver.com/cafe-web/cafe2/ArticleListV2dot1.json?search.clubid=" + tempTarget
                    + "&search.queryType=lastArticle&search.page=1&search.perPage=5&search.menuid=" + targetArticleNum
                    + "&ad=false&adUnit=MW_CAFE_ARTICLE_LIST_RS";
        }

        // 헤더 설정
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.84 Safari/537.36");
        headers.put("accept", "application/json, text/plain, */*");
        headers.put("accept-encoding", "gzip, deflate, br");
        headers.put("accept-language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        headers.put("origin", "https://m.cafe.naver.com");

        try {
            URL urlObj = new URL(listUrl);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");  // GET 메서드 사용
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.84 Safari/537.36");
            connection.setRequestProperty("Accept", "application/json, text/plain, */*");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200 OK
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder responseBuffer = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        responseBuffer.append(inputLine);
                    }

                    // JSON 파싱
                    JSONObject jsonObject = new JSONObject(responseBuffer.toString());
                    JSONArray articleList = jsonObject.getJSONObject("message")
                            .getJSONObject("result")
                            .optJSONArray("articleList");

                    if (articleList != null && articleList.length() > 0) {
                        preArticleId = String.valueOf(articleList.getJSONObject(0).getBigDecimal("articleId"));
                        System.out.println("pre_ArticleId: " + preArticleId);
                    } else {
                        System.out.println("게시글 목록이 비어 있습니다.");
                    }
                }
            } else {
                System.out.println("HTTP 응답 오류: " + responseCode);
            }
        } catch (IOException e) {
            System.out.println("HTTP 요청 처리 중 오류 발생");
            e.printStackTrace();
        }

        String tempArticleId = "3662";
        //댓글달기
        // 요청할 URL 및 파라미터 설정
        String url = "https://apis.naver.com/cafe-web/cafe-mobile/CommentPost.json";
        // 요청 데이터 설정
        Map<String, String> data = new HashMap<>();
        data.put("cafeId", tempTarget);
        data.put("articleId", tempArticleId);
        data.put("content", comment);
        data.put("stickerId", "");
        data.put("requestFrom", "B");

        // MultiValueMap으로 변환
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.setAll(data);

        // 헤더 설정
        HttpHeaders headersComment = new HttpHeaders();
        headersComment.set("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.82 Safari/537.36");
        headersComment.set("Accept", "application/json, text/plain, */*");
        headersComment.set("Accept-Encoding", "gzip, deflate, br");
        headersComment.set("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        headersComment.set("Content-Type", "application/x-www-form-urlencoded");
        headersComment.set("Origin", "https://m.cafe.naver.com");
        headersComment.set("Referer",
                "https://m.cafe.naver.com/ca-fe/web/cafes/" + tempTarget + "/articles/" + tempArticleId
                        + "/comments?page=1&focus=50024535");
        headersComment.set("sec-ch-ua",
                "\"Google Chrome\";v=\"119\", \"Chromium\";v=\"119\", \"Not?A_Brand\";v=\"24\"");
        headersComment.set("sec-ch-ua-mobile", "?0");
        headersComment.set("sec-ch-ua-platform", "\"Windows\"");
        headersComment.set("sec-fetch-dest", "empty");
        headersComment.set("sec-fetch-mode", "cors");
        headersComment.set("sec-fetch-site", "same-site");
        headersComment.set("X-Cafe-Product", "mweb");

        // 쿠키 설정 (올바르게 key-value 형태로 추가)
        if (cookies != null) {
            StringBuilder cookieHeader = new StringBuilder();
            for (Map.Entry<String, String> entry : cookies.entrySet()) {
                cookieHeader.append(entry.getKey()).append("=").append(entry.getValue()).append("; ");
            }
            if (cookieHeader.length() > 0) {
                headersComment.set("Cookie", cookieHeader.toString());
            }
        }
        // HttpEntity 설정
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headersComment);

        // RestTemplate을 이용한 POST 요청
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> apiResponse = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            int statusCode = apiResponse.getStatusCodeValue();
            if (statusCode == 500) {
                System.out.println("실패");
                response.put("status", "실패");
            } else {
                System.out.println("등록완료");
                response.put("status", "등록완료");
            }
        } catch (RestClientException e) {
            System.out.println("요청 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            response.put("status", "요청 오류");
        }

        // 응답 반환
        response.put("message", "댓글을 성공적으로 처리했습니다.");
        return ResponseEntity.ok(response);
    }

}
