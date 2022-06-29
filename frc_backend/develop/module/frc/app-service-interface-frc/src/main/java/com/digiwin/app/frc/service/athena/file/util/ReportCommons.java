package com.digiwin.app.frc.service.athena.file.util;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 报表相关的常量和方法
 *
 * @author Jiangzhou
 * @date 2022/04/26
 */
@Component
public class ReportCommons {
    public static Map<Integer, String> questionAttributionMap = new HashMap<Integer, String>();
    public static Map<Integer, String> importantMap = new HashMap<Integer, String>();
    public static Map<Integer, String> urgencyMap = new HashMap<Integer, String>();
    public static Map<Integer, String> statusMap = new HashMap<Integer, String>();
    public static Map<Integer, String> processStatusMap = new HashMap<Integer, String>();

    static {
        // 问题来源
        questionAttributionMap.put(1, "内部");
        questionAttributionMap.put(2, "外部");
        // 重要性
        importantMap.put(1, "重要");
        importantMap.put(2, "不重要");
        // 紧急度
        urgencyMap.put(1, "紧急");
        urgencyMap.put(2, "不紧急");
        // 完成状态
        statusMap.put(1, "未开始");
        statusMap.put(2, "已完成");
        statusMap.put(3, "无需处理");
        // 处理状态
        processStatusMap.put(1, "未处理");
        processStatusMap.put(2, "已处理");
        processStatusMap.put(3, "无需处理");
    }

    /**
     * 问题来源内部
     */
    public static String QUESTION_ATTRIBUTION_IN = "1";

    /**
     * 问题来源外部
     */
    public static String QUESTION_ATTRIBUTION_OUT = "2";
    /**
     * 图片宽度
     */
    public static Integer pictureWidth = 100;
    /**
     * 图片高度
     */
    public static Integer pictureHeight = 100;
    /**
     * 大标题行与上一内容的间隔
     */
    public static Integer topInterval = 15;
    /**
     * 小标题行与上一内容的间隔
     */
    public static Integer subTopInterval = 10;
    /**
     * 表格与上一内容的间隔
     */
    public static Integer tabTopInterval = 5;
    /**
     * 文字与上一内容的间隔
     */
    public static Integer textTopInterval = 8;

    /**
     * 内容正文与标题的间隔
     */
    public static Integer contentTopInterval = 10;
    /**
     * 文字每行之间的间隔
     */
    public static Integer textContentTopInterval = 30;

    /**
     * 是否看板
     */
    public static String ISUPLOADKANBAN = "0";

    /**
     * 排序字段
     */
    public static String SEQUENCE = "sequence";



    /**
     * 添加标题
     *
     * @param document
     * @param solutionName
     * @throws IOException
     * @throws DocumentException
     */
    public void createPdfTitle(Document document, String solutionName) throws IOException, DocumentException {
        // 设置字体
        BaseFont bfChinese = BaseFont.createFont(PDFUtil.fontName, PDFUtil.fontEncoding, BaseFont.NOT_EMBEDDED);
        // 段落的间距
        Paragraph paragraph = new Paragraph(ReportCommons.textContentTopInterval);
        // 对齐方式 1 2 3代表中右左
        paragraph.setAlignment(1);
        // 字体
        Font fontHeader = new Font(bfChinese, 24, Font.BOLD, BaseColor.BLACK);
        // 设置段落字体
        paragraph.setFont(fontHeader);
        Chunk chunk1 = new Chunk("问题处理报告单");
        paragraph.add(chunk1);
        document.add(paragraph);
        fontHeader = new Font(bfChinese, 14, Font.BOLD, BaseColor.BLACK);
        paragraph.setFont(fontHeader);
        chunk1 = new Chunk("【解决方案：" + solutionName + "】");
        paragraph.clear();
        paragraph.add(chunk1);
        document.add(paragraph);
        // 添加与正文内容间隔(相当于换行)
        Paragraph paragraph1 = new Paragraph(ReportCommons.contentTopInterval);
        Chunk chunk = new Chunk(" ");
        paragraph1.add(chunk);
        document.add(paragraph1);
    }

