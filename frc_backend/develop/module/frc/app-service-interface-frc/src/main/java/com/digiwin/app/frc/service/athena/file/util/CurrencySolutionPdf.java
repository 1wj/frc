package com.digiwin.app.frc.service.athena.file.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 通用解决方案pdf生成
 *
 * @author Jiangzhou
 * @date 2022/04/28
 */
@Component
public class CurrencySolutionPdf {

    @Autowired
    private ReportCommons reportCommons;

    /**
     * 生成通用解决方案pdf
     *
     * @param document
     * @param dataContent
     * @throws IOException
     * @throws DocumentException
     */
    public PdfPTable currencySolution(Document document, PdfWriter writer, JSONObject dataContent)
            throws Exception {
        JSONObject questionInfoJson = reportCommons.getKeyJson(dataContent,QuestionResponseConst.QUESTION_RESULT);
        JSONObject questionBasicInfo = reportCommons.getKeyJson(questionInfoJson,QuestionResponseConst.QUESTION_BASIC_INFO);
        JSONObject questionDetailInfo = reportCommons.getKeyJson(questionInfoJson,QuestionResponseConst.QUESTION_DETAIL_INFO);
        JSONObject questionIdentifyInfo = reportCommons.getKeyJson(questionInfoJson,QuestionResponseConst.QUESTION_IDENTIFY_INFO);

        PdfPTable table = PDFUtil.createTable(4, Element.ALIGN_LEFT, 0);
        // 添加问题负责列
        reportCommons.addLiablePerson(document, writer, table, dataContent, 3);
        document.add(table);
        table = PDFUtil.createTable(3, Element.ALIGN_LEFT, 0);

         //新增
        //为true时表示走到最终 为false表示中途
        boolean flag=true;
        JSONObject lastingMeasureExecuteVerify = reportCommons.getKeyJson(questionInfoJson,QuestionResponseConst.LASTING_MEASURE_EXECUTE_VERIFY);
        JSONArray customPageArr = questionInfoJson.getJSONArray(QuestionResponseConst.CUSTOM_PAGE);
        if(lastingMeasureExecuteVerify == null){
            flag=false;
        }

        // 生成问题概述
        reportCommons.addQuestionBasicInfo(document, writer, table, questionBasicInfo);
        // 问题详述
        reportCommons.addQuestionDetailInfo(document, writer, table, questionDetailInfo,questionIdentifyInfo,flag,customPageArr);
        // 增加问题解决流程
        addCurrencySolutionProcess(document, writer, table, dataContent);
        // 设置表格位置
        Paragraph paragraph = new Paragraph(ReportCommons.textContentTopInterval);
        paragraph.add(table);
        document.add(paragraph);
        return table;
    }

    /**
     * 添加问题解决流程
     *
     * @param document
     * @param writer
     * @param table
     * @param dataContent
     * @throws DocumentException
     * @throws IOException
     */
    private void addCurrencySolutionProcess(Document document, PdfWriter writer, PdfPTable table,
                                            JSONObject dataContent) throws Exception {
        JSONObject questionInfoJson = reportCommons.getKeyJson(dataContent,QuestionResponseConst.QUESTION_RESULT);
        JSONObject reasonAnalysis = reportCommons.getKeyJson(questionInfoJson,QuestionResponseConst.REASON_ANALYSIS);
        JSONObject planArrangeInfo = reportCommons.getKeyJson(questionInfoJson,QuestionResponseConst.PLAN_ARRANGE_INFO);
        JSONObject temporaryMeasureExecuteVerify = reportCommons.getKeyJson(questionInfoJson,QuestionResponseConst.TEMPORARY_MEASURE_EXECUTE_VERIFY);
        JSONObject lastingMeasureExecuteVerify = reportCommons.getKeyJson(questionInfoJson,QuestionResponseConst.LASTING_MEASURE_EXECUTE_VERIFY);
        JSONObject processConfirmVerify = reportCommons.getKeyJson(questionInfoJson,QuestionResponseConst.PROCESS_CONFIRM_VERIFY);
        // 原因分析
        addReason(document, writer, table, reasonAnalysis);
        // D3-计划安排
        addPlan(document, writer, table, planArrangeInfo);
        // D4-临时措施分配
        addTemporaryMeasure(document, writer, table, temporaryMeasureExecuteVerify);
        // D4-临时措施执行验证
        addTemporaryMeasureVerify(document, writer, table,temporaryMeasureExecuteVerify);
        // D5-恒久措施分配
        addLastingMeasure(document, writer, table, lastingMeasureExecuteVerify);
        // D5-恒久措施执行验证
        addLastingMeasureVerify(document, writer, table, lastingMeasureExecuteVerify);
        // 处理验证
        addEDAcceptanceInfo(document, writer, table, processConfirmVerify);

    }

