package ua.cards.util;

import ua.cards.model.User;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

    public static String getTranslationLanguage(String nativeLanguage){
        return (nativeLanguage.equals("rus"))?"eng":"rus";
    }

    public static String MD5Encode(String str){
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        messageDigest.reset();
        messageDigest.update(str.getBytes());
        byte[] digest = messageDigest.digest();
        BigInteger bigInteger = new BigInteger(1,digest);
        return bigInteger.toString(16);
    }

    public static String generateHashCode(String userAgent,
                                          User user){
        String hashCode = userAgent+user.getLogin()+user.getPassword();
        return MD5Encode(hashCode);
    }

}
