package com.souf.karate;

public class OtherUtils {

    public static void setValidationFlags(String validateDB, String validateKafka, String validateEncryption){
        UtilsConstants.VALIDATE_DB = Boolean.valueOf(validateDB);
        UtilsConstants.VALIDATE_KAFKA = Boolean.valueOf(validateKafka);
        UtilsConstants.VALIDATE_ENCRYPTION_DB = Boolean.valueOf(validateEncryption) && Boolean.valueOf(validateDB);
        UtilsConstants.VALIDATE_ENCRYPTION_KAFKA = Boolean.valueOf(validateEncryption) && Boolean.valueOf(validateKafka);
    }

}
