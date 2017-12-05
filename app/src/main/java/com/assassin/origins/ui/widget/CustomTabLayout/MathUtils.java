package com.assassin.origins.ui.widget.CustomTabLayout;

/**
 * Created by ph on 2016/11/23.
 */

public class MathUtils {

    static int constrain(int amount, int low, int high) {
        return amount < low ? low : (amount > high ? high : amount);
    }

    static float constrain(float amount, float low, float high) {
        return amount < low ? low : (amount > high ? high : amount);
    }
}
