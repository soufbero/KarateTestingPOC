package com.souf.karate;

import java.util.Base64;

public class EncodingUtils {

    private static Base64.Decoder decoder;
    private static boolean initialized = false;

    public static void initialize(){
        if (!initialized){
            initialized = true;
            decoder = Base64.getDecoder();
        }
    }

    public static String decode(String input){
        return new String(decoder.decode(input));
    }

}
