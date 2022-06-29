package com.digiwin.app.frc.service.athena.file.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.tea.utils.StringUtils;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.util.DateUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生成8d解决方案pdf
 * @author Jiangzhou
 * @date 2022/04/29
 */
@Component
public class EDSolutionPdf {

    @Autowired
    private ReportCommons reportCommons;
    /**
     * 生成8d解决方案pdf
     *
     * @param document
     * @param dataContent
     * @throws IOException
     * @throws DocumentException
     */
    public PdfPTable edSolution(Document document, PdfWriter writer, JSONObject dataContent)
            throws Exception {
        JSONObject questionInfoJson = reportCommons.getKeyJson(dataContent,QuestionResponseConst.QUESTION_RESULT);
        JSONObject questionBasicInfo = reportCommons.getKeyJson(questionInfoJson,QuestionResponseConst.QUESTION_BASIC_INFO);
        JSONObject questionDetailInfo = reportCommons.getKeyJson(questionInfoJson,QuestionResponseConst.QUESTION_DETAIL_INFO);
        JSONObject questionIdentifyInfo = reportCommons.getKeyJson(questionInfoJson,QuestionResponseConst.QUESTION_IDENTIFY_INFO);
        PdfPTable table = PDFUtil.createTable(4, Element.ALIGN_LEFT, 0);
        // 添加问题负责列
        reportCommons.addLiablePerson(document, writer, table, dataContent, 1);
        document.add(table);
        table = PDFUtil.createTable(3, Element.ALIGN_LEFT, 0);

        //新增
        //为true时表示第八步 为false表示第五步
        boolean flag=true;
        JSONObject keyReasonAnalysis = reportCommons.getKeyJson(questionInfoJson,QuestionResponseConst.KEY_REASON_ANALYSIS);
        JSONArray customPageArr = questionInfoJson.getJSONArray(QuestionResponseConst.CUSTOM_PAGE);
        if(keyReasonAnalysis == null){
            flag=false;
        }

        // 生成问题概述
        reportCommons.addQuestionBasicInfo(document, writer, table, questionBasicInfo);
        // 问题详述  修改
        reportCommons.addQuestionDetailInfo(document, writer, table, questionDetailInfo,questionIdentifyInfo,flag,customPageArr);
        // 增加问题解决流程
        addEDSolutionProcess(document, writer, table, dataContent);

        // 设置表格位置
        // table.writeSelectedRows(0, -1, 60, (document.getPageSize().getHeight() - 120), writer.getDirectContent());
        Paragraph paragraph = new Paragraph(ReportCommons.textContentTopInterval);
        paragraph.add(table);
        document.add(paragraph);
        return table;
    }

    /**
     * 增加问题解决流程-8D
     *
     * @param document
     * @param writer
     * @param table
     * @param dataContent
     * @throws DocumentException
     * @throws IOException
     */
    private void addEDSolutionProcess(Document document, PdfWriter writer, PdfPTable table, JSONObject dataContent)
            throws Exception {
        Map<String, String> dateMap = null;
        JSONObject questionInfoJson = reportCommons.getKeyJson(dataContent,QuestionResponseConst.QUESTION_RESULT);
        JSONObject teamBuildJson = reportCommons.getKeyJson(questionInfoJson,QuestionResponseConst.TEAM_BUILD);
        JSONObject containmentMeasure = reportCommons.getKeyJson(questionInfoJson,QuestionResponseConst.CONTAINMENT_MEASURE);
        JSONObject containmentMeasureVerify = reportCommons.getKeyJson(questionInfoJson,QuestionResponseConst.CONTAINMENT_MEASURE_VERIFY);
        JSONObject keyReasonAnalysis = reportCommons.getKeyJson(questionInfoJson,QuestionResponseConst.KEY_REASON_ANALYSIS);

        // PDFUtil.addLineMerge(document, writer, table, "3、问题解决流程", Font.BOLD, true, 3, topInterval);
        // 添加组建团队
        addEDTeamBuild(document, writer, table, teamBuildJson);
        // 添加围堵措施
        dateMap = addEDContainmentMeasure(document, writer, table, containmentMeasure);
        // 添加围堵措施验证
        addEDContainmentMeasureVerify(document, writer, table, containmentMeasureVerify, dateMap);
        // 添加 根本原因分析
        addEDReasonAnalysis(document, writer, table, keyReasonAnalysis);
        // 纠正措施分配
        dateMap = addEDCorrectiveMeasure(document, writer, table, questionInfoJson.getJSONArray(QuestionResponseConst.CORRECTIVE_MEASURE));
        // 添加纠正措施验证
        addEDCorrectiveMeasureVerify(document, writer, table,questionInfoJson.getJSONObject(QuestionResponseConst.CORRECTIVE_MEASURE_VERIFY),
                questionInfoJson.getJSONArray(QuestionResponseConst.CORRECTIVE_MEASURE_EXECUTE), dateMap);
        // 预防措施分配
        dateMap = addEDPreventionMeasure(document, writer, table,questionInfoJson.getJSONArray(QuestionResponseConst.PREVENTION_MEASURE));
        // 预防措施执行验证
        addEDPreventionMeasureExecuteVerify(document, writer, table, questionInfoJson.getJSONObject(QuestionResponseConst.PREVENTION_MEASURE_EXECUTE_VERIFY),
                questionInfoJson.getJSONArray(QuestionResponseConst.PREVENTION_MEASURE_EXECUTE), dateMap);
        // 处理确认
        addEDAcceptanceInfo(document, writer, table, questionInfoJson.getJSONObject(QuestionResponseConst.PROCESS_CONFIRM));

    }

