package com.snagtag.utils;

import java.text.NumberFormat;

/**
 * Created by benjamin on 10/23/14.
 */
public class FormatUtils {

    public static String formatCurrency(double money) {
       return NumberFormat.getCurrencyInstance().format(money);
    }
}