    /**
     *
     * D2 原因分析
     *
     * @param document
     * @param writer
     * @param table
     * @param reasonJson
     * @throws DocumentException
     * @throws IOException
     */
    private void addReason(Document document, PdfWriter writer, PdfPTable table, JSONObject reasonJson)
            throws Exception {
        // D2-原因分析
        PDFUtil.addLineMerge(document, writer, table, "【D2-原因分析】", Font.BOLD, true, 3,
                ReportCommons.subTopInterval);
        PDFUtil.addTextCellTable(document, writer, table, new PdfPTable(new float[] {PDFUtil.maxWidth}),
                "原因分析:"+reasonJson.getString("reason_analysis_description"),
                Font.NORMAL,Element.ALIGN_LEFT, true, true, 2,
                table.getNumberOfColumns(), 0);

    }

    /**
     *
     * D3 计划安排
     *
     * @param document
     * @param writer
     * @param table
     * @param planJson
     * @throws DocumentException
     * @throws IOException
     */
    private void addPlan(Document document, PdfWriter writer, PdfPTable table, JSONObject planJson)
            throws Exception {
        // D3-计划安排
        PDFUtil.addLineMerge(document, writer, table, "【D3-计划安排】", Font.BOLD, true, 3,
                ReportCommons.subTopInterval);
        String[] solutionPepArr = new String[] {"处理人:" + planJson.getString("liable_person_name"),
                "处理时间:" + planJson.getString("process_date")};
        PDFUtil.addLine(document, writer, table, solutionPepArr, Font.NORMAL, true, 3,
                ReportCommons.textTopInterval);
        // 表格
        JSONArray planArrange = planJson.getJSONArray("plan_arrange");
        String[][] teamArr = new String[planArrange.size() + 1][];
        String[] strArr = new String[] {"步骤", "处理人", "预计完成时间"};
        teamArr[0] = strArr;
        for (int i = 0; i < planArrange.size(); i++) {
            JSONObject pJson = planArrange.getJSONObject(i);
            String expectSolveDate=  PDFUtil.formatDate(pJson.getString("expect_solve_date"));
            strArr = new String[] {pJson.getString("step_name"), pJson.getString("liable_person_name"),
                    expectSolveDate};
            teamArr[i + 1] = strArr;
        }
        PDFUtil.addCellTable(document, writer, table, new PdfPTable(strArr.length), teamArr, Font.NORMAL, true, false,
                strArr.length, table.getNumberOfColumns(), ReportCommons.tabTopInterval);

    }

    /**
     *
     * D4 临时措施分配
     *
     * @param document
     * @param writer
     * @param table
     * @param temporaryMeasureJson
     * @throws DocumentException
     * @throws IOException
     */
    private void addTemporaryMeasure(Document document, PdfWriter writer, PdfPTable table,
                                     JSONObject temporaryMeasureJson)
            throws Exception {
        // D4-临时措施分配
        PDFUtil.addLineMerge(document, writer, table, "【D4-临时措施分配】", Font.BOLD, true, 3,
                ReportCommons.subTopInterval);
        String[] solutionPepArr = new String[] {"处理人:" + temporaryMeasureJson.getString("liable_person_name"),
                "处理时间:" + temporaryMeasureJson.getString("process_date")};
        PDFUtil.addLine(document, writer, table, solutionPepArr, Font.NORMAL, true, 3,
                ReportCommons.textTopInterval);
        // 表格
        JSONArray planArrange = temporaryMeasureJson.getJSONArray("temporary_measure_execute_verify_detail");
        String[][] teamArr = new String[planArrange.size() + 1][];
        String[] strArr = new String[] {"措施内容", "处理人", "预计完成时间"};
        teamArr[0] = strArr;
        for (int i = 0; i < planArrange.size(); i++) {
            JSONObject pJson = planArrange.getJSONObject(i);
            String expectSolveDate=  PDFUtil.formatDate(pJson.getString("expect_solve_date"));
            strArr = new String[] {pJson.getString("measure_content"), pJson.getString("liable_person_name"),
                    expectSolveDate};
            teamArr[i + 1] = strArr;
        }
        PDFUtil.addCellTable(document, writer, table, new PdfPTable(strArr.length), teamArr, Font.NORMAL, true, false,
                strArr.length, table.getNumberOfColumns(), ReportCommons.tabTopInterval);

    }

