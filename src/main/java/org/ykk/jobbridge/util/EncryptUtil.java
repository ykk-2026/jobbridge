package org.ykk.jobbridge.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;





public class EncryptUtil {


    private static final String addMessage = "JobBridgeKopoPoly";


    private static final byte[] ivBytes = {
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };


    private static final String key = "JobBridgeSecretK";





    public static String encHashSHA256(String str) {

        String res = "";


        String plantText = addMessage + str;

        try {

            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(plantText.getBytes());
            byte[] byteData = sh.digest();


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





    public static String encAES128CBC(String str) throws Exception {


        byte[] textBytes = str.getBytes(StandardCharsets.UTF_8);


        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");


        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);


        return Base64.getEncoder().encodeToString(cipher.doFinal(textBytes));
    }




    public static String decAES128CBC(String str) throws Exception {


        byte[] textBytes = Base64.getDecoder().decode(str);


        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");


        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);


        return new String(cipher.doFinal(textBytes), StandardCharsets.UTF_8);
    }
}
