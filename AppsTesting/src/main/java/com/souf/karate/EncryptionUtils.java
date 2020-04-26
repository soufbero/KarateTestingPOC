package com.souf.karate;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.InputSource;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class EncryptionUtils {

    private static final String SECRET_KEY_DEV = "secret@Key@used@dev";
    private static final String SALT_DEV = "salt@value@dev";
    private static final String SECRET_KEY_QA = "secret@Key@used@qa";
    private static final String SALT_QA = "salt@value@qa";

    private static Cipher cipherDecrypt;
    private static XPath xpath = XPathFactory.newInstance().newXPath();

    public static void initialize(String encryptionEnv){
        if ((UtilsConstants.VALIDATE_ENCRYPTION_DB || UtilsConstants.VALIDATE_ENCRYPTION_KAFKA)
            && !UtilsConstants.ENCRYPTION_INITIALIZED){
            UtilsConstants.ENCRYPTION_INITIALIZED = true;
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
    }

    public static String decrypt(String strToDecrypt) {
        try {
            if (cipherDecrypt != null){
                return new String(cipherDecrypt.doFinal(Base64.getDecoder().decode(strToDecrypt)));
            }
        } catch (IllegalBlockSizeException | BadPaddingException e) {
        }
        return strToDecrypt;
    }

    public static Map<String, Object> validateDBEncryptionFromEvents(int eventId, String columnWithEncryption
            , String tagWithEncryption, String compareDecryptedValueTo, Map<String, Object> eventRows) throws Exception{
        Map<String, Object> returnedMap = new HashMap<>();
        if (UtilsConstants.VALIDATE_ENCRYPTION_DB){
            JSONObject jsonObject = new JSONObject(eventRows);
            JSONArray jsonArray = jsonObject.getJSONArray(UtilsConstants.FULL_DATA_KEY);
            String messageText = null;
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jObject = jsonArray.getJSONObject(i);
                if (jObject.getInt("EVT_ID") == eventId){
                    messageText = jObject.getString(columnWithEncryption);
                    break;
                }
            }
            InputSource source = new InputSource(new StringReader(messageText));
            String encryptedValue = xpath.evaluate(tagWithEncryption, source);
            String decryptedValue = decrypt(encryptedValue);
            returnedMap.put("passed",compareDecryptedValueTo.equals(decryptedValue));
            returnedMap.put("reason",UtilsConstants.COMPARISON_RESULT);
            returnedMap.put("encrypted value in tag",encryptedValue);
            returnedMap.put("decrypted value in tag",decryptedValue);
            returnedMap.put("value to compare to",compareDecryptedValueTo);
        }else{
            returnedMap.put("passed",true);
            returnedMap.put("reason",UtilsConstants.VALIDATION_SKIPPED);
        }
        return returnedMap;
    }

    public static Map<String, Object> validateKafkaEncryptionFromEvents(int eventId, String keyWithEncryption
            , String tagWithEncryption, String compareDecryptedValueTo, Map<String, Object> data) throws Exception{
        Map<String, Object> returnedMap = new HashMap<>();
        if (UtilsConstants.VALIDATE_ENCRYPTION_KAFKA){
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray(UtilsConstants.FULL_DATA_KEY);
            String messageText = null;
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jObject = new JSONObject(jsonArray.get(i).toString());
                if (jObject.getInt("eventID") == eventId){
                    messageText = jObject.getString(keyWithEncryption);
                    break;
                }
            }
            InputSource source = new InputSource(new StringReader(messageText));
            String encryptedValue = xpath.evaluate(tagWithEncryption, source);
            String decryptedValue = decrypt(encryptedValue);
            returnedMap.put("passed",compareDecryptedValueTo.equals(decryptedValue));
            returnedMap.put("reason",UtilsConstants.COMPARISON_RESULT);
            returnedMap.put("encrypted value in tag",encryptedValue);
            returnedMap.put("decrypted value in tag",decryptedValue);
            returnedMap.put("value to compare to",compareDecryptedValueTo);
        }else{
            returnedMap.put("passed",true);
            returnedMap.put("reason",UtilsConstants.VALIDATION_SKIPPED);
        }
        return returnedMap;
    }

}