    /**
     * 处理确认-8D
     *
     * @param document
     * @param writer
     * @param table
     * @param acceptanceInfoJson
     *            处理确认信息
     * @throws DocumentException
     * @throws IOException
     */
    private void addEDAcceptanceInfo(Document document, PdfWriter writer, PdfPTable table, JSONObject acceptanceInfoJson)
            throws Exception {
        if(acceptanceInfoJson == null){
            return;
        }
        // D8-处理确认
        PDFUtil.addLineMerge(document, writer, table, "【D8-处理确认】", Font.BOLD, true, 3, ReportCommons.subTopInterval);
        String[] solutionPepArr = new String[] {"处理人:" + acceptanceInfoJson.getString("liable_person_name"),
                "处理时间:" + acceptanceInfoJson.getString("confirm_date")};
        PDFUtil.addLine(document, writer, table, solutionPepArr, Font.NORMAL, true, 3, 8);
        if (acceptanceInfoJson.size() > 0) {
            PDFUtil.addTextCellTable(document, writer, table, new PdfPTable(new float[] {PDFUtil.maxWidth}),
                    "处理确认说明:"+acceptanceInfoJson.getString("process_confirm_illustrate"),
                    Font.NORMAL,Element.ALIGN_LEFT, true, true, 2,
                    table.getNumberOfColumns(), 0);
        }
    }

