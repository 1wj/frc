package com.digiwin.app.frc.service.athena.common.Const;

/**
 * @ClassName QuestionResponseConst
 * @Description 问题快反查询详情-返回结构
 * @Author author
 * @Date 2021/11/20 19:28
 * @Version 1.0
 **/
public class QuestionResponseConst {
    // 最外层 W
    public static final String QUESTION_RESULT = "question_result";

    // question_info D层
    public static final String QUESTION_INFO = "question_info";

    // DF层
    public static final String QUESTION_ID = "question_id";

    // DF层
    public static final String QUESTION_PROCESS_STATUS = "question_process_status";

    // DF层
    public static final String QUESTION_PROCESS_RESULT = "question_process_result";

    // DF层
    public static final String QUESTION_NO = "question_no";

    // DF层
    public static final String QUESTION_RECORD_ID = "question_record_id";

    // DF层
    public static final String QUESTION_FLAG_ID = "return_flag_id";

    // DF层
    public static final String QUESTION_FLAG_NAME = "return_flag_name";

    public static final String QUESTION_DESCRIPTION = "question_description";
    // --------


    public static final String QUESTION_IDENTIFY_INFO = "question_identify_info";

    // 问题关闭-一般
    public static final String QUESTION_CLOSURE = "question_closure";

    public static final String QUESTION_BASIC_INFO = "question_basic_info";

    public static final String PROCESS_CONFIRM_VERIFY = "process_confirm_verify";
    // 处理确认-8D
    public static final String PROCESS_CONFIRM = "process_confirm";

    //短期结案
    public static final String SHORT_TERM_VERIFY = "short_term_verify";

    public static final String QUESTION_DETAIL_INFO = "question_detail_info";

    // 组建人员信息-8d
    public static final String TEAM_BUILD = "team_build";
    // 围堵信息-8d
    public static final String CONTAINMENT_MEASURE = "containment_measure";
    // 围堵验证信息-8d
    public static final String CONTAINMENT_MEASURE_VERIFY = "containment_measure_verify";
    // 根本原因分析信息-8d
    public static final String KEY_REASON_ANALYSIS = "key_reason_analysis";
    // 纠正措施信息-8d
    public static final String CORRECTIVE_MEASURE = "corrective_measure";
    // 纠正措施执行信息-8d
    public static final String CORRECTIVE_MEASURE_EXECUTE = "corrective_measure_execute";
    // 纠正措施验证信息-8d
    public static final String CORRECTIVE_MEASURE_VERIFY = "corrective_measure_verify";
    // 预防措施分配信息-8d
    public static final String PREVENTION_MEASURE = "prevention_measure";
    // 预防措施执行信息-8d
    public static final String PREVENTION_MEASURE_EXECUTE = "prevention_measure_execute";
    // 预防措施执行验证信息-8d
    public static final String PREVENTION_MEASURE_EXECUTE_VERIFY = "prevention_measure_execute_verify";
    // 处理确认信息-8d
    public static final String QUESTION_ACCEPTANCE_INFO = "question_acceptance_info";
    // 原因分析-通用
    public static final String REASON_ANALYSIS = "reason_analysis";
    // 计划安排-通用
    public static final String PLAN_ARRANGE_INFO = "plan_arrange_info";
    // 临时措施数据-通用
    public static final String TEMPORARY_MEASURE_EXECUTE_VERIFY = "temporary_measure_execute_verify";
    // 持久措施数据-通用
    public static final String LASTING_MEASURE_EXECUTE_VERIFY = "lasting_measure_execute_verify";
    // 问题分配数据-一般
    public static final String QUESTION_PROCESS_INFO = "question_process_info";
    // 问题处理验收数据-一般
    public static final String CURB_VERIFY_INFO = "curb_verify_info";

    //优化代码
    public static final String RETURN_REASON = "return_reason";

    public static final String RETURN_REASON_NO = "return_reason_no";

    //迭代八新增
    public static final String CUSTOM_PAGE = "custom_page";
}
