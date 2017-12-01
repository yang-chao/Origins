package com.huasheng.travel.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yc on 16/4/23.
 */
public class CommonUtils {

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * 验证手机号码
     *
     * @param phoneNumber 手机号码
     * @return boolean
     */
    public static boolean checkPhoneNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile("^1[0-9]{10}$");
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}