    /**
     * 预防措施执行验证-8D
     *
     * @param document
     * @param writer
     * @param table
     * @param correctiveMeasureVerifyJson
     *            预防措施执行验证信息
     * @throws DocumentException
     * @throws IOException
     */
    private void addEDPreventionMeasureExecuteVerify(Document document, PdfWriter writer, PdfPTable table,
                                                     JSONObject correctiveMeasureVerifyJson, JSONArray correctiveMeasureJson, Map<String, String> dateMap)
            throws Exception {
        if(correctiveMeasureJson == null || correctiveMeasureVerifyJson == null || dateMap == null){
            return;
        }
        // 获取措施完成时间map
        Map<String, String> executeDateMap = new HashMap<String, String>();
        for (int i = 0; i < correctiveMeasureJson.size(); i++) {
            JSONObject execute = correctiveMeasureJson.getJSONObject(i);
            executeDateMap.put(execute.getString("uuid"), execute.getString("corrective_date"));
        }
        // D7-预防措施执行验证
        PDFUtil.addLineMerge(document, writer, table, "【D7-预防措施执行验证】", Font.BOLD, true, 3, ReportCommons.subTopInterval);
        String[] solutionPepArr = new String[] {"处理人:" + correctiveMeasureVerifyJson.getString("liable_person_name"),
                "处理时间:" + correctiveMeasureVerifyJson.getString("process_date")};
        PDFUtil.addLine(document, writer, table, solutionPepArr, Font.NORMAL, true, 3, 8);
        // 表格
        JSONArray containmentMeasureDetail =
                correctiveMeasureVerifyJson.getJSONArray("prevention_measure_execute_verify_detail");
        String[][] teamArr = new String[containmentMeasureDetail.size() + 1][];
        String[] strArr = new String[] {"预防措施", "执行人", "措施执行说明", "执行状态", "验证说明", "验收时间", "验收状态"};
        teamArr[0] = strArr;
        for (int i = 0; i < containmentMeasureDetail.size(); i++) {
            JSONObject planJson = containmentMeasureDetail.getJSONObject(i);
            String status = ReportCommons.statusMap.get(planJson.getInteger("execute_status"));
            String verifyDate=PDFUtil.formatDate(planJson.getString("verify_date"));
            strArr = new String[] {planJson.getString("prevention_measure_content"),
                    planJson.getString("liable_person_name"), planJson.getString("prevention_measure_execute_illustrate"),
                    PDFUtil.compareDate(dateMap.get(planJson.getString("uuid")),
                            executeDateMap.get(planJson.getString("uuid"))) < 0
                            ? status + PDFUtil.markSymbol + "(逾期)" : status,
                    planJson.getString("verify_illustrate"), verifyDate,
                    "Y".equals(planJson.getString("verify_status")) ? "通过" : "不通过"};
            teamArr[i + 1] = strArr;
        }
        PDFUtil.addCellTable(document, writer, table, new PdfPTable(new float[] {85, 40, 85, 40, 85, 50, 40}), teamArr,
                Font.NORMAL, true, false, strArr.length, table.getNumberOfColumns(), ReportCommons.tabTopInterval);
    }

    /**
     * 预防措施分配-8D
     *
     * @param document
     * @param writer
     * @param table
     * @param correctiveMeasureJson
     *            预防措施分配信息
     * @throws DocumentException
     * @throws IOException
     */
    private Map<String, String> addEDPreventionMeasure(Document document, PdfWriter writer, PdfPTable table,
                                                       JSONArray correctiveMeasureJson) throws Exception {
        if(correctiveMeasureJson == null){
            return null;
        }
        PDFUtil.addLineMerge(document, writer, table, "【D7-预防措施分配】", Font.BOLD, true, 3, ReportCommons.subTopInterval);
        // 表格
        // key uuid value 预计完成时间
        Map<String, String> dateMap = new HashMap<String, String>();
        String[][] teamArr = new String[correctiveMeasureJson.size() + 1][];
        String[] strArr = new String[] {"预防措施", "执行人", "预计完成时间"};
        teamArr[0] = strArr;
        for (int i = 0; i < correctiveMeasureJson.size(); i++) {
            JSONObject planJson = correctiveMeasureJson.getJSONObject(i);
            String expectSolveDate=PDFUtil.formatDate(planJson.getString("expect_solve_date"));
            strArr = new String[] {planJson.getString("prevention_measure_content"),
                    planJson.getString("liable_person_name"), expectSolveDate};
            teamArr[i + 1] = strArr;
            dateMap.put(planJson.getString("uuid"), planJson.getString("expect_solve_date"));
        }
        PDFUtil.addCellTable(document, writer, table,new PdfPTable(strArr.length), teamArr, Font.NORMAL, true, false, strArr.length,
                table.getNumberOfColumns(), ReportCommons.tabTopInterval);
        return dateMap;
    }

