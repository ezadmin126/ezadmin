import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;

/**
 * 功能：加解密工具类第三方系统及对外接口专用版<br/>
 * 说明：使用国密SM4算法<br />
 * <!-- for GM SM4 added by Lemon on 20220810 -->
 * <dependency>
 * <groupId>org.bouncycastle</groupId>
 * <artifactId>bcprov-jdk15on</artifactId>
 * <version>1.65</version>
 * </dependency>
 */
public class CryptoThirdSM4Tools {
	private final static String CIPHERTEXT_PREFIX_THIRD_AES128 = "|$|";
	private final static String CIPHERTEXT_PREFIX_THIRD_AES128_REGEX = "\\|$\\|";
	private final static String CIPHERTEXT_PREFIX_THIRD_SM4ECB = "|$4|";
	private final static String CIPHERTEXT_PREFIX_THIRD_SM4ECB_REGEX = "\\|\\$4\\|";
    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * 功能：国密商用SM4加密<br />
     * 说明：用于加密其它敏感类数据，带密钥参数<br/>
     *
     * @param plaintext 明文
     * @param key       SM4密钥
     * @return String 加密后的密文
     */
    public static String sm4Encrypt(final String plaintext, final String key) {
        if (isBlank(plaintext) || isBlank(key)
                || plaintext.startsWith(CIPHERTEXT_PREFIX_THIRD_AES128)
                || plaintext.startsWith(CIPHERTEXT_PREFIX_THIRD_SM4ECB)
        ) {
            return plaintext;
        } else {
            String tmp = encryptECBPaddingBase64(plaintext, key);
            if (tmp == null) {
                System.err.println("ERROR-sm4Encrypt():plaintext=" + plaintext + ",encryptRes=" + tmp);
            }
            return tmp != null ? (CIPHERTEXT_PREFIX_THIRD_SM4ECB + tmp) : null;
        }
    }

    /**
     * 功能：国密商用SM4解密<br />
     * 说明：用于解密其它敏感类数据，带密钥参数，兼容AES128算法<br/>
     * @param ciphertext 密文
     * @param key        SM4密钥
     * @return String 解密后的明文
     */
    public static String sm4Decrypt(String ciphertext, String key) {
        if (isBlank(ciphertext) || isBlank(key)) return ciphertext;
        if (ciphertext.startsWith(CIPHERTEXT_PREFIX_THIRD_SM4ECB)) {
            String tmp = decryptECBPaddingBase64(ciphertext.replaceFirst(CIPHERTEXT_PREFIX_THIRD_SM4ECB_REGEX, ""), key);
            if (tmp == null) {
                System.err.println("ERROR-sm4Decrypt():ciphertext=" + ciphertext + ",decryptRes=" + tmp);
            }
            return tmp;
        } else if (ciphertext.startsWith(CIPHERTEXT_PREFIX_THIRD_AES128)) {
            String tmp = decryptBase64(ciphertext.replaceFirst(CIPHERTEXT_PREFIX_THIRD_AES128_REGEX, ""), key);
            if (tmp == null) {
                System.err.println("ERROR-sm4Decrypt():ciphertext=" + ciphertext + ",decryptRes=" + tmp);
            }
            return tmp;
        } else {
            return ciphertext;
        }
    }

    private static boolean isBlank(final String astr) {
        if ((null == astr) || (astr.length() == 0)) {
            return true;
        } else {
            return false;
        }
    }

    private static String base64StringFromByteArray(final byte[] binaryData) {
        String base64String = null;
        try {
            base64String = Base64.encodeBase64String(binaryData);
        } catch (Exception e) {
            System.err.println("Exception - base64StringFromByteArray() | binaryData : " + binaryData);
        }
        return base64String;
    }

    private static byte[] byteArrayFromBase64String(final String base64String) {
        byte[] binaryData = null;
        try {
            binaryData = Base64.decodeBase64(base64String);
        } catch (Exception e) {
            System.err.println("Exception - byteArrayFromBase64String() | base64String : " + base64String);
        }
        return binaryData;
    }

    private static String encryptECBPaddingBase64(String content, String key) {
        String res = content;
        if (isBlank(content) || isBlank(key)) {
            System.err.println("【警告】encryptECBPaddingBase64():[IN]content or [IN]key is null!|content=" + content);
        } else {
            try {
                byte[] byteArray = encryptECBPadding(content.getBytes(StandardCharsets.UTF_8), byteArrayFromBase64String(key));
                res = base64StringFromByteArray(byteArray);
            } catch (Exception e) {
                System.err.println("Exception-encryptECBPaddingBase64()" + e.getMessage());
            }
        }
        return res;
    }

