package com.digiwin.app.frc.service.athena.util;

import java.util.regex.Pattern;

/**
 * @Author: xieps
 * @Date: 2021/11/18 22:35
 * @Version 1.0
 * @Description 随机号左补0工具类
 */
public class NumberUtil {
    /**
     * 将元数据前补零，补后的总长度为指定的长度，以字符串的形式返回
     * @param sourceDate
     * @param formatLength
     * @return 重组后的数据
     */
    public static String frontCompWithZore(int sourceDate, int formatLength) {
        /**
         * 0 指前面补充零
         * formatLength 字符总长度为 formatLength
         * d 代表为正数。
         */
        String newString = String.format("%0" + formatLength + "d", sourceDate);
        return newString;
    }

    /**
     *功能描述 判断字符串是否是数字
     * @author cds
     * @date 2022/4/18
     * @param
     * @return
     */
    public static boolean isNumericByRegEx(String str) {
        // ?:0或1个, *:0或多个, +:1或多个
        // 匹配所有整数
        Pattern patternInteger = Pattern.compile("^[-\\+]?[\\d]*$");
        // 匹配小数
        Pattern patternDecimal= Pattern.compile("^[-\\+]?[\\d]+[.][\\d]+$");
        //大于零的
        //Pattern pattern1=Pattern.compile("^[0-9]+.?[0-9]*$");
        return patternInteger.matcher(str).matches() || patternDecimal.matcher(str).matches() ;
    }
}
