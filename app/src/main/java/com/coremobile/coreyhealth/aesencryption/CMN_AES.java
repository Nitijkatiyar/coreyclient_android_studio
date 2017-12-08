package com.coremobile.coreyhealth.aesencryption;

import android.content.Context;

import com.coremobile.coreyhealth.AppConfig;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CMN_AES {

    private static SecretKeySpec secretKey;
    private static byte[] key;
    private static byte[] iv;
    Context context;
//    String secretKey;

//    public static void main(String[] args) {
//        //final String secretKey = "https://dev.mobilecorey.com/serverintegration_appstore";
//        final String secretKey = "HTTPS://DEV.MOBILECOREY.COM/SERVERINTEGRATION_APPSTORE";
//
    //String originalString = "username=vadevadmin&password=dmin&organization=VADev";
//        String originalString = "{\"Deviceid\":\"\",\"Username\":\"VaDevadmin\",\"Password\":\"dmin\",\"DeviceType\":\"\",\"organizationName\":\"VAdev\",\"AppVersion\":\"\",\"AppName\":\"\"}";
//        String encryptedString = CMN_AES.encrypt(originalString, secretKey);
//
//        String decrptText = "5ZK/p87JFUGP5z0BpLxNh5MtXc1O/eve13XsDz0j4Zin6g78cE2xGSG2lm1CqsKSJvRwp/F+kRcKAdrSoR5KnfwZaYFFnHZvs67CQeYyiOJPivr3bjIrobZ8XngRVv/101tdK9Q3rlJZNcc6UILcW8Vwyi6bGjyZvEI48btk/7HKHhAgIhqn2QCBBg4dWSArHlQLeby/aHXz17uVTM36bHX8yMWkvc9RjslcacU/q6rln6h5VkXlsm+Pw7MAsw86ECJTJlPzsJsWX4uJiDGajlF2HBT+wgolbSSU9E0qqDFIhyeZvnH4JDm730X+qwMX/Xm53wL+csMtfN7FkCat4KE0baZxtW1jzyv3el8vs6ybDZ45kOx2S+awk1P4jgQTj/UnjmSLpDLhNc3tJUKyuyk2Wux80Y3sN371Pf88q5b8N32AJcB5Z/OKHvfOMioE4fuTsHIul2+BaGFt4N2CcRnGu7z+HuQ3Z8NQ3VvtrxqA1U1RlzIPBR4mwbujMATwRrfXbGzr9yLgxZQ8zB4FQpSbExtKIAx6ab6ty4pCEWCehQ0gQbP6J4HIXOARcYK07B3KFGcq5dJkAxSuQVdlmQy+S4geoFgtIP9Py9WaHwSf61V/fQgGf5Gzu/ZWPauWaJpyMyDIMs9EdP7Z9fqnGjf0E2KpbYpM1ub6v5yyQONinl1q+6zZdfBRM2GsF8RNoXsZSKf4mJM9M/jx1IsS/dxUG8srYkLyaXO73ZGtnMr6ZmRH5IkhiqqhoyTSgR1OMfxMIoTKaLh+7TXSPV2/LPKlV6SAI9X7TS3eOu5eGsycFp6eMiJ2ZRKAlUFTWQjaTn94UrShKmCQfEy29Ujj2XDKdlguw47E+VeIhKA3annTQ4yGWGog/6W+ENnj0p2VWjmnx5LLRQodADOtHicv5c5VZ1QnLGSZFoNZx7kbVEl/+fNOrbM59/VPwSmGYN6x4UBud5IQ9+uiub/dMDmIlESGWpqu7OxyYV2+UpkEyiP5dfgH0tCxvPLCnJA17JbmXt7yCS8+BbHuaxruF1eR6bpfWv+Ro1aj8To2XuBryQW08H+a3NltLZO58LVPx5Lhb1YR5Mfj2+X3wgZXRyxGtQ==";
//        String decryptedString = CMN_AES.decrypt(encryptedString, secretKey);
//        String decryptedServerString = CMN_AES.decrypt(decrptText, secretKey);
//
//        System.out.println("originalString:-----" + originalString);
//        System.out.println("encryptedString:-----" + encryptedString);
//        System.out.println("decryptedString:-----" + decryptedString);
//        System.out.println("Server String:-----" + decryptedServerString);
//}


    public static void setKey(String myKey) {
        try {
            key = myKey.getBytes("UTF-8");
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "CMN_AES");
            iv = key;
        } catch (UnsupportedEncodingException e) {
            //e.getMessage();
        }
    }

    public static String encrypt(String strToEncrypt, String secret) {
        String encodedFinalData = "";
        if (AppConfig.isAESEnabled) {
            try {
                setKey(secret);
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                IvParameterSpec iv_specs = new IvParameterSpec(iv);
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv_specs);
                byte[] cipherText = cipher.doFinal(strToEncrypt.getBytes("UTF-8"));
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                outputStream.write(cipherText);
                byte[] finalData = outputStream.toByteArray();
                encodedFinalData = DatatypeConverter.printBase64Binary(finalData);
//            Log.e("Encoded Data", "" + encodedFinalData);
//            decryptData("Zi1i05Knx4AzwMOiwdcdV4AtzfO/QieCIIDywxlDoRNgD5JMayrni4VacY+ZCjGx",secret);

            } catch (Exception e) {
                //e.getMessage();
            }
        } else {
            encodedFinalData = strToEncrypt;
        }
        return encodedFinalData;
    }

    public static String decryptData(String strToDecrypt, String secret) {
        String data = "";
        if (AppConfig.isAESEnabled) {
            data = decrypt(strToDecrypt, secret);
        } else {
            data = strToDecrypt;
        }
        return data;
    }

    public static String decrypt(String strToDecrypt, String secret) {
        String plainText = "";
        try {
            setKey(secret);
            byte[] encryptedData = DatatypeConverter
                    .parseBase64Binary(strToDecrypt);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv_specs = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv_specs);
            byte[] plainTextBytes = cipher.doFinal(encryptedData);
            plainText = new String(plainTextBytes);
            //Log.e("Decoded Data", "" + plainText);

        } catch (Exception e) {
            e.getMessage();
            plainText = strToDecrypt;
        }
        return plainText;
    }

    public static HttpEntity getEntity(String jsonObject, String secret, ArrayList<NameValuePair> nameValuePair) throws Exception {
        HttpEntity stringEntity = null;
        if (AppConfig.isAESEnabled) {
            stringEntity = new StringEntity(encrypt(jsonObject.toString(), secret), "utf-8");
        } else {
            stringEntity = new UrlEncodedFormEntity(nameValuePair, "utf-8");
        }
        return stringEntity;
    }

    public static String ParseResponse(String result, String secret) {
        String response = null;
        if (AppConfig.isAESEnabled) {
            response = decrypt(result, secret);
//            httpEntity = new StringEntity(CMN_AES.encryptData(jsonObject.toString(), url), "utf-8");
        } else {
            return result;
        }
        return response;
    }
}