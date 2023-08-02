package com.ezadmin.common.utils;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;



public class DESUtils {

    /**
     * DES密钥
     */
    private static String key = "";

    //    public static void key(String ke){
//        key=ke;
//    }
    public static void init(String password) {
        if (StringUtils.isBlank(key)) {
            key = "ezadmin1234!@#$&asjkdfakjapiASJDFIJ";
        }
        if (StringUtils.isNotBlank(password)) {
            key = password;
        }
    }


    /**
     * DES加密
     *
     * @param  :需要加密的数据
     * @return
     */
    public static String encryptDES(Object object) throws Exception {
        return encryptDES(Utils.trimNull(object));
    }
    public static String encryptDES(String data) throws Exception {
        init("");
        String s = null;
        if (data != null) {
            //DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();
            //从原始密钥数据创建DESKeySpec对象
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
            //创建一个密钥工厂，用它将DESKeySpec转化成SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKeySpec);
            //Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES");
            //用密钥初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
            //将加密后的数据编码成字符串
            s =  Base64.getUrlEncoder().encodeToString(cipher.doFinal(data.getBytes()));
        }
        return s;
    }



    /**
     * DES解密
     *
     * @param data:需要解密的数据
     * @return
     * @throws Exception
     */
    public static String decryptDES(String data) throws Exception {
        init("");
        String s = null;
        if (data != null) {
            //DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();
            //从原始密钥数据创建DESKeySpec对象
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
            //创建一个密钥工厂，用它将DESKeySpec转化成SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKeySpec);
            //Cipher对象实际完成解密操作
            Cipher cipher = Cipher.getInstance("DES");
            //用密钥初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
            //将加密后的数据解码再解密
            byte[] buf = cipher.doFinal(Base64.getUrlDecoder().decode(data));
            s = new String(buf);
        }
        return s;
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

    public static void main(String[] args) throws Exception {
        init("ezadmin1234!@#$&asjkdfakjapiASJDFIJ");
        System.out.println(decryptDES("bHtr2BRpeDFl7yfPxT8UckGA2Qn5eKnhHUGfdP2Ag30"));;
    }

}
