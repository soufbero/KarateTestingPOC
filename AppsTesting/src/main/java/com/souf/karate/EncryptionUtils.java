package com.souf.karate;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class EncryptionUtils {

    public static final String SECRET_KEY_DEV = "secret@Key@used@dev";
    public static final String SALT_DEV = "salt@value@dev";
    public static final String SECRET_KEY_QA = "secret@Key@used@qa";
    public static final String SALT_QA = "salt@value@qa";

    private Cipher cipherDecrypt;

    public EncryptionUtils(String encryptionEnv){
        try{
            String secretKey = null;
            String salt = null;
            if (encryptionEnv.equals("DevEncryption")){
                secretKey = SECRET_KEY_DEV;
                salt = SALT_DEV;
            }else if (encryptionEnv.equals("QaEncryption")){
                secretKey = SECRET_KEY_QA;
                salt = SALT_QA;
            }

            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
            cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
        }catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | InvalidAlgorithmParameterException e){
        }
    }

    public String decrypt(String strToDecrypt) {
        try {
            if (this.cipherDecrypt != null){
                return new String(this.cipherDecrypt.doFinal(Base64.getDecoder().decode(strToDecrypt)));
            }
        } catch (IllegalBlockSizeException | BadPaddingException e) {
        }
        return strToDecrypt;
    }
}
