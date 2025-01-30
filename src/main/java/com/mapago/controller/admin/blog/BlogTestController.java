package com.mapago.controller.admin.blog;

import com.mapago.model.user.User;
import com.mapago.service.user.UserService;
import com.mapago.util.AesUtil;
import com.mapago.util.JwtUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/blog")
public class BlogTestController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

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

        // Selenium WebDriver 설정
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver"); // chromedriver 경로 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "window-size=1920x1080", "--disable-extensions");

        WebDriver driver = new ChromeDriver(options);

        // 네이버 로그인 페이지 접속
        driver.get("https://nid.naver.com/nidlogin.login");

        // ID 및 비밀번호 입력
        WebElement idField = driver.findElement(By.name("id"));
        WebElement pwField = driver.findElement(By.name("pw"));

        idField.sendKeys(id);
        pwField.sendKeys(password);

        // 로그인 버튼 클릭
        WebElement loginButton = driver.findElement(By.id("log.login"));
        loginButton.click();

        // 로그인 결과 확인
        String loginCheck;
        try {
            WebElement protectionCheck = driver.findElement(By.className("btn_next.detect_clear"));
            loginCheck = protectionCheck.getText().contains("보호조치") ? "보호조치" : "로그인성공";
        } catch (Exception e) {
            loginCheck = "로그인성공";
        }

        return null;
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