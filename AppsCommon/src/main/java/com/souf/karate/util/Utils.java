package com.souf.karate.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
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

    private Utils(){}

    private static final XmlMapper xmlMapper = XmlMapper.builder()
            .enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
            .build();

    private static Cipher cipherEncryptDev;
    private static Cipher cipherEncryptQa;
    static{
        try{
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            KeySpec specDev = new PBEKeySpec(Constants.SECRET_KEY_DEV.toCharArray(), Constants.SALT_DEV.getBytes(), 65536, 256);
            SecretKeySpec secretKeySpecDev = new SecretKeySpec(factory.generateSecret(specDev).getEncoded(), "AES");
            cipherEncryptDev = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipherEncryptDev.init(Cipher.ENCRYPT_MODE, secretKeySpecDev, ivSpec);

            KeySpec specQa = new PBEKeySpec(Constants.SECRET_KEY_QA.toCharArray(), Constants.SALT_QA.getBytes(), 65536, 256);
            SecretKeySpec secretKeySpecQa = new SecretKeySpec(factory.generateSecret(specQa).getEncoded(), "AES");
            cipherEncryptQa = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipherEncryptQa.init(Cipher.ENCRYPT_MODE, secretKeySpecQa, ivSpec);
        }catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | InvalidAlgorithmParameterException e){
            logger.error("Encryption Ciphers could not be initialized",e);
        }
    }

    public static String encryptDev(String strToEncrypt) {
        try {
            if (cipherEncryptDev != null){
                return Base64.getEncoder()
                        .encodeToString(cipherEncryptDev.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
            }
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            logger.error("Cannot Encrypt",e);
        }
        return strToEncrypt;
    }

    public static String encryptQa(String strToEncrypt) {
        try {
            if (cipherEncryptQa != null){
                return Base64.getEncoder()
                        .encodeToString(cipherEncryptQa.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
            }
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            logger.error("Cannot Encrypt",e);
        }
        return strToEncrypt;
    }



    public static boolean isEmptyOrNull(String input){
        return input == null || input.trim().isEmpty();
    }

    public static String printObjectAsXMLString(Object object){
        try {
            return xmlMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}