    /**
     * 添加问题责任列
     *
     * @param document
     * @param writer
     * @param table
     * @param dataContent
     * @param type        1 是8D，2是一般，3是通用
     * @throws DocumentException
     * @throws IOException
     */
    public void addLiablePerson(Document document, PdfWriter writer, PdfPTable table, JSONObject dataContent, int type)
            throws DocumentException, IOException {
        //处理不同阶段QUESTION_IDENTIFY_INFO结构不同问题
        JSONObject questionInfoJson = getKeyJson(dataContent, QuestionResponseConst.QUESTION_RESULT);
        JSONObject questionIdentifyInfo = getKeyJson(questionInfoJson, QuestionResponseConst.QUESTION_IDENTIFY_INFO);
        String personName = questionIdentifyInfo.getString("liable_person_name");
        JSONObject questionBasicInfo = getKeyJson(questionInfoJson, QuestionResponseConst.QUESTION_BASIC_INFO);
        String startDate = questionBasicInfo.getString("happen_date");


        String completeDate = "";
        if (type == 1) {
            JSONObject shortTermVerify = questionInfoJson.getJSONObject(QuestionResponseConst.SHORT_TERM_VERIFY);
            if (shortTermVerify != null) {
                completeDate = shortTermVerify.getString("verify_date");
            }
            JSONObject processConfirm = questionInfoJson.getJSONObject(QuestionResponseConst.PROCESS_CONFIRM);
            if (processConfirm != null) {
                completeDate = processConfirm.getString("confirm_date");
            }

        } else if (type == 2) {
            JSONArray questionClosure = questionInfoJson.getJSONArray(QuestionResponseConst.QUESTION_CLOSURE);
            if (questionClosure != null && questionClosure.getJSONObject(0) != null) {
                completeDate = questionClosure.getJSONObject(0).getString("actual_complete_date");
            }

        } else if (type == 3) {
            JSONObject shortTermVerify = questionInfoJson.getJSONObject(QuestionResponseConst.SHORT_TERM_VERIFY);
            if (shortTermVerify != null) {
                completeDate = shortTermVerify.getString("verify_date");
            }
            JSONObject processConfirmVerify = questionInfoJson.getJSONObject(QuestionResponseConst.PROCESS_CONFIRM_VERIFY);
            if (processConfirmVerify != null) {
                completeDate = processConfirmVerify.getString("verify_date");
            }
        }
        // 问题归属 1内部，2外部 3全部
        String questionAttributionNo = questionBasicInfo.getString("question_attribution_no");
        String questionSourceName = questionBasicInfo.getString("question_source_name");
        String[] liablePersonArr =
                new String[]{(ReportCommons.QUESTION_ATTRIBUTION_IN.equals(questionAttributionNo) ? "问题环节:" : "客户名称:")
                        + questionSourceName, "问题负责人:" + personName, "启动时间:" + startDate, "完成时间:" + completeDate};
        PDFUtil.addLine(document, writer, table, liablePersonArr, Font.NORMAL, true, table.getNumberOfColumns(), 0);
        // 添加与正文内容间隔(相当于换行)
        PDFUtil.addLineMerge(document, writer, table, " ", Font.NORMAL, true, table.getNumberOfColumns());
    }


    /**
     * 根据json和其内部key值取对应的json数据
     *
     * @param dataContent
     * @param key
     * @return
     */
    public JSONObject getKeyJson(JSONObject dataContent, String key) {
        Object questionInfo = dataContent.get(key);
        JSONObject questionInfoJson = null;
        if (questionInfo instanceof JSONObject) {
            questionInfoJson = dataContent.getJSONObject(key);
        } else if (questionInfo instanceof JSONArray) {
            questionInfoJson = dataContent.getJSONArray(key).getJSONObject(0);
        }
        return questionInfoJson;
    }

