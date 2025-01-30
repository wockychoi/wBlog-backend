package com.mapago.controller.portal.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mapago.model.user.User;
import com.mapago.service.shop.ShopService;
import com.mapago.service.user.UserService;
import com.mapago.util.AesUtil;
import com.mapago.util.JwtUtil;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portal/api/user")
public class PtlUserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String CLIENT_ID = "KnkwGYYtlZM4zwvPXqmK";
    private static final String REDIRECT_URI = "http://localhost:8080/portal/api/user/naverLoginCallback";
    private static final String CLIENT_SECRET = "sw3vVe9WUk";
    private static final String STATE = "RANDOM_STRING";  // 보안을 위한 임의의 문자열

    private static final String CLIENT_ID_KAKAO = "6167809099931a632e84191909f83003";
    private static final String REDIRECT_URI_KAKAO = "http://localhost:8080/portal/api/user/kakaoLoginCallback";

    private static final String CLIENT_ID_APPLE = "YOUR_CLIENT_ID";
    private static final String REDIRECT_URI_APPLE = "YOUR_REDIRECT_URI";

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) throws Exception {
        try {
            String decryptedPassword = AesUtil.decrypt(loginRequest.getPassword());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUserId(), decryptedPassword)
            );
            UserDetails userDetails = userService.loadUserByUsername(loginRequest.getUserId());
            User user = null;
            if (userDetails.isEnabled()) {
                user = userService.findByUserId(loginRequest.getUserId());
                user.setRoles(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
            }
            String accessToken = jwtUtil.generatePortalAccessToken(userDetails.getUsername());
            String refreshToken = jwtUtil.generatePortalRefreshToken(userDetails.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("user", user);
            response.put("ownerShopList", shopService.findOwnerShopList(user.getUserId()));
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(Map.of("error", "아이디 또는 비밀번호를 확인해주세요."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "오류가 발생했습니다. 운영자에게 문의 해주세요."));
        }

    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody User userRequest) throws Exception {
        User validateUser = userService.findByUserId(userRequest.getUserId());
        if (validateUser != null) {
            throw new DataIntegrityViolationException("이미 사용중인 아이디 입니다.[" + userRequest.getUserId() + "]");
        }

        validateUser = userService.findByNickname(userRequest.getNickname());
        if (validateUser != null) {
            throw new DataIntegrityViolationException("이미 사용중인 닉네임 입니다.[" + userRequest.getNickname() + "]");
        }
        String decryptPassword = AesUtil.decrypt(userRequest.getPassword());
        userRequest.setPassword(passwordEncoder.encode(decryptPassword));
        userRequest.setRoles(List.of("ROLE_USER"));

        userService.insertUser(userRequest);
        userService.insertUserRole(userRequest);
        User user = userService.findByUserId(userRequest.getUserId());
        user.setRoles(List.of("ROLE_USER"));
        String accessToken = jwtUtil.generatePortalAccessToken(user.getUserId());
        String refreshToken = jwtUtil.generatePortalRefreshToken(user.getUserId());

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("user", user);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/naverLogin")
    public String getnaverLogin() throws Exception {
        String encodedRedirectURI = URLEncoder.encode(REDIRECT_URI, "UTF-8");
        String url = "https://nid.naver.com/oauth2.0/authorize?response_type=code"
                + "&client_id=" + CLIENT_ID
                + "&redirect_uri=" + encodedRedirectURI
                + "&state=" + STATE;
        return url;
    }

    @GetMapping("/naverLoginCallback2")
    public String naverLoginCallback2(String code, String state) throws IOException {
        String tokenUrl = "https://nid.naver.com/oauth2.0/token";
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost post = new HttpPost(tokenUrl);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        StringBuilder params = new StringBuilder();
        params.append("grant_type=authorization_code");
        params.append("&client_id=").append(CLIENT_ID);
        params.append("&client_secret=").append(CLIENT_SECRET);
        params.append("&code=").append(code);
        params.append("&state=").append(state);
        params.append("&redirect_uri=").append(URLEncoder.encode(REDIRECT_URI, "UTF-8"));

        post.setEntity(new StringEntity(params.toString()));

        HttpResponse response = httpClient.execute(post);
        String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");

        JsonObject jsonResponse = JsonParser.parseString(responseString).getAsJsonObject();
        return jsonResponse.get("access_token").getAsString();
    }

    @GetMapping("/naverLoginCallback")
    public JsonObject naverLoginCallback(String code, String state) throws IOException {
        String tokenUrl = "https://nid.naver.com/oauth2.0/token";
        String userInfoUrl = "https://openapi.naver.com/v1/nid/me";

        // Create HTTP client
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 1. 액세스 토큰 요청
        HttpPost post = new HttpPost(tokenUrl);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        StringBuilder params = new StringBuilder();
        params.append("grant_type=authorization_code");
        params.append("&client_id=").append(CLIENT_ID);
        params.append("&client_secret=").append(CLIENT_SECRET);
        params.append("&code=").append(code);
        params.append("&state=").append(state);
        params.append("&redirect_uri=").append(URLEncoder.encode(REDIRECT_URI, "UTF-8"));

        post.setEntity(new StringEntity(params.toString()));
        HttpResponse tokenResponse = httpClient.execute(post);
        String tokenResponseString = EntityUtils.toString(tokenResponse.getEntity(), "UTF-8");

        JsonObject tokenJson = JsonParser.parseString(tokenResponseString).getAsJsonObject();
        String accessToken = tokenJson.get("access_token").getAsString();

        // 2. 사용자 정보 요청
        HttpGet get = new HttpGet(userInfoUrl);
        get.setHeader("Authorization", "Bearer " + accessToken);

        HttpResponse userInfoResponse = httpClient.execute(get);
        String userInfoResponseString = EntityUtils.toString(userInfoResponse.getEntity(), "UTF-8");

        JsonObject userInfoJson = JsonParser.parseString(userInfoResponseString).getAsJsonObject();
        JsonObject userInfo = userInfoJson.getAsJsonObject("response");

        return userInfo;
    }

    @PostMapping("/kakaoLogin")
    public String getkakaoLogin() throws Exception {
        String encodedRedirectURI = URLEncoder.encode(REDIRECT_URI_KAKAO, "UTF-8");
        String url = "https://kauth.kakao.com/oauth/authorize?response_type=code"
                + "&client_id=" + CLIENT_ID_KAKAO
                + "&redirect_uri=" + encodedRedirectURI;
        return url;
    }

    @GetMapping("/kakaoLoginCallback")
    public JsonObject kakaoLoginCallback(String code) throws IOException {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        // Create HTTP client
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 1. 액세스 토큰 요청
        HttpPost post = new HttpPost(tokenUrl);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        StringBuilder params = new StringBuilder();
        params.append("grant_type=authorization_code");
        params.append("&client_id=").append(CLIENT_ID_KAKAO);
        /*if (CLIENT_SECRET != null && !CLIENT_SECRET.isEmpty()) {
            params.append("&client_secret=").append(CLIENT_SECRET);
        }*/
        params.append("&redirect_uri=").append(URLEncoder.encode(REDIRECT_URI_KAKAO, "UTF-8"));
        params.append("&code=").append(code);

        post.setEntity(new StringEntity(params.toString()));
        HttpResponse tokenResponse = httpClient.execute(post);
        String tokenResponseString = EntityUtils.toString(tokenResponse.getEntity(), "UTF-8");

        JsonObject tokenJson = JsonParser.parseString(tokenResponseString).getAsJsonObject();
        String accessToken = tokenJson.get("access_token").getAsString();

        // 2. 사용자 정보 요청
        HttpGet get = new HttpGet(userInfoUrl);
        get.setHeader("Authorization", "Bearer " + accessToken);

        HttpResponse userInfoResponse = httpClient.execute(get);
        String userInfoResponseString = EntityUtils.toString(userInfoResponse.getEntity(), "UTF-8");

        JsonObject userInfoJson = JsonParser.parseString(userInfoResponseString).getAsJsonObject();
        return userInfoJson;
    }


}