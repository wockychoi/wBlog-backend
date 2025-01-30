package com.mapago.service.bizcall;

import com.mapago.mapper.shop.ShopMapper;
import com.mapago.model.shop.Shop;
import com.mapago.service.shop.ShopService;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class BizcallService {

    private static ShopService shopService;


    private final ShopMapper shopMapper;

    public BizcallService(ShopMapper shopMapper) {
        this.shopMapper = shopMapper;
    }

    public void get_vns(Shop shop) throws Exception {
        try {
            // 요청할 URL 설정
            URL url = new URL("https://api.050bizcall.co.kr/link/get_vns.do"); // 실제 요청할 URL로 변경
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // POST 방식 설정
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            // 파라미터 설정
            String iid = "qufnzhopllgveu1198tg";
            String mmdd = "1023";
            String auth = md5AndHexBase64(iid + mmdd); // 올바른 auth 값으로 변경

            String parameters = "iid=" + iid + "&mmdd=" + mmdd + "&auth=" + auth;

            // 요청 바디에 파라미터를 쓰기
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = parameters.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 응답 코드 확인
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 응답 데이터 읽기
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    // 응답 데이터 출력
                    System.out.println("응답 데이터: " + response.toString());

                    // JSON 응답 파싱
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.has("rec")) {
                        JSONArray recArray = jsonResponse.getJSONArray("rec");

                        // 배열의 각 항목에 대해 반복
                        for (int i = 0; i < recArray.length(); i++) {
                            JSONObject recItem = recArray.getJSONObject(i);

                            // "vn" 값이 존재하면 shop 객체에 설정
                            if (recItem.has("vn")) {
                                String bizCall = recItem.getString("vn");
                                shop.setPhoneBizNum(bizCall); // shop 객체에 vn 값 설정
                                shop.setShopId(i + 1);
                                shopMapper.shopBizCall(shop); // DB 업데이트
                            }
                        }
                    } else {
                        System.out.println("응답에 'rec' 배열이 없습니다.");
                    }
                }
            } else {
                System.out.println("요청 실패, 응답 코드: " + responseCode);
                try (BufferedReader errorStream = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = errorStream.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }
                    // 에러 응답 출력
                    System.out.println("에러 응답: " + errorResponse.toString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void set_vn(Shop shop) throws Exception {
        try {
            // shop 전체 리스트를 가져오고 가상번호 착신번호 가져와서 설정
            List<Shop> shopList = shopMapper.findShopListAll();

            // shopList의 사이즈만큼 for문을 돌림
            for (int i = 0; i < shopList.size(); i++) {
                Shop tempshop = shopList.get(i);

                // 각 shop 객체에 대한 작업 수행
                System.out.println("Shop ID: " + tempshop.getShopId());
                System.out.println("Shop Phone: " + tempshop.getPhoneNum());
                System.out.println("Shop Biz_Phone: " + tempshop.getPhoneBizNum());

                // 요청할 URL 설정
                URL url = new URL("https://api.050bizcall.co.kr/link/set_vn.do");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // POST 방식 설정
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setDoOutput(true);

                // id + vn을 MD5 암호화 후 Base64 인코딩
                String iid = "qufnzhopllgveu1198tg";
                String tempAuth = iid + tempshop.getPhoneBizNum();
                String tempAuth2 = md5AndHexBase64(tempAuth);

                System.out.println("auth: " + tempAuth2);

                // 파라미터 설정
                String parameters =
                        "iid=" + iid + "&vn=" + tempshop.getPhoneBizNum() + "&rn=" + tempshop.getPhoneNum() + "&auth="
                                + tempAuth2 + "&cr_id=70094&if_id=49045";
                System.out.println("요청 번호: " + parameters);
                // 요청 바디에 파라미터 쓰기
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = parameters.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                // 응답 코드 확인
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 응답 데이터 읽기
                    try (BufferedReader in = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            response.append(line);
                        }
                        // 응답 데이터 출력
                        System.out.println("응답 데이터: " + response.toString());

                    }
                } else {
                    System.out.println("요청 실패, 응답 코드: " + responseCode);
                    try (BufferedReader errorStream = new BufferedReader(
                            new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                        StringBuilder errorResponse = new StringBuilder();
                        String errorLine;
                        while ((errorLine = errorStream.readLine()) != null) {
                            errorResponse.append(errorLine);
                        }
                        // 에러 응답 출력
                        System.out.println("에러 응답: " + errorResponse.toString());
                    }
                }

                // 연결 종료
                connection.disconnect();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String encode(String text) {
        try {
            // MD5 해시 알고리즘을 사용
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5Hash = md.digest(text.getBytes(StandardCharsets.UTF_8));

            // Base64 인코딩
            String base64Encoded = Base64.getEncoder().encodeToString(md5Hash);

            return base64Encoded;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String md5AndHexBase64(String input) {
        try {
            // MD5 해시 생성
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5Hash = md.digest(input.getBytes(StandardCharsets.UTF_8));

            // MD5 해시 값을 16진수 문자열로 변환
            StringBuilder hexString = new StringBuilder();
            for (byte b : md5Hash) {
                hexString.append(String.format("%02x", b));
            }
            String md5Hex = hexString.toString();
            //System.out.println("MD5 해시 (16진수): " + md5Hex);

            // 16진수 문자열을 다시 바이트 배열로 변환
            byte[] hexBytes = md5Hex.getBytes(StandardCharsets.UTF_8);

            // Base64 인코딩 후 값 출력
            String base64Encoded = Base64.getEncoder().encodeToString(hexBytes);
            // System.out.println("Base64 인코딩된 값 (16진수 문자열 기반): " + base64Encoded);

            return base64Encoded;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


}