    /**
     *
     * D4 临时措施执行验证
     *
     * @param document
     * @param writer
     * @param table
     * @param temporaryMeasureJson
     * @throws DocumentException
     * @throws IOException
     */
    private void addTemporaryMeasureVerify(Document document, PdfWriter writer, PdfPTable table,
                                           JSONObject temporaryMeasureJson)
            throws Exception {
        // D4-临时措施执行验证
        PDFUtil.addLineMerge(document, writer, table, "【D4-临时措施执行验证】", Font.BOLD, true, 3,
                ReportCommons.subTopInterval);
        String[] solutionPepArr = new String[] {"处理人:" + temporaryMeasureJson.getString("liable_person_name"),
                "处理时间:" + temporaryMeasureJson.getString("process_date")};
        PDFUtil.addLine(document, writer, table, solutionPepArr, Font.NORMAL, true, 3,
                ReportCommons.textTopInterval);
        // 表格
        JSONArray planArrange = temporaryMeasureJson.getJSONArray("temporary_measure_execute_verify_detail");
        String[][] teamArr = new String[planArrange.size() + 1][];
        String[] strArr =
                new String[] {"措施内容", "处理人", "执行说明", "预计完成时间", "实际完成时间", "执行状态", "验证说明", "验收时间 ", "验收状态"};
        teamArr[0] = strArr;
        for (int i = 0; i < planArrange.size(); i++) {
            JSONObject pJson = planArrange.getJSONObject(i);
            String status = ReportCommons.statusMap.get(pJson.getInteger("execute_status"));
            String actualCompleteDate=PDFUtil.formatDate(pJson.getString("actual_finish_date"));
            String verifyDate=PDFUtil.formatDate(pJson.getString("verify_date"));
            String expectSolveDate=  PDFUtil.formatDate(pJson.getString("expect_solve_date"));
            strArr = new String[] {pJson.getString("measure_content"), pJson.getString("liable_person_name"),
                    pJson.getString("execute_illustrate"),expectSolveDate,
                    actualCompleteDate,
                    PDFUtil.compareDate(PDFUtil.formatDate(pJson.getString("expect_solve_date")),
                            pJson.getString("actual_finish_date")) < 0 ? status + PDFUtil.markSymbol + "(逾期)"
                            : status,
                    pJson.getString("verify_illustrate"),verifyDate,
                    "Y".equals(pJson.getString("verify_status")) ? "通过" : "不通过"};
            teamArr[i + 1] = strArr;
        }
        PDFUtil.addCellTable(document, writer, table, new PdfPTable(new float[] {80, 40, 80, 60, 60, 45, 75, 55, 45}),
                teamArr, Font.NORMAL, true, false, strArr.length, table.getNumberOfColumns(), ReportCommons.tabTopInterval);

    }

    /**
     *
     * D5 持久措施分配
     *
     * @param document
     * @param writer
     * @param table
     * @param lastingMeasureJson
     * @throws DocumentException
     * @throws IOException
     */
    private void addLastingMeasure(Document document, PdfWriter writer, PdfPTable table, JSONObject lastingMeasureJson)
            throws Exception {
        if(lastingMeasureJson == null){
            return;
        }
        // D5-持久措施分配
        PDFUtil.addLineMerge(document, writer, table, "【D5-恒久措施分配】", Font.BOLD, true, 3,
                ReportCommons.subTopInterval);
        String[] solutionPepArr = new String[] {"处理人:" + lastingMeasureJson.getString("liable_person_name"),
                "处理时间:" + lastingMeasureJson.getString("process_date")};
        PDFUtil.addLine(document, writer, table, solutionPepArr, Font.NORMAL, true, 3,
                ReportCommons.textTopInterval);
        // 表格
        JSONArray planArrange = lastingMeasureJson.getJSONArray("lasting_measure_execute_verify_detail");
        String[][] teamArr = new String[planArrange.size() + 1][];
        String[] strArr = new String[] {"措施内容", "处理人", "预计完成时间"};
        teamArr[0] = strArr;
        for (int i = 0; i < planArrange.size(); i++) {
            JSONObject pJson = planArrange.getJSONObject(i);
            String expectSolveDate=  PDFUtil.formatDate(pJson.getString("expect_solve_date"));
            strArr = new String[] {pJson.getString("measure_content"), pJson.getString("liable_person_name"),
                    expectSolveDate};
            teamArr[i + 1] = strArr;
        }
        PDFUtil.addCellTable(document, writer, table, new PdfPTable(strArr.length), teamArr, Font.NORMAL, true, false,
                strArr.length, table.getNumberOfColumns(), ReportCommons.tabTopInterval);

    }