    /**
     * 纠正措施执行验证-8D
     *
     * @param document
     * @param writer
     * @param table
     * @param correctiveMeasureVerifyJson
     *            纠正措施信息
     * @throws DocumentException
     * @throws IOException
     */
    private void addEDCorrectiveMeasureVerify(Document document, PdfWriter writer, PdfPTable table,
                                              JSONObject correctiveMeasureVerifyJson, JSONArray correctiveMeasureExecuteJson, Map<String, String> dateMap)
            throws Exception {
        if(correctiveMeasureVerifyJson == null || correctiveMeasureExecuteJson == null || dateMap == null){
            return;
        }
        // 获取措施完成时间map
        Map<String, String> executeDateMap = new HashMap<String, String>();
        for (int i = 0; i < correctiveMeasureExecuteJson.size(); i++) {
            JSONObject execute = correctiveMeasureExecuteJson.getJSONObject(i);
            executeDateMap.put(execute.getString("uuid"), execute.getString("corrective_date"));
        }
        // D6-纠正措施验证
        PDFUtil.addLineMerge(document, writer, table, "【D6-纠正措施执行验证】", Font.BOLD, true, 3, ReportCommons.subTopInterval);
        String[] solutionPepArr = new String[] {"处理人:" + correctiveMeasureVerifyJson.getString("liable_person_name"),
                "处理时间:" + correctiveMeasureVerifyJson.getString("process_date")};
        PDFUtil.addLine(document, writer, table, solutionPepArr, Font.NORMAL, true, 3, 8);
        // 表格
        JSONArray containmentMeasureDetail =
                correctiveMeasureVerifyJson.getJSONArray("corrective_measure_verify_detail");
        String[][] teamArr = new String[containmentMeasureDetail.size() + 1][];
        String[] strArr = new String[] {"纠正内容", "纠正执行说明", "纠正人", "实际完成时间", "纠正状态", "验证说明", "验收时间", "验收状态"};
        teamArr[0] = strArr;
        for (int i = 0; i < containmentMeasureDetail.size(); i++) {
            JSONObject planJson = containmentMeasureDetail.getJSONObject(i);
            String status = ReportCommons.statusMap.get(planJson.getInteger("corrective_status"));
            String verifyDate=PDFUtil.formatDate(planJson.getString("verify_date"));
            strArr = new String[] {planJson.getString("corrective_content"),
                    planJson.getString("corrective_execute_illustrate"),
                    planJson.getString("corrective_person_name"),
                    executeDateMap.get(planJson.getString("uuid")),
                    PDFUtil.compareDate(dateMap.get(planJson.getString("uuid")),
                            executeDateMap.get(planJson.getString("uuid"))) < 0
                            ? status + PDFUtil.markSymbol + "(逾期)" : status,
                    planJson.getString("verify_illustrate"), verifyDate,
                    "Y".equals(planJson.getString("verify_status")) ? "通过" : "不通过"};
            teamArr[i + 1] = strArr;
        }
        PDFUtil.addCellTable(document, writer, table, new PdfPTable(new float[] {80, 80, 40, 55, 50, 80, 45, 40}),
                teamArr, Font.NORMAL, true, false, strArr.length, table.getNumberOfColumns(), ReportCommons.tabTopInterval);
    }

    /**
     * 纠正措施分配-8D
     *
     * @param document
     * @param writer
     * @param table
     * @param correctiveMeasureJson
     *            纠正措施信息
     * @throws DocumentException
     * @throws IOException
     */
    private Map<String, String> addEDCorrectiveMeasure(Document document, PdfWriter writer, PdfPTable table,
                                                       JSONArray correctiveMeasureJson) throws Exception {
        if(correctiveMeasureJson==null){
            return null;
        }
        // key uuid value 预计完成时间
        Map<String, String> dateMap = new HashMap<String, String>();
        // D6-纠正措施验证
        PDFUtil.addLineMerge(document, writer, table, "【D5-纠正措施分配】", Font.BOLD, true, 3, ReportCommons.subTopInterval);
        // 表格
        String[][] teamArr = new String[correctiveMeasureJson.size() + 1][];
        String[] strArr = new String[] {"纠正内容", "纠正人", "纠正部门", "预计完成时间"};
        teamArr[0] = strArr;
        for (int i = 0; i < correctiveMeasureJson.size(); i++) {
            JSONObject planJson = correctiveMeasureJson.getJSONObject(i);
            String expectSolveDate=PDFUtil.formatDate(planJson.getString("expect_solve_date"));
            strArr =
                    new String[] {planJson.getString("corrective_content"), planJson.getString("corrective_person_name"),
                            planJson.getString("corrective_department_name"), expectSolveDate};
            dateMap.put(planJson.getString("uuid"), planJson.getString("expect_solve_date"));
            teamArr[i + 1] = strArr;
        }
        PDFUtil.addCellTable(document, writer, table,new PdfPTable(strArr.length), teamArr, Font.NORMAL, true, false, strArr.length,
                table.getNumberOfColumns(), ReportCommons.tabTopInterval);
        return dateMap;
    }

