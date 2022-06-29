package com.digiwin.app.frc.service.athena.common.Const;

/**
 * @ClassName ParamConst
 * @Description TODO
 * @Author author
 * @Date 2021/11/11 9:40
 * @Version 1.0
 **/
public class ParamConst {
    private ParamConst() {};

    /**
     * ESP 请求标准入参 std_data
     */
    public static final String STD_DATA = "std_data";
    /**
     * ESP 请求标准入参 parameter
     */
    public static final String PARAMETER = "parameter";

    /**
     * 场景：查询问题单据详情
     * QUESTION_INFO : 问题资讯(入参)
     */
    public static final String QUESTION_INFO = "question_info";

    /**
     * 场景：迭代三 修改更新 入参
     */
    public static final String QUESTION_RESULT = "question_result";

    /**
     * 场景：查询问题单据详情
     * QUESTION_ID : 问题id主键(入参)
     */
    public static final String QUESTION_ID = "question_id";

    /**
     * 多语系-模组名
     */
    public static final String LANGUAGE_MODULE_NAME = "frc";

    /**
     * 场景：新增待审核任务卡-出参返回主键
     * QUESTION_INFO : 问题资讯(入参)
     */
    public static final String pending_approve_question_id = "pending_approve_question_id";

}
