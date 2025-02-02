package com.mapago.controller.admin.user;

import com.mapago.model.user.User;
import com.mapago.service.user.UserService;
import com.mapago.util.AesUtil;
import com.mapago.util.JwtUtil;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/user")
public class AdmUserController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private static final Map<String, String> cookieStorage = new HashMap<>();


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User loginRequest) {
        try {
            String decryptedPassword = AesUtil.decrypt(loginRequest.getPassword());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUserId(), decryptedPassword)
            );
            UserDetails userDetails = userService.loadUserByUsername(loginRequest.getUserId());
            User user = null;
            if (userDetails.isEnabled()) {
                user = userService.findByUserId(loginRequest.getUserId());
                user.setRoles(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()));
            }
            String accessToken = jwtUtil.generateAdminAccessToken(userDetails.getUsername());
            String refreshToken = jwtUtil.generateAdminRefreshToken(userDetails.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("user", user);

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(Map.of("error", "아이디 또는 비밀번호를 확인해주세요."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "오류가 발생했습니다. 운영자에게 문의 해주세요."));
        }
    }

    @PostMapping("/login2")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String id = loginRequest.get("id");
        String password = loginRequest.get("password");

        // WebDriver 설정
        System.setProperty("webdriver.chrome.driver",
                "C:\\Users\\최우석\\.cache\\selenium\\chromedriver\\win64\\131.0.6778.204\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-gpu", "window-size=1920x1080", "--disable-extensions", "disable-infobars");

        WebDriver driver = new ChromeDriver(options);
        try {
            // 네이버 로그인 페이지로 이동
            String url = "https://nid.naver.com/nidlogin.login?mode=form&url=https%3A%2F%2Fwww.naver.com";
            driver.get(url);

            // 잠시 대기 (페이지 로딩)
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

            // 아이디 입력 폼 찾기
            WebElement idField = driver.findElement(By.name("id"));

            // 비밀번호 입력 폼 찾기
            WebElement pwField = driver.findElement(By.name("pw"));

            // 아이디 입력
            String uid = "glemfk3";  // 실제 입력할 아이디로 변경
            idField.click();
            idField.sendKeys(uid);
// 잠시 대기 (페이지 로딩)
            driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
            // 비밀번호 입력
            String upw = "dntjr1@suwon35";  // 실제 입력할 비밀번호로 변경
            pwField.click();
            pwField.sendKeys(upw);
// 잠시 대기 (페이지 로딩)
            driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
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


    @GetMapping("/list")
    public ResponseEntity<?> list(User user) throws Exception {
        return ResponseEntity.ok(userService.getUserList(user));
    }

    @PostMapping("/modify")
    public ResponseEntity<?> modify(@RequestBody User user) throws Exception {
        return ResponseEntity.ok(userService.updateUser(user));
    }

}