package com.mapago.controller.admin.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapago.model.user.PostingLog;
import com.mapago.model.user.User;
import com.mapago.service.user.UserService;
import com.mapago.util.AesUtil;
import com.mapago.util.JwtUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.io.FileHandler;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final Set<Cookie> cookies;
    private static final Map<String, String> cookieStorage = new HashMap<>();


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User loginRequest) {
        try {
            // 🔓 1. 클라이언트에서 받은 비밀번호 복호화
            String decryptedPasswordFromClient = AesUtil.decrypt(loginRequest.getPassword());

            // 🔍 2. 사용자 조회
            User user = userService.findByUserId(loginRequest.getUserId());
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "존재하지 않는 아이디입니다."));
            }

            // 🔓 3. DB에 저장된 암호화된 비밀번호도 복호화
            String decryptedPasswordFromDB = AesUtil.decrypt(user.getPassword());

            // 🔐 4. 두 복호화된 비밀번호 비교
            if (!decryptedPasswordFromClient.equals(decryptedPasswordFromDB)) {
                return ResponseEntity.status(401).body(Map.of("error", "비밀번호가 일치하지 않습니다."));
            }

            // ✅ 5. 로그인 성공 처리
            UserDetails userDetails = userService.loadUserByUsername(user.getUserId());
            user.setRoles(userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));

            String accessToken = jwtUtil.generateAdminAccessToken(user.getUserId());
            String refreshToken = jwtUtil.generateAdminRefreshToken(user.getUserId());

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("user", user);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "오류가 발생했습니다. 운영자에게 문의 해주세요."));
        }
    }


    @PostMapping("/login_backup")
    public ResponseEntity<Map<String, Object>> login_backup(@RequestBody User loginRequest) {
        try {
            System.out.println(AesUtil.encrypt("1234"));
            String decryptedPassword = AesUtil.decrypt(loginRequest.getPassword());
            /*authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUserId(), decryptedPassword)
            );*/
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

    @GetMapping("/list")
    public ResponseEntity<?> list(User user) throws Exception {
        return ResponseEntity.ok(userService.getUserList(user));
    }

    @PostMapping("/modify")
    public ResponseEntity<?> modify(@RequestBody User user) throws Exception {
        return ResponseEntity.ok(userService.updateUser(user));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) throws Exception {
        return ResponseEntity.ok(userService.insertUser(user));
    }

    @GetMapping("/duplicate")
    public ResponseEntity<?> duplicate(@RequestParam String userId) throws Exception {
        return ResponseEntity.ok(userService.findByUserId(userId));
    }

    @PostMapping("/savePostKey")
    public ResponseEntity<?> savePostKey(@RequestBody User user) throws Exception {
        return ResponseEntity.ok(userService.savePostKey(user));
    }

    @PostMapping("/tistoryLogin")
    public ResponseEntity<Map<String, Object>> tistoryLogin(@RequestBody Map<String, String> loginRequest) {
        String id = loginRequest.get("userId");
        String password = loginRequest.get("password");

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        options.addArguments("--disable-gpu");
        options.addArguments("window-size=1920x1080");
        options.addArguments("--disable-extensions");
        options.addArguments("disable-infobars");
        options.addArguments("--incognito");
        options.addArguments("--headless=new"); // 최신 headless
        options.addArguments("--disable-blink-features=AutomationControlled");

        // ✅ 고유한 user-data-dir 설정 (충돌 방지)
        String userDataDir = "/tmp/chrome-profile-" + UUID.randomUUID().toString();
        options.addArguments("--user-data-dir=" + userDataDir);

        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = new ChromeDriver(options);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        Map<String, Object> response = new HashMap<>();
        Map<String, String> cookieStorage = new HashMap<>();
        String loginCheck = "로그인성공";

        try {
            String kakaoLoginUrl = "https://accounts.kakao.com/login/?continue=https%3A%2F%2Fkauth.kakao.com%2Foauth%2Fauthorize%3Fclient_id%3D3e6ddd834b023f24221217e370daed18%26state%3DaHR0cHM6Ly93d3cudGlzdG9yeS5jb20v%26redirect_uri%3Dhttps%253A%252F%252Fwww.tistory.com%252Fauth%252Fkakao%252Fredirect%26response_type%3Dcode%26auth_tran_id%3DoQTqsL_iicbdhe~9e56q00O1~CIUUwaqmMaluDAeqhvNIWjUjwnf0u~PiPUR%26ka%3Dsdk%252F2.7.3%2520os%252Fjavascript%2520sdk_type%252Fjavascript%2520lang%252Fko-KR%2520device%252FWin32%2520origin%252Fhttps%25253A%25252F%25252Fwww.tistory.com%26is_popup%3Dfalse%26through_account%3Dtrue&talk_login=hidden#login";
            driver.get(kakaoLoginUrl);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            Thread.sleep(3000);
            takeScreenshot(driver, "step1_login_page_loaded");

            WebElement idField = driver.findElement(By.name("loginId"));
            WebElement pwField = driver.findElement(By.name("password"));

            idField.click();
            idField.clear();
            idField.sendKeys(id);
            Thread.sleep(2000);
            takeScreenshot(driver, "step2_id_entered");

            pwField.click();
            pwField.clear();
            pwField.sendKeys(password);
            Thread.sleep(2000);
            takeScreenshot(driver, "step3_password_entered");

           /* try {
                WebElement iframe = new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.presenceOfElementLocated(
                                By.cssSelector("iframe[title='reCAPTCHA']")));

                driver.switchTo().frame(iframe);

                WebElement captchaBox = new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.elementToBeClickable(
                                By.cssSelector("div.recaptcha-checkbox-border")));

                captchaBox.click();
                Thread.sleep(3000);

                driver.switchTo().defaultContent();
            } catch (Exception e) {
                System.out.println("CAPTCHA 체크 실패: " + e.getMessage());
            }*/

            WebElement loginBtn = driver.findElement(By.cssSelector(".btn_g.highlight.submit"));
            js.executeScript("arguments[0].click();", loginBtn);
            Thread.sleep(9000);
            takeScreenshot(driver, "step4_after_login");

            // 쿠키 수집
            Set<Cookie> cookies = driver.manage().getCookies();
            for (Cookie cookie : cookies) {
                cookieStorage.put(cookie.getName(), cookie.getValue());
            }

        } catch (Exception e) {
            loginCheck = "에러발생";
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        response.put("status", loginCheck);
        response.put("cookies", cookieStorage);
        return ResponseEntity.ok(response);
    }

    private void takeScreenshot(WebDriver driver, String filename) {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File destFile = new File("/tmp/" + filename + ".png");
        try {
            FileHandler.copy(screenshot, destFile);
            System.out.println("[스크린샷 저장됨] " + destFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("스크린샷 저장 실패: " + filename);
            e.printStackTrace();
        }
    }

    @PostMapping("/tistoryPost")
    public ResponseEntity<?> tistoryPostWocky(
            @RequestHeader("X-Tistory-Cookie") String cookie,
            @RequestBody Map<String, Object> request
    ) {
        try {
            List<Map<String, Object>> places = (List<Map<String, Object>>) request.get("places");
            String title = (String) request.getOrDefault("title", "네이버 리뷰 모음");
            String tag = (String) request.getOrDefault("tag", "");
            String blogAddress = (String) request.getOrDefault("blogAddress", "");
            String blogSayHello = (String) request.getOrDefault("blogSayHello",
                    "네이버 플레이스 리뷰 모음집입니다 ㅎㅎ<br>방문하실때 참고해주세요.<br>");
            int placeCount = Integer.parseInt(request.getOrDefault("placeCount", "9999").toString());
            String userId = (String) request.getOrDefault("userId", "");

            // 🔍 2. 사용자 조회
            User userPostkey = userService.findByUserId(userId);
            if (userPostkey == null) {
                return ResponseEntity.status(401).body(Map.of("error", "존재하지 않는 아이디입니다."));
            }

            cookie = userPostkey.getPostKey();

            List<String> results = new ArrayList<>();

            int count = 0;
            for (Map<String, Object> place : places) {
                if (count >= placeCount) {
                    break;
                }
                count++;

                String placeId = (String) place.get("placeId");
                String placeName = (String) place.get("placeName");
                List<Map<String, Object>> reviews = (List<Map<String, Object>>) place.get("reviews");

                // 1. 본문 생성
                String content = generateHtmlContent(placeId, placeName, reviews, blogSayHello);

                // 2. 썸네일 추출
                String thumbnail = "";
                outerLoop:
                for (Map<String, Object> review : reviews) {
                    List<Map<String, Object>> images = (List<Map<String, Object>>) review.get("imageUrls");
                    if (images != null && !images.isEmpty()) {
                        thumbnail = (String) images.get(0).get("url");
                        break outerLoop;
                    }
                }

                // 3. 티스토리 포스트 payload 구성
                Map<String, Object> payload = new HashMap<>();
                payload.put("id", "0");
                payload.put("title", title + " - " + placeName);
                payload.put("content", content);
                payload.put("attachments", new ArrayList<>()); // 이미지 첨부 안 함
                payload.put("category", 0);
                payload.put("cclCommercial", 0);
                payload.put("cclDerive", 0);
                payload.put("draftSequence", null);
                payload.put("password", "4yMjYzMD");
                payload.put("published", 1);
                payload.put("visibility", 20);
                payload.put("recaptchaValue", "");
                payload.put("slogan", "네이버플레이스크롤링");
                payload.put("tag", tag);
                //payload.put("thumbnail", thumbnail);
                payload.put("type", "post");
                payload.put("uselessMarginForEntry", 0);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Cookie", cookie);
                headers.set("Origin", "https://" + blogAddress);
                headers.set("Referer",
                        "https://" + blogAddress + "/manage/newpost/?type=post&returnURL=%2Fmanage%2Fposts%2F");
                headers.set("User-Agent", "Mozilla/5.0");

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

                ResponseEntity<String> response = new RestTemplate().postForEntity(
                        "https://" + blogAddress + "/manage/post.json",
                        entity,
                        String.class
                );

                //성공했으면 포스팅 로그 ㅇ , 포인트 로그 ,
                PostingLog postingLog = new PostingLog();
                postingLog.setUserId(userId);
                postingLog.setBlogAccountId(blogAddress); // 포스팅 1건당 1포인트 차감 등
                postingLog.setTitle(title + " - " + placeName + " 포스팅");
                postingLog.setStatus("SUCCESS");
                postingLog.setCreatedAt(LocalDateTime.now().toString()); // String 형식
                postingLog.setAmount("-5");
                postingLog.setType("USE");
                postingLog.setDescription("티스토리 포스팅 차감");
                userService.insertPostingLog(postingLog);

                //포인트 차감 1. 포인트 차감
                User user = new User();
                user.setUserId(userId);
                user.setDeductPoint("-5");
                User t = userService.updatePointWallet(user);
                //results.add("✅ [" + placeName + "] 등록 성공: " + response.getBody() + "포인트:" + t.getPointBalance());
                results.add("✅ [" + placeName + "] 등록 성공: "
                        + response.getBody().trim()
                        + " || 포인트:" + t.getPointBalance());

            }

            return ResponseEntity.ok(results);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ 오류 발생: " + e.getMessage());
        }
    }

    @PostMapping("/tistoryPost_rkdl")
    public ResponseEntity<?> postToTistory(
            @RequestHeader("X-Tistory-Cookie") String cookie,
            @RequestBody Map<String, Object> request
    ) {
        try {
            List<Map<String, Object>> places = (List<Map<String, Object>>) request.get("places");
            String title = (String) request.getOrDefault("title", "네이버 리뷰 모음");
            String tag = (String) request.getOrDefault("tag", "");
            String blogSayHello = (String) request.getOrDefault("blogSayHello",
                    "네이버 플레이스 리뷰 모음집입니다 ㅎㅎ<br>방문하실때 참고해주세요.<br>");
            int placeCount = Integer.parseInt(request.getOrDefault("placeCount", "9999").toString());

            List<String> results = new ArrayList<>();

            int count = 0;
            for (Map<String, Object> place : places) {
                if (count >= placeCount) {
                    break;
                }
                count++;

                String placeId = (String) place.get("placeId");
                String placeName = (String) place.get("placeName");
                List<Map<String, Object>> reviews = (List<Map<String, Object>>) place.get("reviews");

                // 1. 본문 생성
                String content = generateHtmlContent(placeId, placeName, reviews, blogSayHello);

                // 2. 썸네일 추출
                String thumbnail = "";
                outerLoop:
                for (Map<String, Object> review : reviews) {
                    List<Map<String, Object>> images = (List<Map<String, Object>>) review.get("imageUrls");
                    if (images != null && !images.isEmpty()) {
                        thumbnail = (String) images.get(0).get("url");
                        break outerLoop;
                    }
                }

                // 3. 티스토리 포스트 payload 구성
                Map<String, Object> payload = new HashMap<>();
                payload.put("id", "0");
                payload.put("title", title + " - " + placeName);
                payload.put("content", content);
                payload.put("attachments", new ArrayList<>()); // 이미지 첨부 안 함
                payload.put("category", 0);
                payload.put("cclCommercial", 0);
                payload.put("cclDerive", 0);
                payload.put("draftSequence", null);
                payload.put("password", "44MTUwMD");
                payload.put("published", 1);
                payload.put("visibility", 20);
                payload.put("recaptchaValue", "");
                payload.put("slogan", "네이버플레이스크롤링2");
                payload.put("tag", tag);
                //payload.put("thumbnail", thumbnail);
                payload.put("type", "post");
                payload.put("uselessMarginForEntry", 0);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Cookie", cookie);
                headers.set("Origin", "https://gaiary.tistory.com");
                headers.set("Referer",
                        "https://gaiary.tistory.com/manage/newpost/?type=post&returnURL=%2Fmanage%2Fposts%2F");
                headers.set("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36");

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

                ResponseEntity<String> response = new RestTemplate().postForEntity(
                        "https://gaiary.tistory.com/manage/post.json",
                        entity,
                        String.class
                );

                results.add("✅ [" + placeName + "] 등록 성공: " + response.getBody());
            }

            return ResponseEntity.ok(results);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ 오류 발생: " + e.getMessage());
        }
    }

    @PostMapping("/tistoryPost2")
    public ResponseEntity<?> postToTistory2(
            @RequestHeader("X-Tistory-Cookie") String cookie,
            @RequestBody Map<String, Object> request
    ) {
        try {
            // 👇 리뷰 데이터 받기
            List<Map<String, Object>> places = (List<Map<String, Object>>) request.get("places");

            StringBuilder contentBuilder = new StringBuilder();

            String placeName = null;
            for (Map<String, Object> place : places) {
                placeName = (String) place.get("placeName");
                List<Map<String, Object>> reviews = (List<Map<String, Object>>) place.get("reviews");

                contentBuilder.append("<h2>").append(placeName).append("</h2>");

                for (Map<String, Object> review : reviews) {
                    String body = (String) review.get("body");
                    contentBuilder.append("<p>").append(body).append("</p>");

                    List<Map<String, Object>> imageUrls = (List<Map<String, Object>>) review.get("imageUrls");
                    if (imageUrls != null) {
                        for (Map<String, Object> img : imageUrls) {
                            String imgUrl = (String) img.get("url");
                            contentBuilder.append("<p><img src=\"")
                                    .append(imgUrl)
                                    .append("\" style=\"alignCenter\" /></p>");
                        }
                    }
                }
            }

            String content = contentBuilder.toString();

            // 첫 이미지가 썸네일로
            String thumbnail = "";
            for (Map<String, Object> place : places) {
                List<Map<String, Object>> reviews = (List<Map<String, Object>>) place.get("reviews");
                for (Map<String, Object> review : reviews) {
                    List<Map<String, Object>> imageUrls = (List<Map<String, Object>>) review.get("imageUrls");
                    if (imageUrls != null && !imageUrls.isEmpty()) {
                        thumbnail = (String) imageUrls.get(0).get("url");
                        break;
                    }
                }
                if (!thumbnail.isEmpty()) {
                    break;
                }
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("id", "0");
            payload.put("title", "수집된 네이버 플레이스 리뷰");
            payload.put("content", content);
            payload.put("attachments", new ArrayList<>()); // 첨부 안함
            payload.put("category", 0);
            payload.put("cclCommercial", 1);
            payload.put("cclDerive", 1);
            payload.put("password", "45MTg1NT");
            payload.put("published", 1);
            payload.put("visibility", 20);
            payload.put("recaptchaValue", "");
            payload.put("slogan", "네이버플레이스크롤링");
            payload.put("tag", "맛집, 네이버플레이스");
            payload.put("thumbnail", thumbnail);
            payload.put("type", "post");
            payload.put("uselessMarginForEntry", 0);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Cookie", cookie);
            headers.set("Origin", "https://wockytest.tistory.com");
            headers.set("Referer", "https://wockytest.tistory.com/manage/newpost/");
            headers.set("User-Agent", "Mozilla/5.0");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = new RestTemplate().postForEntity(
                    "https://wockytest.tistory.com/manage/post.json",
                    entity,
                    String.class
            );

            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public String generateHtmlContent(String placeId, String placeName, List<Map<String, Object>> reviews,
                                      String helloText) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>").append(placeName).append("</h2>\n");
        sb.append(helloText).append("<br>");

        for (int i = 0; i < reviews.size(); i++) {
            Map<String, Object> review = reviews.get(i);
            String body = (String) review.get("body");
            List<Map<String, Object>> imageUrls = (List<Map<String, Object>>) review.get("imageUrls");

            String[] lines = body.split("\n");
            int interval = imageUrls != null && !imageUrls.isEmpty()
                    ? Math.max(1, lines.length / (imageUrls.size() + 1))
                    : lines.length + 1;

            StringBuilder modified = new StringBuilder();
            int imageIndex = 0;

            for (int j = 0; j < lines.length; j++) {
                modified.append(lines[j]).append("<br>");

                // 간격에 맞춰 이미지 삽입
                if ((j + 1) % interval == 0 && imageUrls != null && imageIndex < imageUrls.size()) {
                    String imgUrl = (String) imageUrls.get(imageIndex).get("url");
                    modified.append("<img src='").append(imgUrl).append("' style='max-width:100%;'><br>");
                    imageIndex++;
                }
            }

            // 아직 남은 이미지가 있다면 본문 끝에 모두 삽입
            if (imageUrls != null) {
                while (imageIndex < imageUrls.size()) {
                    String imgUrl = (String) imageUrls.get(imageIndex).get("url");
                    modified.append("<img src='").append(imgUrl).append("' style='max-width:100%;'><br>");
                    imageIndex++;
                }
            }

            sb.append("<h3>리뷰 ").append(i + 1).append("</h3>\n<p>").append(modified).append("</p>\n");
        }

        // 메뉴 추가
        List<Map<String, Object>> menuList = getMenuList(placeId);
        if (!menuList.isEmpty()) {
            sb.append("<h2>메뉴</h2>\n");
            for (Map<String, Object> menu : menuList) {
                sb.append("<p><strong>").append(menu.get("name")).append("</strong> - ").append(menu.get("price"))
                        .append("</p>\n");
                List<String> imgs = (List<String>) menu.get("images");
                for (String img : imgs) {
                    sb.append("<img src='").append(img).append("' style='max-width:100%;'><br>");
                }
            }
        }

        return sb.toString();
    }


    public List<Map<String, Object>> getMenuList(String placeId) throws Exception {
        String apiUrl = "https://pcmap.place.naver.com/restaurant/" + placeId + "/home";

        // 1. URL 연결
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setRequestProperty("Referer", "https://pcmap.place.naver.com/");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        // 2. 응답 읽기
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            return Collections.emptyList();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine).append("\n");
        }
        in.close();
        conn.disconnect();

        // 3. 스크립트에서 window.__APOLLO_STATE__ 추출
        String html = response.toString();
        Pattern pattern = Pattern.compile("window\\.__APOLLO_STATE__ = (\\{.*?\\});", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        if (!matcher.find()) {
            return Collections.emptyList();
        }

        String jsonStr = matcher.group(1);

        // 4. JSON 파싱
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> json = mapper.readValue(jsonStr, Map.class);

        // 5. 메뉴 정보 필터링
        List<Map<String, Object>> menuList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : json.entrySet()) {
            if (entry.getKey().startsWith("Menu:")) {
                Map<String, Object> val = (Map<String, Object>) entry.getValue();
                Map<String, Object> menuItem = new HashMap<>();
                menuItem.put("name", val.get("name"));
                menuItem.put("price", val.get("price"));
                menuItem.put("images", val.getOrDefault("images", new ArrayList<>()));
                menuList.add(menuItem);
            }
        }

        return menuList;
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


    @PostMapping("/nplaceReview")
    public ResponseEntity<?> crawlNaverPlace(@RequestBody CrawlerRequest req) {
        try {
            String query = URLEncoder.encode(req.getSearchQuery(), StandardCharsets.UTF_8);
            int placeCount = req.getPlaceCount();
            int reviewCount = req.getReviewCount();

            // Step 1: 장소 목록 얻기
            List<Map<String, String>> places = NaverMapAPI.getPlaceList(query, placeCount);

            // Step 2: Selenium으로 쿠키 수집 (1개만 로그인)
            String placeId = places.get(0).get("place_id");
            Map<String, String> cookies = SeleniumHelper.getCookiesForPlace(placeId);

            // Step 3: 각 장소마다 리뷰 수집
            List<Map<String, Object>> results = new ArrayList<>();
            for (Map<String, String> place : places) {
                Map<String, Object> placeResult = NaverGraphQLAPI.fetchReviews(place, cookies, reviewCount);
                results.add(placeResult);
            }

            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("크롤링 실패: " + e.getMessage());
        }
    }

    @Data
    public static class CrawlerRequest {
        private String searchQuery;
        private int placeCount;
        private int reviewCount;

    }

    public static class NaverMapAPI {
        public static List<Map<String, String>> getPlaceList(String query, int placeCount) throws IOException {
            List<Map<String, String>> results = new ArrayList<>();

            String urlStr = "https://map.naver.com/p/api/search/instant-search?query="
                    + query
                    + "&coords=37.517305,127.047502";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36");
            conn.setRequestProperty("Accept", "application/json, text/plain, */*");
            conn.setRequestProperty("Referer",
                    "https://map.naver.com/p/search/" + query
                            + "?searchType=place");
            conn.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");
            conn.setRequestProperty("sec-ch-ua",
                    "\"Google Chrome\";v=\"135\", \"Not-A.Brand\";v=\"8\", \"Chromium\";v=\"135\"");
            conn.setRequestProperty("sec-ch-ua-mobile", "?0");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            conn.disconnect();

            // JSON 파싱
            String json = response.toString();
            org.json.JSONObject obj = new org.json.JSONObject(json);
            org.json.JSONArray placeArr = obj.getJSONArray("place");

            Set<String> seen = new HashSet<>();

            for (int i = 0; i < placeArr.length() && results.size() < placeCount; i++) {
                org.json.JSONObject place = placeArr.getJSONObject(i);
                String placeId = place.getString("id");
                if (seen.contains(placeId)) {
                    continue;
                }
                seen.add(placeId);

                Map<String, String> map = new HashMap<>();
                map.put("place_id", placeId);
                map.put("place_name", place.getString("title"));
                map.put("address", place.optString("jibunAddress", ""));
                map.put("roadAddress", place.optString("roadAddress", ""));
                results.add(map);
            }

            return results;
        }
    }


    public static class SeleniumHelper {
        public static Map<String, String> getCookiesForPlace(String placeId) {
            // ✅ chromedriver 자동 설치 및 경로 설정
            WebDriverManager.chromedriver().setup();

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-gpu");
            options.addArguments(
                    "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36");

            WebDriver driver = new ChromeDriver(options);
            String url = "https://pcmap.place.naver.com/restaurant/" + placeId + "/review/visitor";
            driver.get(url);

            try {
                Thread.sleep(2000);  // ✅ WebDriverWait 사용하면 더 안정적
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Map<String, String> cookies = new HashMap<>();
            for (org.openqa.selenium.Cookie cookie : driver.manage().getCookies()) {
                cookies.put(cookie.getName(), cookie.getValue());
            }

            driver.quit();
            return cookies;
        }
    }

    public static class NaverGraphQLAPI {
        public static Map<String, Object> fetchReviews(Map<String, String> place, Map<String, String> cookies,
                                                       int reviewCount) {
            String placeId = place.get("place_id");

            // GraphQL Query
            String query = """
                    query getVisitorReviews($input: VisitorReviewsInput) {
                      visitorReviews(input: $input) {
                        items {
                          id
                          rating
                          body
                          created
                          author {
                            nickname
                            imageUrl
                          }
                          media {
                            type
                            thumbnail
                            thumbnailRatio
                          }
                        }
                        total
                      }
                    }
                    """;

            // JSON Payload
            JSONObject input = new JSONObject();
            input.put("businessId", placeId);
            input.put("businessType", "restaurant");
            input.put("item", "0");
            input.put("page", 1);
            input.put("size", reviewCount);
            input.put("isPhotoUsed", false);
            input.put("includeContent", true);
            input.put("getUserStats", true);
            input.put("includeReceiptPhotos", true);
            input.put("cidList", new JSONArray());
            input.put("getReactions", true);
            input.put("getTrailer", true);

            JSONObject variables = new JSONObject();
            variables.put("input", input);

            JSONObject payload = new JSONObject();
            payload.put("operationName", "getVisitorReviews");
            payload.put("variables", variables);
            payload.put("query", query);

            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36");
            headers.set("Referer", "https://pcmap.place.naver.com/restaurant/" + placeId + "/review/visitor");
            headers.set("Origin", "https://pcmap.place.naver.com");
            headers.set("Accept", "*/*");
            headers.set("x-ncaptcha-violation", "false");
            headers.set("x-wtm-graphql", generateXWtmGraphQL(placeId));
            headers.set("Accept-Language", "ko");
            headers.set("sec-ch-ua-platform", "\"Windows\"");
            headers.set("sec-ch-ua", "\"Google Chrome\";v=\"135\", \"Not-A.Brand\";v=\"8\", \"Chromium\";v=\"135\"");
            headers.set("sec-ch-ua-mobile", "?0");
            headers.set("Sec-Fetch-Site", "same-site");
            headers.set("Sec-Fetch-Mode", "cors");
            headers.set("Sec-Fetch-Dest", "empty");

            // 쿠키 설정
            if (cookies != null && !cookies.isEmpty()) {
                StringBuilder cookieHeader = new StringBuilder();
                for (Map.Entry<String, String> entry : cookies.entrySet()) {
                    cookieHeader.append(entry.getKey()).append("=").append(entry.getValue()).append("; ");
                }
                headers.set("Cookie", cookieHeader.toString());
            }

            // HttpEntity
            HttpEntity<String> entity = new HttpEntity<>(payload.toString(), headers);

            // 요청
            String url = "https://pcmap-api.place.naver.com/graphql";
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            System.out.println("📥 서버 응답: " + response.getStatusCodeValue());
            System.out.println("📄 응답 본문: " + response.getBody());

            JSONObject obj = new JSONObject(response.getBody());
            JSONArray items = obj.getJSONObject("data").getJSONObject("visitorReviews").getJSONArray("items");

            List<Map<String, Object>> reviewList = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                Map<String, Object> review = new HashMap<>();
                review.put("body", item.optString("body"));

                List<Map<String, Object>> images = new ArrayList<>();
                JSONArray mediaArr = item.optJSONArray("media");
                if (mediaArr != null) {
                    for (int j = 0; j < mediaArr.length(); j++) {
                        JSONObject media = mediaArr.getJSONObject(j);
                        if ("image".equals(media.optString("type"))) {
                            Map<String, Object> img = new HashMap<>();
                            img.put("url", media.optString("thumbnail"));
                            img.put("ratio", media.optDouble("thumbnailRatio"));
                            images.add(img);
                        }
                    }
                }

                review.put("imageUrls", images);
                reviewList.add(review);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("placeName", place.get("place_name"));
            result.put("placeId", place.get("place_id"));
            result.put("address", place.get("address"));
            result.put("roadAddress", place.get("roadAddress"));
            result.put("reviews", reviewList);
            return result;
        }
    }

    public static Map<String, String> getNaverCookiesFromPlace(String placeId) {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        options.addArguments(
                "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36");

        WebDriver driver = new ChromeDriver(options);
        String url = "https://pcmap.place.naver.com/restaurant/" + placeId + "/review/visitor";
        driver.get(url);

        try {
            Thread.sleep(3000); // 페이지 렌더링 대기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map<String, String> cookieMap = new HashMap<>();
        for (Cookie cookie : driver.manage().getCookies()) {
            cookieMap.put(cookie.getName(), cookie.getValue());
        }

        driver.quit();
        return cookieMap;
    }

    private static Map<String, String> buildReviewHeaders(String placeId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36");
        headers.put("Referer", "https://pcmap.place.naver.com/restaurant/" + placeId + "/review/visitor");
        headers.put("Origin", "https://pcmap.place.naver.com");
        headers.put("Accept", "*/*");
        headers.put("Content-Type", "application/json");
        headers.put("x-ncaptcha-violation", "false");
        headers.put("x-wtm-graphql", generateXWtmGraphQL(placeId));
        headers.put("Accept-Language", "ko");
        headers.put("sec-ch-ua-platform", "\"Windows\"");
        headers.put("sec-ch-ua", "\"Google Chrome\";v=\"135\", \"Not-A.Brand\";v=\"8\", \"Chromium\";v=\"135\"");
        headers.put("sec-ch-ua-mobile", "?0");
        headers.put("Sec-Fetch-Site", "same-site");
        headers.put("Sec-Fetch-Mode", "cors");
        headers.put("Sec-Fetch-Dest", "empty");
        return headers;
    }

    private static String generateXWtmGraphQL(String placeId) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("arg", placeId);
            payload.put("type", "restaurant");
            payload.put("source", "place");

            String jsonStr = new org.json.JSONObject(payload).toString();
            return Base64.getEncoder().encodeToString(jsonStr.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}