package top.ezadmin.common.utils;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;


public class DESUtils {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    /**
     * DES密钥
     */
    private static String key = "0123456789abcdef";

    //    public static void key(String ke){
//        key=ke;
//    }
    public static void init(String password) {
        if (StringUtils.isNotBlank(password)) {
            key = password;
        }
    }
    public static String encrypt(String plaintext ) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String ciphertext ) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    /**
     * DES加密
     *
     */
    public static String encryptDES(Object object) throws Exception {
        return encryptDES(Utils.trimNull(object));
    }
    public static String encryptDES(String data) throws Exception {
        return encrypt(data);
    }



    /**
     * DES解密
     * @throws Exception
     */
    public static String decryptDES(String data) throws Exception {
        return decrypt(data);
    }

    public static String md5(InputStream inputStream){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");

            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }
            byte[] digest = md.digest();

            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
