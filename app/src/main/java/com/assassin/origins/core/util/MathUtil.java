package com.assassin.origins.core.util;

/**
 * Created by ph on 2017/2/8.
 */

public class MathUtil {

    public static String getTenThousandStr(long l) {
        if (l > 10000) {
            if (l % 10000 >= 1000) {
                return l / 10000 + "." + (l % 10000) / 1000+ "万";
            } else {
                return l / 10000 + "万";
            }
        }
        return l + "";
    }
}
