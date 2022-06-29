package com.digiwin.app.frc.service.athena.file.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 生成一般解决方案
 *
 * @author Jiangzhou
 * @date 2022/04/29
 */
@Component
public class CommonSolutionPdf {
    @Autowired
    private ReportCommons reportCommons;

    /**
     * 生成一般解决方案pdf
     *
     * @param document
     * @param dataContent
     * @throws IOException
     * @throws DocumentException
     */
    public void commonSolution(Document document, PdfWriter writer, JSONObject dataContent)
            throws Exception {
        JSONObject questionInfoJson = reportCommons.getKeyJson(dataContent,QuestionResponseConst.QUESTION_RESULT);
        JSONObject questionBasicInfo = reportCommons.getKeyJson(questionInfoJson,QuestionResponseConst.QUESTION_BASIC_INFO);
        JSONObject questionDetailInfo = reportCommons.getKeyJson(questionInfoJson,QuestionResponseConst.QUESTION_DETAIL_INFO);
        JSONObject questionIdentifyInfo = reportCommons.getKeyJson(questionInfoJson,QuestionResponseConst.QUESTION_IDENTIFY_INFO);
        PdfPTable table = PDFUtil.createTable(4, Element.ALIGN_LEFT, 0);
        // 添加问题负责列
        reportCommons.addLiablePerson(document, writer, table, dataContent, 2);
        document.add(table);
        table = PDFUtil.createTable(3, Element.ALIGN_LEFT, 0);

        //新增
        //一般解决方案是直接走完 所以直接传true就行了
        JSONArray customPageArr = questionInfoJson.getJSONArray(QuestionResponseConst.CUSTOM_PAGE);

        // 生成问题概述
        reportCommons.addQuestionBasicInfo(document, writer, table,questionBasicInfo);
        // 问题详述
        reportCommons.addQuestionDetailInfo(document, writer, table,
                questionDetailInfo,questionIdentifyInfo,true,customPageArr);
        // 增加问题解决流程
        addCommonSolutionProcess(document, writer, table, dataContent);
        // 设置表格位置
        Paragraph paragraph = new Paragraph(ReportCommons.textContentTopInterval);
        paragraph.add(table);
        document.add(paragraph);
    }

    /**
     * 增加问题解决流程
     *
     * @param document
     * @param writer
     * @param table
     * @param dataContent
     * @throws IOException
     * @throws DocumentException
     */
    private void addCommonSolutionProcess(Document document, PdfWriter writer, PdfPTable table,
                                          JSONObject dataContent) throws Exception {
        // 问题分配
        addProcessInfo(document, writer, table, dataContent.getJSONArray(QuestionResponseConst.QUESTION_RESULT)
                .getJSONObject(0).getJSONArray(QuestionResponseConst.QUESTION_PROCESS_INFO));
        // 问题处理验收
        addVerifyInfo(document, writer, table, dataContent.getJSONArray(QuestionResponseConst.QUESTION_RESULT)
                .getJSONObject(0).getJSONArray(QuestionResponseConst.CURB_VERIFY_INFO));
        // 问题关闭
        addClosureInfo(document, writer, table, dataContent.getJSONArray(QuestionResponseConst.QUESTION_RESULT)
                .getJSONObject(0).getJSONArray(QuestionResponseConst.QUESTION_CLOSURE));

    }

    /**
     * 问题分配
     *
     * @param document
     * @param writer
     * @param table
     * @param processInfoJsonArray
     * @throws IOException
     * @throws DocumentException
     */
    private void addProcessInfo(Document document, PdfWriter writer, PdfPTable table, JSONArray processInfoJsonArray)
            throws Exception {
        // D2-问题分配
        PDFUtil.addLineMerge(document, writer, table, "【D2-问题分配】", Font.BOLD, true, 3,
                ReportCommons.subTopInterval);
        if (processInfoJsonArray.size() == 0) {
            return;
        }
        JSONObject processInfoJson = processInfoJsonArray.getJSONObject(0);
        String[] solutionPepArr = new String[] {"处理人:" + processInfoJson.getString("process_person_name"),
                "处理时间:" + processInfoJson.getString("process_date")};
        PDFUtil.addLine(document, writer, table, solutionPepArr, Font.NORMAL, true, 3,
                ReportCommons.textTopInterval);
        PDFUtil.addTextCellTable(document, writer, table, new PdfPTable(new float[] {PDFUtil.maxWidth}),
                "分配要求:"+processInfoJson.getString("question_distribute_request"),
                Font.NORMAL,Element.ALIGN_LEFT, true, true, 2,
                table.getNumberOfColumns(), 0);
        // 表格
        JSONArray planArrange = processInfoJson.getJSONArray("question_distribute_detail");
        String[][] teamArr = new String[planArrange.size() + 1][];
        String[] strArr = new String[] {"步骤", "处理人", "预计完成时间"};
        teamArr[0] = strArr;
        for (int i = 0; i < planArrange.size(); i++) {
            JSONObject pJson = planArrange.getJSONObject(i);
            String expectCompleteDate=  PDFUtil.formatDate(pJson.getString("expect_complete_date"));
            strArr = new String[] {pJson.getString("step_name"), pJson.getString("process_person_name"),
                    expectCompleteDate};
            teamArr[i + 1] = strArr;
        }
        PDFUtil.addCellTable(document, writer, table,new PdfPTable(strArr.length), teamArr, Font.NORMAL, true, false, strArr.length,
                table.getNumberOfColumns(), ReportCommons.tabTopInterval);

    }

