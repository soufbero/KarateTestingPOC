package com.souf.karate.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    private static Cipher cipherEncrypt;
    private static Cipher cipherDecrypt;
    static{
        try{
            String secretKey = "secret@Key@used";
            String salt = "salt@value";
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(), "AES");

            cipherEncrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipherEncrypt.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);
            cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivspec);
        }catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | InvalidAlgorithmParameterException e){
            logger.error("Encryption/Decryption Ciphers could not be initialized",e);
        }
    }


    public static String encodeString(String input){
        return new String(Base64.getEncoder().encode(input.getBytes()));
    }

    public static String decodeString(String input){
        return new String(Base64.getDecoder().decode(input));
    }

    public static String encrypt(String strToEncrypt) {
        try {
            if (cipherEncrypt != null){
                return Base64.getEncoder()
                        .encodeToString(cipherEncrypt.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
            }
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            logger.error("Cannot Encrypt",e);
        }
        return strToEncrypt;
    }

    public static String decrypt(String strToDecrypt) {
        try {
            if (cipherDecrypt != null){
                return new String(cipherDecrypt.doFinal(Base64.getDecoder().decode(strToDecrypt)));
            }
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            logger.error("Cannot Decrypt",e);
        }
        return strToDecrypt;
    }

    public static boolean isEmptyOrNull(String input){
        return input == null || input.trim().isEmpty();
    }

    private Utils(){}
}
