package com.digiwin.app.frc.service.athena.rqi.constants;

import java.util.HashMap;
import java.util.Map;

/**
*@ClassName: LiablePersonConfigConstant
*@Description 问题责任人问题维度常量
 * 所有Excel列名中，*在Excel文件中表示不可为空白之栏位
*@Author Jiangyw
*@Date 2022/4/28
*@Time 10:45
*@Version
*/
public class LiablePersonConfigExcelConstant {

    /**
      *  1-问题确认，2-问题评审，3-问题处理，4-问题验收
      *  QC-问题确认 QAN-问题分析 QH-问题处理 QAC-问题验收
     */
    public static final Map<String,String> CONFIG_FLAG_MAP = new HashMap<String, String>(){
        {
            put("1","QC");
            put("2","QAN");
            put("3","QH");
            put("4","QAC");
        }
    };

    public static final Map<String,String> SOLUTION_EDIT_FLAG_MAP = new HashMap<String, String>(){
        {
            put("1","一般解决方案");
            put("2","8D解决方案");
        }
    };

    /**
     * Excel列名  *任务阶段-config_flag
     * *任务阶段:1-问题确认，2-问题评审，3-问题处理，4-问题验收
     *  QC-问题确认 QAN-问题分析 QH-问题处理 QAC-问题验收
     */
    public static final String CONFIG_FLAG = "*任务阶段";
    /**
     * Excel列名  *问题归属-attribution_no
     * 1-内部，2-外部
     */
    public static final String ATTRIBUTION_NO = "*问题归属";

    /**
     * Excel列名 *风险等级-risk_level_name
     */
    public static final String RISK_LEVEL_NAME = "*风险等级";


    /**
     * Excel列名 *问题来源-source_name
     */
    public static final String SOURCE_NAME = "*问题来源";

    /**
     * Excel列名 *问题分类（多选）-classification_name
     */
    public static final String CLASSIFICATION_NAME = "*问题分类（多选）";

    /**
     * Excel列名  *问题责任人-liable_person_name
     */
    public static final String LIABLE_PERSON_NAME = "*问题责任人";

    /**
     * Excel列名 -liable_person_id
     */
    public static final String LIABLE_PERSON_ID = "*问题责任人ID";

    /**
     * Excel列名  	-solution_step_id
     *
     * 用于QH
     */
    public static final String SOLUTION_EDIT_FLAG = "";

    /**
     * Excel列名 acceptance_role
     *
     * 用于QAC-用于QAC  1-反馈人 2-确认人 3-分析人
     */
    public static final String ACCEPTANCE_ROLE = "";
}
