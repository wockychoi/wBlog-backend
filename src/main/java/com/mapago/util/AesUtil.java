package com.mapago.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AesUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    // AES-256 고정 키
    private static final String SECRET_KEY = "mySuperSecretKey32BytesLengthoo";

    // 암호화 메서드
    public static String encrypt(String plainText) throws Exception {
        SecretKeySpec key = getSecretKeySpec(SECRET_KEY);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // 복호화 메서드
    public static String decrypt(String encryptedText) throws Exception {
        SecretKeySpec key = getSecretKeySpec(SECRET_KEY);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    // 32바이트 SecretKeySpec 생성
    private static SecretKeySpec getSecretKeySpec(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] paddedKeyBytes = new byte[32]; // 32바이트 배열 생성
        System.arraycopy(keyBytes, 0, paddedKeyBytes, 0, Math.min(keyBytes.length, 32)); // 키 패딩
        return new SecretKeySpec(paddedKeyBytes, ALGORITHM);
    }
}