    private static byte[] encryptECBPadding(byte[] content, byte[] key) {
        byte[] res = null;
        try {
            Cipher cipher = generateECBCipher("SM4/ECB/PKCS5Padding", Cipher.ENCRYPT_MODE, key);
            res = cipher.doFinal(content);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("NoSuchAlgorithmException-encryptECBPadding()" + e.getMessage());
        } catch (NoSuchProviderException e) {
            System.err.println("NoSuchProviderException-encryptECBPadding()" + e.getMessage());
        } catch (NoSuchPaddingException e) {
            System.err.println("NoSuchPaddingException-encryptECBPadding()" + e.getMessage());
        } catch (InvalidKeyException e) {
            System.err.println("InvalidKeyException-encryptECBPadding()" + e.getMessage());
        } catch (Exception e) {
            System.err.println("Exception-encryptECBPadding()" + e.getMessage());
        }
        return res;
    }

    private static String decryptECBPaddingBase64(String cipherText, String key) {
        String res = cipherText;
        if (!isBlank(cipherText) && !isBlank(key)) {
            try {
                byte[] byteArray = decryptECBPadding(byteArrayFromBase64String(cipherText), byteArrayFromBase64String(key));
                res = new String(byteArray, "UTF-8");
            } catch (Exception e) {
                System.err.println("Exception-decryptECBPaddingBase64()" + e.getMessage());
            }
        } else {
            System.err.println("【警告】decryptECBPaddingBase64():[IN]cipherText or [IN]key is null!|cipherText=" + cipherText);
        }
        return res;
    }



    private static byte[] decryptECBPadding(byte[] cipherText, byte[] key) {
        byte[] res = null;
        try {
            Cipher cipher = generateECBCipher("SM4/ECB/PKCS5Padding", Cipher.DECRYPT_MODE, key);
            res = cipher.doFinal(cipherText);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("NoSuchAlgorithmException-decryptECBPadding()" + e.getMessage());
        } catch (NoSuchProviderException e) {
            System.err.println("NoSuchProviderException-decryptECBPadding()" + e.getMessage());
        } catch (NoSuchPaddingException e) {
            System.err.println("NoSuchPaddingException-decryptECBPadding()" + e.getMessage());
        } catch (InvalidKeyException e) {
            System.err.println("InvalidKeyException-decryptECBPadding()" + e.getMessage());
        } catch (Exception e) {
            System.err.println("Exception-decryptECBPadding()" + e.getMessage());
        }
        return res;
    }

    private static Cipher generateECBCipher(String algorithmName, int mode, byte[] key)
            throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException,
            InvalidKeyException {
        Cipher cipher = Cipher.getInstance(algorithmName, BouncyCastleProvider.PROVIDER_NAME);
        Key sm4Key = new SecretKeySpec(key, "SM4");
        cipher.init(mode, sm4Key);
        return cipher;
    }


    private static String encryptAES128(final String content, final String key) {
        return encryptBase64(content, key);
    }


    private static String encryptBase64(final String content, final String base64AESKey) {
        return base64StringFromByteArray(encrypt(content, byteArrayFromBase64String(base64AESKey)));
    }

    private static String decryptBase64(final String base64Content, final String base64AESKey) {
        String result = null;
        try {
            result = new String(decryptBase64(base64Content, byteArrayFromBase64String(base64AESKey)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("Exception - decryptBase64()");
        }
        return result;
    }

    private static byte[] decryptBase64(final String base64Content, final byte[] byteAESKey) {
        return decrypt(byteArrayFromBase64String(base64Content), byteAESKey);
    }


    private static byte[] encrypt(final String content, final byte[] byteAESKey) {
        byte[] byteMiWen = null;
        try {
            SecretKeySpec sKeySpec = new SecretKeySpec(byteAESKey, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, sKeySpec);
            byteMiWen = cipher.doFinal(content.getBytes("UTF-8"));
        } catch (Exception e) {
            System.err.println("Exception - encrypt()");
        }
        return byteMiWen;
    }


    private static byte[] decrypt(final byte[] content, final byte[] byteAESKey) {
        byte[] byteMingWen = null;
        try {
            SecretKeySpec sKeySpec = new SecretKeySpec(byteAESKey, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec);
            byteMingWen = cipher.doFinal(content);
        } catch (Exception e) {
            System.err.println("Exception - decrypt()");
        }
        return byteMingWen;
}   

	public static void main(String[] args) {
		String params = "123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123";
		String key = "TvaBgrhE46sft3nZlfe7xw==";
		String content = params+key;
        String encryptStr = CryptoThirdSM4Tools.sm4Encrypt(content, key);
		System.out.printf("加密后数据：%s\n", encryptStr);
		String decryptStr = CryptoThirdSM4Tools.sm4Decrypt(encryptStr, key);
		System.out.printf("解密后数据：%s\n", decryptStr);
	}
}