    /**
     *
     * D5 恒久措施执行验证
     *
     * @param document
     * @param writer
     * @param table
     * @param lastingMeasureJson
     * @throws DocumentException
     * @throws IOException
     */
    private void addLastingMeasureVerify(Document document, PdfWriter writer, PdfPTable table,
                                         JSONObject lastingMeasureJson) throws Exception {
        if(lastingMeasureJson == null){
            return;
        }
        // D5-持久措施执行验证
        PDFUtil.addLineMerge(document, writer, table, "【D5-恒久措施执行验证】", Font.BOLD, true, 3,
                ReportCommons.subTopInterval);
        String[] solutionPepArr = new String[] {"处理人:" + lastingMeasureJson.getString("liable_person_name"),
                "处理时间:" + lastingMeasureJson.getString("process_date")};
        PDFUtil.addLine(document, writer, table, solutionPepArr, Font.NORMAL, true, 3,
                ReportCommons.textTopInterval);
        // 表格
        JSONArray planArrange = lastingMeasureJson.getJSONArray("lasting_measure_execute_verify_detail");
        String[][] teamArr = new String[planArrange.size() + 1][];
        String[] strArr = new String[] {"措施内容", "处理人", "执行说明", "预计完成时间", "实际完成时间", "执行状态", "验证说明", "验收时间 ", "验收状态"};
        teamArr[0] = strArr;
        for (int i = 0; i < planArrange.size(); i++) {
            JSONObject pJson = planArrange.getJSONObject(i);
            String status = ReportCommons.statusMap.get(pJson.getInteger("execute_status"));
            String actualFinishDate=PDFUtil.formatDate(pJson.getString("actual_finish_date"));
            String verifyDate=PDFUtil.formatDate(pJson.getString("verify_date"));
            String expectSolveDate=  PDFUtil.formatDate(pJson.getString("expect_solve_date"));
            strArr = new String[] {pJson.getString("measure_content"), pJson.getString("liable_person_name"),
                    pJson.getString("execute_illustrate"), expectSolveDate,
                    actualFinishDate,
                    PDFUtil.compareDate(PDFUtil.formatDate(pJson.getString("expect_solve_date")),
                            pJson.getString("actual_finish_date")) < 0 ? status + PDFUtil.markSymbol + "(逾期)"
                            : status,
                    pJson.getString("verify_illustrate"), verifyDate,
                    "Y".equals(pJson.getString("verify_status")) ? "通过" : "不通过"};
            teamArr[i + 1] = strArr;
        }
        PDFUtil.addCellTable(document, writer, table, new PdfPTable(new float[] {80, 40, 80, 60, 60, 45, 75, 55, 45}),
                teamArr, Font.NORMAL, true, false, strArr.length, table.getNumberOfColumns(), ReportCommons.tabTopInterval);

    }

    /**
     * 处理确认
     *
     * @param document
     * @param writer
     * @param table
     * @param acceptanceInfoJson
     *            处理确认信息
     * @throws DocumentException
     * @throws IOException
     */
    private void addEDAcceptanceInfo(Document document, PdfWriter writer, PdfPTable table,
                                     JSONObject acceptanceInfoJson)
            throws Exception {
        if(acceptanceInfoJson == null){
            return;
        }
        // D8-处理确认
        PDFUtil.addLineMerge(document, writer, table, "【D6-处理确认】", Font.BOLD, true, 3,
                ReportCommons.subTopInterval);
        String[] solutionPepArr = new String[] {"处理人:" + acceptanceInfoJson.getString("verify_person_name"),
                "处理时间:" + acceptanceInfoJson.getString("verify_date")};
        PDFUtil.addLine(document, writer, table, solutionPepArr, Font.NORMAL, true, 3,
                ReportCommons.textTopInterval);
        PDFUtil.addTextCellTable(document, writer, table, new PdfPTable(new float[] {PDFUtil.maxWidth}),
                "处理确认说明:" + acceptanceInfoJson.getString("verify_illustrate"),
                Font.NORMAL,Element.ALIGN_LEFT, true, true, 2,
                table.getNumberOfColumns(), 0);

    }


}