    /**
     * 生成问题概述
     *
     * @param document
     * @param writer
     * @param basicInfo
     * @throws DocumentException
     * @throws IOException
     */
    public void addQuestionBasicInfo(Document document, PdfWriter writer, PdfPTable table, JSONObject basicInfo)
            throws DocumentException, IOException {
        // 添加问题概述
        PDFUtil.addLineMerge(document, writer, table, "【D1-问题描述】", Font.BOLD, true, 3, 0);
        // 添加问题概述内容
        String[] basicInfoArr = new String[]{
                "问题归属:" + ReportCommons.questionAttributionMap.get(basicInfo.getInteger("question_attribution_no")),
                "问题来源:" + basicInfo.getString("question_source_name"),
                "问题分类:" + basicInfo.getString("question_classification_name"),
                "发生阶段:" + basicInfo.getString("occur_stage_name"), "风险等级:" + basicInfo.getString("risk_level_name"),
                "重要性:" + ReportCommons.importantMap.get(basicInfo.getInteger("important")),
                "紧急度:" + ReportCommons.urgencyMap.get(basicInfo.getInteger("urgency")),
                "问题提出人:" + basicInfo.getString("question_proposer_name"),
                "提出部门:" + basicInfo.getString("proposer_department_name"), "发生时间:" + basicInfo.getString("happen_date"),
                "期望关闭时间:" + basicInfo.getString("expect_solve_date")};
        PDFUtil.addLine(document, writer, table, basicInfoArr, Font.NORMAL, true, 3, ReportCommons.textTopInterval);
//        PDFUtil.addLineMerge(document, writer, table, "问题描述:" + basicInfo.getString("question_description"),
//                Font.NORMAL, true, 3);
        PDFUtil.addTextCellTable(document, writer, table, new PdfPTable(new float[]{550}),
                "问题描述:" + basicInfo.getString("question_description"),
                Font.NORMAL, Element.ALIGN_LEFT, true, true, 2,
                table.getNumberOfColumns(), 0);
        /*PDFUtil.addLineMerge(document, writer, table, "问题图片:", Font.NORMAL, true, 3);
        // 添加问题图片
        JSONArray jsonArray = basicInfo.getJSONArray("question_picture");
        if (jsonArray.size() == 0) {
            return;
        }
        List<String> pictureList = new ArrayList<String>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject == null) {
                continue;
            }
            String pictureId = jsonObject.getString("picture_id");
            if (StringUtils.isNotEmpty(pictureId)) {
                pictureList.add(pictureId);
            }
        }
        if (pictureList.size() == 0) {
            return;
        }
        PDFUtil.addImageList(table, pictureList, 3, pictureWidth, pictureHeight, false, true);*/
    }

    /**
     * 问题详述
     *
     * @param document
     * @param writer
     * @param table
     * @param detailInfo
     * @throws DocumentException
     * @throws IOException
     */
    public void addQuestionDetailInfo(Document document, PdfWriter writer, PdfPTable table, JSONObject detailInfo, JSONObject questionIdentifyInfo,boolean flag, JSONArray customPageArr)
            throws DocumentException, IOException {


        // 添加问题祥述
        String[] detailInfoArr = new String[]{"项目代号:" + detailInfo.getString("project_no"),
                "来源单号:" + detailInfo.getString("source_no"), "料号:" + detailInfo.getString("item_name"),
                "SN号:" + detailInfo.getString("sn"),
                "产品系列:" + detailInfo.getString("product_name"), "工序:" + detailInfo.getString("process_name"),
                "生产线/设备:" + detailInfo.getString("workstation_name"), "本批数量:" + detailInfo.getString("batch_qty"),
                "发现问题数量:" + detailInfo.getString("discover_question_qty"), "缺陷名称:" + detailInfo.getString("defect_name"),
                "是否上板:" + (ISUPLOADKANBAN.equals(questionIdentifyInfo.getString("is_upload_kanban")) ? "是" : "否")};
        //添加自定义字段
        if (!CollectionUtil.isEmpty(customPageArr)) {
            //排序  false为升序
            JSONArray objects = PDFUtil.jsonArraySort(customPageArr, SEQUENCE, false);
            for (Iterator<Object> iterator = objects.iterator(); iterator.hasNext(); ) {
                JSONObject obj = (JSONObject) iterator.next();
                Object questionProcessStep = obj.get("question_process_step");
                if (!flag && !"QA".equals(questionProcessStep)){//这表示短期结案
                    detailInfoArr =(String[]) ArrayUtils.addAll(detailInfoArr,pingJie(obj));
                }
                if (flag){//这表示最后结束
                    detailInfoArr =(String[]) ArrayUtils.addAll(detailInfoArr,pingJie(obj));
                }
            }
        }
        PDFUtil.addLine(document, writer, table, detailInfoArr, Font.NORMAL, true, 3, ReportCommons.textTopInterval);
        // PDFUtil.addLineMerge(document, writer, table, "缺陷图片:", Font.NORMAL, true, 3, 0);
        /* String url = detailInfo.getString("defect_picture_id");
        if (StringUtils.isEmpty(url)) {
            return;
        }
        PdfPCell imageCell = PDFUtil.getImageCell("D:\\桌面\\" + url, pictureWidth, pictureHeight, false, true, 3);
        table.addCell(imageCell);*/
    }

        private String[] pingJie(JSONObject obj){
            String[] detailInfoArr=new String[]{obj.getString("chs_field_name") +":"+ obj.getString("field_value")};
            return detailInfoArr;
        }
}
