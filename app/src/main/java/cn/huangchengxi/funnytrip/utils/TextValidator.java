package cn.huangchengxi.funnytrip.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextValidator {
    private TextValidator(){}

    public static boolean validateEmail(String email){
        Pattern pattern=Pattern.compile("^[0-9a-zA-Z]+@[0-9a-zA-Z]+\\.[0-9a-zA-Z]+$");
        Matcher matcher=pattern.matcher(email);
        if (matcher.find()){
            return true;
        }else{
            return false;
        }
    }
    public static boolean validatePassword(String password){
        Pattern pattern=Pattern.compile("^[0-9a-zA-Z_]{10,20}$");
        Matcher matcher=pattern.matcher(password);
        if (matcher.find()){
            return true;
        }else{
            return false;
        }
    }
    public static boolean validateCode(String code){
        Pattern pattern=Pattern.compile("^[0-9a-z]{5}$");
        Matcher matcher=pattern.matcher(code);
        if (matcher.find()){
            return true;
        }else{
            return false;
        }
    }
}
