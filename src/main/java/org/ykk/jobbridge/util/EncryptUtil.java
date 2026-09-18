package org.ykk.jobbridge.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

/**
 * 암호화 유틸리티 클래스
 * 공통 암호화/복호화 메서드 제공 (AES 128 CBC와 SHA-256 알고리즘 지원)
 */
public class EncryptUtil {

    // SHA-256 해시 암호화할 때 사용되는 Salt 값
    private static final String addMessage = "JobBridgeKopoPoly";

    // AES CBC 초기 벡터(IV), 복호화 시 동일해야 함
    private static final byte[] ivBytes = {
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };

    // AES 대칭키 값 (16바이트 -> 1바이트는 8bit -> 128bit)
    private static final String key = "JobBridgeSecretK";

    /**
     * 단방향 해시 암호화 (SHA-256) - 복호화(암호풀기) 불가능
     * 비밀번호 저장할 때 사용함
     */
    public static String encHashSHA256(String str) {

        String res = "";

        // 입력 문자열에 Salt(addMessage) 결합
        String plantText = addMessage + str;

        try {
            // SHA-256 해시 알고리즘 적용
            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(plantText.getBytes());
            byte[] byteData = sh.digest();

            // 64자리 Hex 문자열로 변환
            StringBuilder sb = new StringBuilder();
            for (byte byteDatum : byteData) {
                sb.append(Integer.toString((byteDatum & 0xff) + 0x100, 16).substring(1));
            }

            res = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            res = "";
        }

        return res;
    }

    /**
     * AES128 CBC 암호화 - 복호화(암호풀기) 가능
     * 이메일 등 개인정보 저장할 때 사용함
     */
    public static String encAES128CBC(String str) throws Exception {

        // 평문 문자열을 바이트 배열로 변환
        byte[] textBytes = str.getBytes(StandardCharsets.UTF_8);

        // 키와 IV 객체 생성
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

        // Cipher 객체를 AES/CBC/PKCS5Padding으로 초기화
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);

        // 암호화 실행 후 Base64 인코딩 반환
        return Base64.getEncoder().encodeToString(cipher.doFinal(textBytes));
    }

    /**
     * AES128 CBC 복호화
     */
    public static String decAES128CBC(String str) throws Exception {

        // Base64 디코딩으로 암호문을 바이트 배열로 변환
        byte[] textBytes = Base64.getDecoder().decode(str);

        // 키와 IV 객체 생성
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

        // Cipher 객체를 AES/CBC/PKCS5Padding으로 초기화 (DECRYPT_MODE)
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);

        // 복호화 실행 후 평문 문자열 반환
        return new String(cipher.doFinal(textBytes), StandardCharsets.UTF_8);
    }
}
