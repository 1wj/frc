package com.digiwin.app.frc.service.athena.common.Const;

import com.digiwin.app.frc.service.athena.common.enums.qdh.Question8DSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUniversalSolveEnum;

import java.util.HashMap;
import java.util.Map;

/**
*@ClassName: SolutionSTEPConstant
*@Description 记录各个解决方案中，id-name的映射关系
*@Author Jiangyw
*@Date 2022/5/18
*@Time 11:39
*@Version
*/
public class SolutionStepConstant {

    /**
     * 记录各个解决方案中，id-name的映射关系
     */
    public static final Map<String,String> SOLUTION_STEP_MAP = new HashMap<String, String>(){
        {
            //依次遍历枚举类，将映射关系存入map
            for (Question8DSolveEnum question8DSolveEnum :Question8DSolveEnum.values()){
                put(question8DSolveEnum.getCode(), question8DSolveEnum.getMessage());
            }
            for (QuestionSolveEnum questionSolveEnum : QuestionSolveEnum.values()){
                put(questionSolveEnum.getCode(), questionSolveEnum.getMessage());
            }
            for (QuestionUniversalSolveEnum questionUniversalSolveEnum : QuestionUniversalSolveEnum.values()){
                put(questionUniversalSolveEnum.getCode(), questionUniversalSolveEnum.getMessage());
            }
            //放入任务阶段
            put("QFL","问题发起");
            put("QF","问题确认");
            put("QIA","问题分析");
            put("QIR","问题分析审核");
            put("QS","问题处理");
            put("QA","问题验收");
        }
    };

}