    /**
     * 根本原因分析-8D
     *
     * @param document
     * @param writer
     * @param table
     * @param reasonAnalysis
     * @throws DocumentException
     * @throws IOException
     */
    private void addEDReasonAnalysis(Document document, PdfWriter writer, PdfPTable table, JSONObject reasonAnalysis)
            throws Exception {
        if(reasonAnalysis == null){
            return;
        }
        // D4-根本原因分析
        PDFUtil.addLineMerge(document, writer, table, "【D4-根本原因分析】", Font.BOLD, true, table.getNumberOfColumns(),
                ReportCommons.subTopInterval);
        String[] solutionPepArr = new String[] {"处理人:" + reasonAnalysis.getString("liable_person_name"),
                "处理时间:" + reasonAnalysis.getString("process_date")};
        PDFUtil.addLine(document, writer, table, solutionPepArr, Font.NORMAL, true, table.getNumberOfColumns(),
                ReportCommons.textTopInterval);
        PDFUtil.addTextCellTable(document, writer, table, new PdfPTable(new float[] {PDFUtil.maxWidth}),
                "流出原因:"+reasonAnalysis.getString("outflow_reason"),
                Font.NORMAL,Element.ALIGN_LEFT, true, true, 2,
                table.getNumberOfColumns(), 0);
        PDFUtil.addTextCellTable(document, writer, table, new PdfPTable(new float[] {PDFUtil.maxWidth}),
                "产出原因:"+reasonAnalysis.getString("output_reason"),
                Font.NORMAL,Element.ALIGN_LEFT, true, true, 2,
                table.getNumberOfColumns(), 0);
        PDFUtil.addTextCellTable(document, writer, table, new PdfPTable(new float[] {PDFUtil.maxWidth}),
                "系统原因:"+reasonAnalysis.getString("system_reason"),
                Font.NORMAL,Element.ALIGN_LEFT, true, true, 2,
                table.getNumberOfColumns(), 0);

    }

    /**
     * 围堵措施验证-8D
     *
     * @param document
     * @param writer
     * @param table
     * @param containmentMeasureVerifyJson
     * @throws DocumentException
     * @throws IOException
     */
    private void addEDContainmentMeasureVerify(Document document, PdfWriter writer, PdfPTable table,
                                               JSONObject containmentMeasureVerifyJson, Map<String, String> dateMap) throws Exception {
        // D3-围堵措施
        PDFUtil.addLineMerge(document, writer, table, "【D3-围堵措施执行验证】", Font.BOLD, true, 3, ReportCommons.subTopInterval);
        String[] solutionPepArr = new String[] {"处理人:" + containmentMeasureVerifyJson.getString("liable_person_name"),
                "处理时间:" + containmentMeasureVerifyJson.getString("process_date")};
        PDFUtil.addLine(document, writer, table, solutionPepArr, Font.NORMAL, true, 3, 8);
        // 表格
        JSONArray containmentMeasureDetail =
                containmentMeasureVerifyJson.getJSONArray("containment_measure_verify_detail");
        String[][] teamArr = new String[containmentMeasureDetail.size() + 1][];
        String[] strArr = new String[] {"围堵场所", "处理人", "围堵说明", "实际完成时间", "围堵状态", "验证说明", "验收时间", "验收状态"};
        teamArr[0] = strArr;
        for (int i = 0; i < containmentMeasureDetail.size(); i++) {
            JSONObject planJson = containmentMeasureDetail.getJSONObject(i);
            String status = ReportCommons.statusMap.get(planJson.getInteger("containment_status"));

            String actualCompleteDate=PDFUtil.formatDate(planJson.getString("actual_complete_date"));
            String verifyDate=PDFUtil.formatDate(planJson.getString("verify_date"));

            strArr = new String[] {planJson.getString("containment_place"), planJson.getString("liable_person_name"),
                    planJson.getString("containment_illustrate"),actualCompleteDate,
                    PDFUtil.compareDate(dateMap.get(planJson.getString("uuid")),
                            planJson.getString("actual_complete_date")) < 0 ? status + PDFUtil.markSymbol + "(逾期)"
                            : status,
                    planJson.getString("verify_illustrate"), verifyDate,
                    "Y".equals(planJson.getString("verify_status")) ? "通过" : "不通过"};

            teamArr[i + 1] = strArr;
        }
        PDFUtil.addCellTable(document, writer, table, new PdfPTable(new float[] {85, 40, 85, 60, 40, 85, 50, 45}),
                teamArr, Font.NORMAL, true, false, strArr.length, table.getNumberOfColumns(), ReportCommons.tabTopInterval);
    }