    /**
     * 问题处理验收
     *
     * @param document
     * @param writer
     * @param table
     * @param processInfoJsonArray
     * @throws IOException
     * @throws DocumentException
     */
    private void addVerifyInfo(Document document, PdfWriter writer, PdfPTable table, JSONArray processInfoJsonArray)
            throws Exception {
        // D3-问题处理验收
        PDFUtil.addLineMerge(document, writer, table, "【D3-问题处理验收】", Font.BOLD, true, 3,
                ReportCommons.subTopInterval);
        if (processInfoJsonArray.size() == 0) {
            return;
        }
        JSONObject processInfoJson = processInfoJsonArray.getJSONObject(0);
        String[] solutionPepArr = new String[] {"处理人:" + processInfoJson.getString("process_person_name"),
                "处理时间:" + processInfoJson.getString("process_date")};
        PDFUtil.addLine(document, writer, table, solutionPepArr, Font.NORMAL, true, 3,
                ReportCommons.textTopInterval);
        PDFUtil.addTextCellTable(document, writer, table, new PdfPTable(new float[] {PDFUtil.maxWidth}),
                "任务要求:"+processInfoJson.getString("curb_request"),
                Font.NORMAL,Element.ALIGN_LEFT, true, true, 2,
                table.getNumberOfColumns(), 0);
        // 表格
        JSONArray planArrange = processInfoJson.getJSONArray("curb_verify_detail");
        String[][] teamArr = new String[planArrange.size() + 1][];
        String[] strArr = new String[] {"处理场所", "处理人", "任务反馈", "系统内数量", "实际检查数量", "预计完成时间", "实际完成时间", "处理状态"};
        teamArr[0] = strArr;
        for (int i = 0; i < planArrange.size(); i++) {
            JSONObject pJson = planArrange.getJSONObject(i);
            String status = ReportCommons.processStatusMap.get(pJson.getInteger("process_status"));
            String actualCompleteDate=PDFUtil.formatDate(pJson.getString("actual_complete_date"));
            String expectCompleteDate=  PDFUtil.formatDate(pJson.getString("expect_complete_date"));
            strArr = new String[] {pJson.getString("curb_scene"), pJson.getString("process_person_name"),
                    pJson.getString("curb_feedback"), pJson.getString("system_qty"), pJson.getString("actual_check_qty"),
                    expectCompleteDate, actualCompleteDate,
                    PDFUtil.compareDate(pJson.getString("expect_complete_date"),
                            pJson.getString("actual_complete_date")) < 0 ? status + PDFUtil.markSymbol + "(逾期)"
                            : status
            };
            teamArr[i + 1] = strArr;
        }
        PDFUtil.addCellTable(document, writer, table,new PdfPTable(new float[]{80,40,80,50,50,60,60,50}),
                teamArr, Font.NORMAL, true, false, strArr.length,table.getNumberOfColumns(), ReportCommons.tabTopInterval);


    }

    /**
     * 问题关闭
     *
     * @param document
     * @param writer
     * @param table
     * @param questionClosureJsonArray
     * @throws IOException
     * @throws DocumentException
     */
    private void addClosureInfo(Document document, PdfWriter writer, PdfPTable table,
                                JSONArray questionClosureJsonArray)
            throws Exception {
        // D4-问题关闭
        PDFUtil.addLineMerge(document, writer, table, "【D4-问题关闭】", Font.BOLD, true, 3,
                ReportCommons.subTopInterval);
        if (questionClosureJsonArray.size() == 0) {
            return;
        }
        JSONObject questionClosureJson = questionClosureJsonArray.getJSONObject(0);
        String expectCompleteDate=  PDFUtil.formatDate(questionClosureJson.getString("expect_complete_date"));

        String[] solutionPepArr = new String[] {"处理人:" + questionClosureJson.getString("process_person_name"),
                "处理时间:" + questionClosureJson.getString("process_date"),
                "预计完成时间:" + expectCompleteDate};
        PDFUtil.addLine(document, writer, table, solutionPepArr, Font.NORMAL, true, 3,
                ReportCommons.textTopInterval);
        PDFUtil.addTextCellTable(document, writer, table, new PdfPTable(new float[] {PDFUtil.maxWidth}),
                "问题总结:"+questionClosureJson.getString("question_summary"),
                Font.NORMAL,Element.ALIGN_LEFT, true, true, 2,
                table.getNumberOfColumns(), 0);



    }

}
