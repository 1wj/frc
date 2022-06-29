package com.digiwin.app.frc.service.athena.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName QuestionNoUtil
 * @Description 问题号工具类
 * @Author author
 * @Date 2022/2/10 17:03
 * @Version 1.0
 **/
public class QuestionNoUtil {

    /**
     * 生成问题号
     * @return 问题号
     */
    public static String initQuestionNo(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String str = formatter.format(new Date());
        str +=  (int)((Math.random() * 9 + 1) * 10000);
        return "XQ_"+str;
    }
}