    /**
     * 添加组建团队-8D
     *
     * @param document
     * @param writer
     * @param table
     * @param teamBuildJson
     * @throws DocumentException
     * @throws IOException
     */
    private void addEDTeamBuild(Document document, PdfWriter writer, PdfPTable table, JSONObject teamBuildJson)
            throws Exception {
        // D2-组建团队
        PDFUtil.addLineMerge(document, writer, table, "【D2-组建团队】", Font.BOLD, true, 3, ReportCommons.subTopInterval);
        String[] solutionPepArr = new String[] {"处理人:" + teamBuildJson.getString("liable_person_name"),
                "处理时间:" + teamBuildJson.getString("process_date")};
        PDFUtil.addLine(document, writer, table, solutionPepArr, Font.NORMAL, true, 3, ReportCommons.textTopInterval);
        // 表格
        JSONArray planArrange = teamBuildJson.getJSONArray("plan_arrange");
        String[][] teamArr = new String[planArrange.size() + 1][];
        String[] strArr = new String[] {"步骤", "处理人", "预计完成时间"};
        teamArr[0] = strArr;
        for (int i = 0; i < planArrange.size(); i++) {
            JSONObject planJson = planArrange.getJSONObject(i);
            String expectSolveDate=  PDFUtil.formatDate(planJson.getString("expect_solve_date"));
            strArr = new String[] {planJson.getString("step_name"), planJson.getString("liable_person_name"),
                    expectSolveDate};
            teamArr[i + 1] = strArr;
        }
        PDFUtil.addCellTable(document, writer, table,new PdfPTable(strArr.length), teamArr, Font.NORMAL, true, false, strArr.length,
                table.getNumberOfColumns(), ReportCommons.tabTopInterval);


    }

    /**
     * 围堵措施-8D
     *
     * @param document
     * @param writer
     * @param table
     * @param containmentMeasureJson
     * @throws DocumentException
     * @throws IOException
     */
    private Map<String, String> addEDContainmentMeasure(Document document, PdfWriter writer, PdfPTable table,
                                                        JSONObject containmentMeasureJson) throws Exception {
        // key uuid value 预计完成时间
        Map<String, String> dateMap = new HashMap<String, String>();
        // D3-围堵措施
        PDFUtil.addLineMerge(document, writer, table, "【 D3-围堵措施分配】", Font.BOLD, true, 3, ReportCommons.subTopInterval);
        String[] solutionPepArr = new String[] {"处理人:" + containmentMeasureJson.getString("liable_person_name"),
                "处理时间:" + containmentMeasureJson.getString("process_date")};
        PDFUtil.addLine(document, writer, table, solutionPepArr, Font.NORMAL, true, 3, 8);
        // 表格
        JSONArray containmentMeasureDetail = containmentMeasureJson.getJSONArray("containment_measure_detail");
        String[][] teamArr = new String[containmentMeasureDetail.size() + 1][];
        String[] strArr = new String[] {"围堵场所", "处理人", "预计完成时间"};
        teamArr[0] = strArr;
        for (int i = 0; i < containmentMeasureDetail.size(); i++) {
            JSONObject planJson = containmentMeasureDetail.getJSONObject(i);
            String expectSolveDate=  PDFUtil.formatDate(planJson.getString("expect_solve_date"));
            strArr = new String[] {planJson.getString("containment_place"), planJson.getString("liable_person_name"),
                    expectSolveDate};
            teamArr[i + 1] = strArr;
            dateMap.put(planJson.getString("uuid"), planJson.getString("expect_solve_date"));
        }
        PDFUtil.addCellTable(document, writer, table, new PdfPTable(strArr.length), teamArr, Font.NORMAL, true, false, strArr.length,
                table.getNumberOfColumns(), ReportCommons.tabTopInterval);
        return dateMap;
    }
}
