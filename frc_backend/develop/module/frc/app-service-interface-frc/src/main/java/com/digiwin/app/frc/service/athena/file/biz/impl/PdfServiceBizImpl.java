package com.digiwin.app.frc.service.athena.file.biz.impl;

import cn.hutool.core.codec.Base64Decoder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.file.biz.IPdfServiceBiz;
import com.digiwin.app.frc.service.athena.file.util.*;
import com.digiwin.app.frc.service.athena.util.DmcClient;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.service.DWFile;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;


@Slf4j
@Service
public class PdfServiceBizImpl implements IPdfServiceBiz {

    @Autowired
    private CurrencySolutionPdf currencySolutionPdf;
    @Autowired
    private CommonSolutionPdf commonSolutionPdf;
    @Autowired
    private ReportCommons reportCommons;
    @Autowired
    private EDSolutionPdf edSolutionPdf;


    @Override
    public String getReportPdf(JSONObject dataContent) {
      //  String fileName = "report_Pdf_"+System.currentTimeMillis()+".pdf";
        String fileName = "D://report_Pdf_"+System.currentTimeMillis()+".pdf";
          // 创建文件
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        FileOutputStream outputStream = null;
        PdfWriter writer= null;
        FileInputStream fileInputStream = null;

        try {
            // PDF的目录
            outputStream = new FileOutputStream(fileName);
            // 创建PDF
            writer = PdfWriter.getInstance(document, outputStream);
            // 设置页面布局
            writer.setViewerPreferences(PdfWriter.PageLayoutOneColumn);
            // 打开文件
            document.open();

            // 添加标题
            JSONObject questionInfoJson = reportCommons.getKeyJson(dataContent,QuestionResponseConst.QUESTION_RESULT);
            JSONObject questionIdentifyInfo = reportCommons.getKeyJson(questionInfoJson,QuestionResponseConst.QUESTION_IDENTIFY_INFO);
            String solutionName = questionIdentifyInfo.getString("solution_name");

            reportCommons.createPdfTitle(document, solutionName);

            // 添加边框、页眉
            PdfPageBoardEventHandler border = new PdfPageBoardEventHandler();
            border.setPosition(10);
            border.setFristPagePosition(85);
            writer.setPageEvent(border);
            border.setActive(true);
            switch (solutionName) {
                case "一般解决方案":
                    commonSolutionPdf.commonSolution(document, writer, dataContent);

                    break;
                case "8D解决方案":
                    edSolutionPdf.edSolution(document, writer, dataContent);

                    break;
                case "通用解决方案":
                    currencySolutionPdf.currencySolution(document, writer, dataContent);
                    break;

                default:
                    break;
            }
            int pageNumber = writer.getPageNumber();
            border.setLastPageNum(pageNumber);
            border.setActive(false);
            // 添加最后文字
            Paragraph paragraph = new Paragraph(ReportCommons.topInterval);
            paragraph.setAlignment(3);
            paragraph.setFont(PDFUtil.getFont(Font.NORMAL));
            Chunk chunk = new Chunk("分发： □总经理  □副总经理  □技术装备部  □质量保证部  □生产制造部  □市场部  □采购部  □财务部  □供方");
            paragraph.add(chunk);
            document.add(paragraph);

            //测试
            fileInputStream = new FileInputStream(new File(fileName));
            DmcClient.toByteArray(fileInputStream);

            // 关闭文档
            document.close();
            outputStream.close();
            // 关闭书写器
            writer.close();
            document = null;
            outputStream = null;
            writer= null;
            log.info("报告生成成功");
            fileInputStream = new FileInputStream(new File(fileName));
         //   String fileId = DmcClient.uploadFile(solutionName+".pdf", fileInputStream);
            String fileId = "kk";
            fileInputStream.close();
            fileInputStream = null;
            return fileId;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("报告生成失败："+e.getMessage());
        }finally {
            //处理异常退出流关闭
            if(document != null){
                document.close();
            }
            try {
                if(outputStream != null) {
                    outputStream.close();
                }
                if(fileInputStream != null){
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                log.error("文件流关闭出错："+e.getMessage());
            }
            if(writer != null){
                writer.close();
            }
            File f = new File(fileName);
            f.delete();
        }
        return "";
    }

    @Override
    public JSONObject upload(DWFile[] files) throws Exception{
        long start = System.currentTimeMillis();
        log.info("开始附件上传：");
        JSONArray attachmentInfo = new JSONArray();
        String userName = TenantTokenUtil.getUserName();
        String userId = TenantTokenUtil.getUserId();
        for(DWFile file : files){
            JSONObject json = new JSONObject();
            String fileId = DmcClient.uploadFile(file);
            json.fluentPut("attachment_id",fileId).fluentPut("attachment_name",file.getFileName())
                    .fluentPut("upload_person_name", userName)
                    .fluentPut("upload_person_id",userId);
            attachmentInfo.add(json);
        }
        log.info("附件上传结束：时长 "+(System.currentTimeMillis()-start));
        return new JSONObject().fluentPut("attachment_info",attachmentInfo);
    }

    @Override
    public JSONObject uniAppUpload(String messageBody) throws Exception{
        JSONObject json = JSON.parseObject(messageBody);
        JSONArray filesJson = json.getJSONArray("files");
        if(filesJson == null) {
            return null;
        }
        JSONArray attachmentInfo = new JSONArray();
        String userName = TenantTokenUtil.getUserName();
        String userId = TenantTokenUtil.getUserId();
        for (int i = 0; i < filesJson.size(); i++) {
            JSONObject result = new JSONObject();
            JSONObject fileJson = filesJson.getJSONObject(i);
            String data = fileJson.getString("data");
            String fileName = fileJson.getString("name");
            String fileId = DmcClient.uploadFile(fileName, Base64Decoder.decode(data));
            result.fluentPut("attachment_id",fileId).fluentPut("attachment_name",fileName)
                    .fluentPut("upload_person_name", userName)
                    .fluentPut("upload_person_id",userId);
            attachmentInfo.add(result);
        }
        return new JSONObject().fluentPut("attachment_info",attachmentInfo);

    }


   /* public static void main(String[] args) throws IOException {
        File f = new File("running//app_backend//application//module//test.txt");
        f.createNewFile();
        PdfServiceBizImpl pdf = new PdfServiceBizImpl();
        System.out.println(111);
        String text =
                // "{\"question_result\":[{\"question_proposer_id\":\"FRC002\",\"return_reason\":\"\",\"question_no\":\"XQ_2022042909422038774777\",\"liable_person_name\":\"张丽（FRC902）\",\"question_basic_info\":[{\"question_proposer_id\":\"FRC002\",\"question_source_name\":\"成品检验\",\"expect_solve_date\":\"2022-05-02\",\"question_source_oid\":\"131ddd5d867b4862a061b426dc390915\",\"risk_level_name\":\"A\",\"occur_stage_name\":\"温度异常\",\"defect_name\":\"产品已损坏\",\"risk_level_oid\":\"49bc4a6f8ba84113a32e550e97c9f24e\",\"question_classification_no\":\"Q9\",\"happen_date\":\"2022-04-29\",\"defect_grade\":\"A\",\"important\":1,\"question_classification_name\":\"性能\",\"urgency\":2,\"occur_stage_no\":\"FR008\",\"risk_level_no\":\"FX-002\",\"question_picture\":[{\"picture_id\":\"f48c68aa-d658-4941-a24c-542c99f6dcff\"}],\"proposer_department_name\":\"生产部\",\"question_description\":\"jz-一般问题流程\",\"question_attribution_no\":\"1\",\"question_proposer_name\":\"张丽（FRC902）\",\"question_source_no\":\"R-1003\",\"question_classification_oid\":\"a6181625124648db952ed9ae9c179c36\",\"proposer_department_id\":\"DIGI001\"}],\"return_id\":\"\",\"question_closure\":[{\"process_person_id\":\"FRC002\",\"question_summary\":\"问题关闭问题总结\",\"actual_complete_date\":\"\",\"process_date\":\"2022-04-29\",\"question_closure_name\":\"问题关闭\",\"question_closure_id\":\"SE002005\",\"process_person_name\":\"张丽（FRC902）\",\"expect_complete_date\":\"2022-05-01\"}],\"question_detail_info\":[{\"source_no\":\"FRC5182561\",\"process_id\":\"271a07a0bf9e4583a93db018934754ff\",\"workstation_name\":\"机器人弯管自动线\",\"item_name\":\"400037386\",\"defect_name\":\"产品已损坏\",\"workstation_id\":\"H10-852\",\"product_no\":\"NK06\",\"product_name\":\"散热体\",\"item_no\":\"400037386\",\"project_no\":\"FRC-JZ-519889\",\"process_name\":\"装配OP14\",\"defect_picture_id\":\"90dcef27-b8c8-4abb-8801-3587bc7f4fd0\",\"batch_qty\":333,\"discover_question_qty\":\"22\",\"defect_no\":\"QT-001\"}],\"return_reason_no\":\"\",\"question_identify_info\":[{\"solution_name\":\"一般解决方案\",\"liable_person_name\":\"张丽（FRC902）\",\"is_upload_kanban\":0,\"liable_person_id\":\"FRC002\",\"solution_id\":\"SE002\",\"repeat_times\":1}],\"question_process_info\":[{\"question_distribute_name\":\"问题分配\",\"process_person_id\":\"FRC002\",\"question_distribute_detail\":[{\"process_person_id\":\"FRC002\",\"step_name\":\"问题分配\",\"attachment_upload_flag\":\"N\",\"step_id\":\"SE002001\",\"process_person_name\":\"张丽（FRC902）\",\"expect_complete_date\":\"2022-04-30\"},{\"process_person_id\":\"FRC002\",\"step_name\":\"问题处理\",\"attachment_upload_flag\":\"N\",\"step_id\":\"SE002002\",\"process_person_name\":\"张丽（FRC902）\",\"expect_complete_date\":\"2022-05-01\"},{\"process_person_id\":\"FRC002\",\"step_name\":\"问题关闭\",\"attachment_upload_flag\":\"N\",\"step_id\":\"SE002005\",\"process_person_name\":\"张丽（FRC902）\",\"expect_complete_date\":\"2022-05-03\"}],\"process_date\":\"2022-04-29\",\"question_distribute_request\":\"分配要求内容\",\"question_distribute_no\":\"SE002001\",\"process_person_name\":\"张丽（FRC902）\"}],\"attachment_info\":[],\"curb_verify_info\":[{\"process_person_id\":\"FRC002\",\"curb_verify_name\":\"任务处理验收\",\"curb_verify_detail\":[{\"edit_type\":0,\"uuid\":\"1651199677511\",\"question_id\":\"33dcdcb990234a029ca3328e9ecbbf1f\",\"process_person_name\":\"张丽（FRC902）\",\"curb_feedback\":\"任务反馈\",\"system_qty\":\"50\",\"process_person_id\":\"FRC002\",\"actual_complete_date\":\"2022-04-29\",\"process_status\":\"2\",\"process_result\":\"4\",\"curb_scene\":\"处理场所1\",\"actual_check_qty\":20,\"expect_complete_date\":\"2022-05-01\"}],\"process_date\":\"2022-04-29\",\"curb_request\":\"任务分配任务要求\",\"curb_verify_id\":\"SE002004\",\"process_person_name\":\"张丽（FRC902）\"}],\"question_acceptance_info\":[{\"acceptance_description\":\"\",\"is_knowledge_base\":0}],\"question_info\":[{\"question_no\":\"XQ_2022042909422038774777\",\"liable_person_name\":\"张丽（FRC902）\",\"liable_person_position_id\":\"\",\"liable_person_position_name\":\"\",\"question_process_result\":1,\"return_flag_id\":\"\",\"question_id\":\"7dd6d146e2c846c59aa92139f1bf0740\",\"return_reason_no\":\"\",\"question_process_status\":2,\"question_record_id\":\"1b4fb5fb534145dfbab07b2a336fd996\",\"return_flag_name\":\"\",\"question_description\":\"jz-一般问题流程\",\"close_reason\":\"\",\"liable_person_id\":\"FRC002\",\"create_date\":\"2022-04-29\"}],\"return_flag_name\":\"\",\"question_description\":\"jz-一般问题流程\",\"question_proposer_name\":\"张丽（FRC902）\",\"return_name\":\"\",\"liable_person_id\":\"FRC002\",\"__DATA_KEY\":\"NAN;\"}]}";
                // 通用
               // "{\"question_result\":{\"question_identify_info\":{\"solution_name\":\"通用解决方案\",\"liable_person_name\":\"李月（FRC905）\",\"is_upload_kanban\":0,\"liable_person_id\":\"FRC005\",\"solution_id\":\"SE003\",\"repeat_times\":0},\"reason_analysis\":{\"reason_analysis_description\":\"品号负库存\"},\"attachment_info\":[{\"attachment_name\":\"4.png\",\"attachment_id\":\"2078ad60-4cbc-42c0-8974-fd30f6fde160\",\"upload_person_name\":\"李月(FRC905)\",\"attachment_belong_stage\":\"SE003002\",\"upload_person_id\":\"FRC905\"}],\"question_confirm\":{\"return_reason\":\"\",\"return_step_no\":\"SE003002\",\"status\":\"\"},\"temporary_measure\":[{\"process_department_name\":\"质量部\",\"expect_solve_date\":\"2022-06-15 10:13:50\",\"liable_person_name\":\"李月（FRC905）\",\"human_bottleneck_analysis\":152,\"process_department_id\":\"DIGI002\",\"measure_content\":\"车间1\",\"edit_type\":0,\"attachment_upload_flag\":\"N\",\"liable_person_id\":\"FRC005\",\"uuid\":1655259210818,\"human_bottleneck_analysis_desc\":\"当前人员有152个未处理任务\"}],\"temporary_measure_execute_verify\":{\"liable_person_name\":\"李月（FRC905）\",\"process_date\":\"2022-06-15\",\"temporary_measure_execute_verify_detail\":[{\"verify_person_name\":\"李月（FRC905）\",\"process_work_hours\":\"6.4\",\"expect_solve_date\":\"2022-06-15 10:14:56\",\"liable_person_name\":\"李月（FRC905）\",\"human_bottleneck_analysis\":152,\"execute_status\":\"2\",\"execute_illustrate\":\"车检1执行\",\"process_department_id\":\"DIGI002\",\"edit_type\":0,\"verify_illustrate\":\"品号负库存\",\"attachment_upload_flag\":\"N\",\"verify_date\":\"2022-06-15\",\"uuid\":1655259210818,\"actual_finish_date\":\"2022-06-15\",\"question_id\":\"a0cd9bf2cc994e7a9dade3b035c661c1\",\"human_bottleneck_analysis_desc\":\"当前人员有152个未处理任务\",\"is_history_data\":\"N\",\"process_department_name\":\"质量部\",\"verify_person_id\":\"FRC005\",\"measure_content\":\"车间1\",\"verify_status\":\"Y\",\"liable_person_id\":\"FRC005\"}],\"liable_person_id\":\"FRC005\"},\"question_basic_info\":{\"question_proposer_id\":\"FRC005\",\"question_source_name\":\"生产制造\",\"expect_solve_date\":\"2022-06-18\",\"question_source_oid\":\"fbdea022a6c54c91bc82d4037b84b765\",\"risk_level_name\":\"I(重要-紧急)\",\"occur_stage_name\":\"生產製作\",\"defect_name\":\"产品已损坏\",\"risk_level_oid\":\"49bc4a6f8ba84113a32e550e97c9f24e\",\"question_classification_no\":\"Q4\",\"happen_date\":\"2022-06-15\",\"defect_grade\":\"A\",\"important\":1,\"question_classification_name\":\"产品异常\",\"urgency\":1,\"occur_stage_no\":\"FS030\",\"risk_level_no\":\"FX-002\",\"question_picture\":[{\"picture_id\":\"a748e9d5-d7b6-4398-b91e-f04de33e5523\"},{\"picture_id\":\"0e66f540-b46b-4827-9199-67808d0b464a\"},{\"picture_id\":\"aabf6dcf-13e0-4fe6-976f-976d734d7817\"}],\"proposer_department_name\":\"质量部\",\"question_description\":\"品号负库存\",\"question_attribution_no\":\"1\",\"question_proposer_name\":\"李月（FRC905）\",\"question_source_no\":\"R-1002\",\"question_classification_oid\":\"761c40214c7d4447a1127f0ab4622679\",\"proposer_department_id\":\"DIGI002\"},\"plan_arrange_info\":{\"liable_person_name\":\"李月（FRC905）\",\"process_date\":\"2022-06-15\",\"reason_analysis_description\":\"品号负库存\",\"liable_person_id\":\"FRC005\",\"plan_arrange\":[{\"process_department_name\":\"质量部\",\"expect_solve_date\":\"2022-06-15 10:42:04\",\"liable_person_name\":\"李月（FRC905）\",\"human_bottleneck_analysis\":151,\"step_no\":\"SE003002\",\"step_name\":\"D4-临时措施\",\"process_department_id\":\"DIGI002\",\"human_duj\":true,\"attachment_upload_flag\":\"Y\",\"liable_person_id\":\"FRC005\",\"human_bottleneck_analysis_desc\":\"当前人员有151个未处理任务\"},{\"process_department_name\":\"质量部\",\"expect_solve_date\":\"2022-06-15 16:00:04\",\"liable_person_name\":\"李月（FRC905）\",\"human_bottleneck_analysis\":151,\"step_no\":\"SE003006\",\"step_name\":\"D5-恒久措施\",\"process_department_id\":\"DIGI002\",\"human_duj\":true,\"attachment_upload_flag\":\"Y\",\"liable_person_id\":\"FRC005\",\"human_bottleneck_analysis_desc\":\"当前人员有151个未处理任务\"},{\"process_department_name\":\"质量部\",\"expect_solve_date\":\"2022-06-15 17:12:04\",\"liable_person_name\":\"李月（FRC905）\",\"human_bottleneck_analysis\":151,\"step_no\":\"SE003009\",\"step_name\":\"D6-处理确认\",\"process_department_id\":\"DIGI002\",\"human_duj\":true,\"attachment_upload_flag\":\"Y\",\"liable_person_id\":\"FRC005\",\"human_bottleneck_analysis_desc\":\"当前人员有151个未处理任务\"}]},\"question_detail_info\":{\"source_no\":\"B2222222222222222222222\",\"process_id\":\"7315e7661c8e415ca87c6233057c9414\",\"workstation_name\":\"机器人弯管自动线\",\"item_name\":\"303005102\",\"defect_name\":\"产品已损坏\",\"workstation_id\":\"H10-852\",\"product_no\":\"GS600SUA\",\"product_name\":\"双轨点胶机\",\"item_no\":\"303005102\",\"project_no\":\"A111111111111111111111111111111\",\"process_name\":\"装配OP12\",\"defect_picture_id\":\"90dcef27-b8c8-4abb-8801-3587bc7f4fd0\",\"sn\":\"C33333333333333333333333\",\"batch_qty\":100,\"discover_question_qty\":\"10\",\"defect_no\":\"QT-001\"},\"plan_arrange\":[{\"process_department_name\":\"质量部\",\"expect_solve_date\":\"2022-06-15 10:42:04\",\"liable_person_name\":\"李月（FRC905）\",\"human_bottleneck_analysis\":151,\"step_no\":\"SE003002\",\"step_name\":\"D4-临时措施\",\"process_department_id\":\"DIGI002\",\"human_duj\":true,\"attachment_upload_flag\":\"Y\",\"liable_person_id\":\"FRC005\",\"human_bottleneck_analysis_desc\":\"当前人员有151个未处理任务\"},{\"process_department_name\":\"质量部\",\"expect_solve_date\":\"2022-06-15 16:00:04\",\"liable_person_name\":\"李月（FRC905）\",\"human_bottleneck_analysis\":151,\"step_no\":\"SE003006\",\"step_name\":\"D5-恒久措施\",\"process_department_id\":\"DIGI002\",\"human_duj\":true,\"attachment_upload_flag\":\"Y\",\"liable_person_id\":\"FRC005\",\"human_bottleneck_analysis_desc\":\"当前人员有151个未处理任务\"},{\"process_department_name\":\"质量部\",\"expect_solve_date\":\"2022-06-15 17:12:04\",\"liable_person_name\":\"李月（FRC905）\",\"human_bottleneck_analysis\":151,\"step_no\":\"SE003009\",\"step_name\":\"D6-处理确认\",\"process_department_id\":\"DIGI002\",\"human_duj\":true,\"attachment_upload_flag\":\"Y\",\"liable_person_id\":\"FRC005\",\"human_bottleneck_analysis_desc\":\"当前人员有151个未处理任务\"}],\"short_term_verify\":{\"inspector_id\":\"FRC005\",\"inspector_name\":\"李月（FRC905）\",\"print_report_id\":\"18758296-e2a1-43ed-9b02-a76689d4eb38\",\"verify_illustrate\":\"\",\"verify_date\":\"2022-06-15\"}}}";
                // 8d
                "{\"question_result\":[{\"question_identify_info\":[{\"solution_name\":\"8D解决方案\",\"liable_person_name\":\"钱亦（FRC908）\",\"is_upload_kanban\":0,\"liable_person_id\":\"FRC008\",\"solution_id\":\"SE001\",\"repeat_times\":0}],\"team_member_info\":[{\"member_id\":\"FRC008\",\"role_name\":\"组长\",\"duty_name\":\"一般员工\",\"department_id\":\"DIGI002\",\"department_name\":\"质量部\",\"remark\":\"\",\"disabled\":true,\"duty_no\":\"level_8\",\"role_no\":\"1\",\"member_name\":\"钱亦（FRC908）\",\"uuid\":1655194128996}],\"containment_measure_execute\":[{\"containment_illustrate\":\"会计凭证浏览界面中显示出摘要信息和备注\",\"containment_status\":\"2\",\"process_work_hours\":\"3\",\"expect_solve_date\":\"2022-06-14 16:24:19\",\"liable_person_name\":\"钱亦（FRC908）\",\"containment_place\":\"会计凭证浏览界面中显示出摘要信息和备注\",\"liable_person_id\":\"FRC008\",\"uuid\":1655195033432,\"actual_finish_date\":\"2022-06-14\"}],\"team_build\":{\"liable_person_name\":\"钱亦（FRC908）\",\"process_date\":\"2022-06-14\",\"liable_person_id\":\"FRC008\",\"plan_arrange\":[{\"expect_solve_date\":\"2022-06-14 22:14:32\",\"liable_person_name\":\"钱亦（FRC908）\",\"human_bottleneck_analysis\":\"当前人员有46个未处理任务\",\"step_no\":\"SE001002\",\"step_name\":\"D3-围堵措施\",\"attachment_upload_flag\":\"N\",\"liable_person_id\":\"FRC008\"},{\"expect_solve_date\":\"2022-06-14 23:08:32\",\"liable_person_name\":\"钱亦（FRC908）\",\"human_bottleneck_analysis\":\"当前人员有46个未处理任务\",\"step_no\":\"SE001005\",\"step_name\":\"D4-根本原因\",\"attachment_upload_flag\":\"N\",\"liable_person_id\":\"FRC008\"},{\"expect_solve_date\":\"2022-06-14 00:08:32\",\"liable_person_name\":\"钱亦（FRC908）\",\"human_bottleneck_analysis\":\"当前人员有46个未处理任务\",\"step_no\":\"SE001006\",\"step_name\":\"D5-纠正措施\",\"attachment_upload_flag\":\"N\",\"liable_person_id\":\"FRC008\"},{\"expect_solve_date\":\"2022-06-15 01:08:32\",\"liable_person_name\":\"钱亦（FRC908）\",\"human_bottleneck_analysis\":\"当前人员有46个未处理任务\",\"step_no\":\"SE001009\",\"step_name\":\"D6-纠正措施验证\",\"attachment_upload_flag\":\"N\",\"liable_person_id\":\"FRC008\"},{\"expect_solve_date\":\"2022-06-15 02:08:32\",\"liable_person_name\":\"钱亦（FRC908）\",\"human_bottleneck_analysis\":\"当前人员有46个未处理任务\",\"step_no\":\"SE001010\",\"step_name\":\"D7-预防措施\",\"attachment_upload_flag\":\"N\",\"liable_person_id\":\"FRC008\"},{\"expect_solve_date\":\"2022-06-15 02:08:32\",\"liable_person_name\":\"钱亦（FRC908）\",\"human_bottleneck_analysis\":\"当前人员有46个未处理任务\",\"step_no\":\"SE001013\",\"step_name\":\"D8-处理确认\",\"attachment_upload_flag\":\"N\",\"liable_person_id\":\"FRC008\"}]},\"attachment_info\":[],\"question_confirm\":{\"return_reason\":\"\",\"return_step_no\":\"\",\"status\":\"2\"},\"question_basic_info\":[{\"question_proposer_id\":\"FRC005\",\"question_source_name\":\"生产制造\",\"expect_solve_date\":\"2022-06-17\",\"question_source_oid\":\"fbdea022a6c54c91bc82d4037b84b765\",\"risk_level_name\":\"S级\",\"occur_stage_name\":\"\",\"defect_name\":\"\",\"risk_level_oid\":\"20aa364672ae4719bbdc69450214f0a3\",\"question_classification_no\":\"Q1\",\"happen_date\":\"2022-06-14\",\"defect_grade\":\"\",\"important\":1,\"question_classification_name\":\"计划异常\",\"urgency\":1,\"occur_stage_no\":\"\",\"risk_level_no\":\"FX-006\",\"question_picture\":[],\"proposer_department_name\":\"质量部\",\"question_description\":\"会计凭证浏览界面中显示出摘要信息和备注\",\"question_attribution_no\":\"1\",\"question_proposer_name\":\"李月（FRC905）\",\"question_source_no\":\"R-1002\",\"question_classification_oid\":\"7373f837416241079ce22485edf5c8b0\",\"proposer_department_id\":\"DIGI002\"}],\"containment_measure_verify\":{\"liable_person_name\":\"钱亦（FRC908）\",\"process_date\":\"2022-06-14\",\"liable_person_id\":\"FRC008\",\"containment_measure_verify_detail\":[{\"process_work_hours\":\"3\",\"expect_solve_date\":\"2022-06-14 16:24:19\",\"liable_person_name\":\"钱亦（FRC908）\",\"verify_illustrate\":\"会计凭证浏览界面中显示出摘要信息和备注\",\"containment_place\":\"会计凭证浏览界面中显示出摘要信息和备注\",\"verify_date\":\"2022-06-14\",\"uuid\":1655195033432,\"actual_finish_date\":\"2022-06-14\",\"question_id\":\"0838d694d4d64579922ffd77aace659b\",\"containment_illustrate\":\"会计凭证浏览界面中显示出摘要信息和备注\",\"containment_status\":\"2\",\"actual_complete_date\":\"2022-06-14\",\"verify_status\":\"Y\",\"liable_person_id\":\"FRC008\"}]},\"question_detail_info\":[{\"source_no\":\"\",\"process_id\":\"\",\"workstation_name\":\"\",\"item_name\":\"\",\"defect_name\":\"\",\"workstation_id\":\"\",\"product_no\":\"\",\"product_name\":\"\",\"item_no\":\"\",\"project_no\":\"\",\"process_name\":\"\",\"defect_picture_id\":\"\",\"sn\":\"\",\"batch_qty\":0,\"discover_question_qty\":\"0\",\"defect_no\":\"\"}],\"plan_arrange\":[{\"expect_solve_date\":\"2022-06-14 22:14:32\",\"liable_person_name\":\"钱亦（FRC908）\",\"human_bottleneck_analysis\":\"当前人员有46个未处理任务\",\"step_no\":\"SE001002\",\"step_name\":\"D3-围堵措施\",\"attachment_upload_flag\":\"N\",\"liable_person_id\":\"FRC008\"},{\"expect_solve_date\":\"2022-06-14 23:08:32\",\"liable_person_name\":\"钱亦（FRC908）\",\"human_bottleneck_analysis\":\"当前人员有46个未处理任务\",\"step_no\":\"SE001005\",\"step_name\":\"D4-根本原因\",\"attachment_upload_flag\":\"N\",\"liable_person_id\":\"FRC008\"},{\"expect_solve_date\":\"2022-06-14 00:08:32\",\"liable_person_name\":\"钱亦（FRC908）\",\"human_bottleneck_analysis\":\"当前人员有46个未处理任务\",\"step_no\":\"SE001006\",\"step_name\":\"D5-纠正措施\",\"attachment_upload_flag\":\"N\",\"liable_person_id\":\"FRC008\"},{\"expect_solve_date\":\"2022-06-15 01:08:32\",\"liable_person_name\":\"钱亦（FRC908）\",\"human_bottleneck_analysis\":\"当前人员有46个未处理任务\",\"step_no\":\"SE001009\",\"step_name\":\"D6-纠正措施验证\",\"attachment_upload_flag\":\"N\",\"liable_person_id\":\"FRC008\"},{\"expect_solve_date\":\"2022-06-15 02:08:32\",\"liable_person_name\":\"钱亦（FRC908）\",\"human_bottleneck_analysis\":\"当前人员有46个未处理任务\",\"step_no\":\"SE001010\",\"step_name\":\"D7-预防措施\",\"attachment_upload_flag\":\"N\",\"liable_person_id\":\"FRC008\"},{\"expect_solve_date\":\"2022-06-15 02:08:32\",\"liable_person_name\":\"钱亦（FRC908）\",\"human_bottleneck_analysis\":\"当前人员有46个未处理任务\",\"step_no\":\"SE001013\",\"step_name\":\"D8-处理确认\",\"attachment_upload_flag\":\"N\",\"liable_person_id\":\"FRC008\"}],\"short_term_verify\":{\"inspector_id\":\"FRC005\",\"inspector_name\":\"李月（FRC905）\",\"print_report_id\":\"c871129d-686f-4df4-9bec-ae70aa0678f5\",\"verify_illustrate\":\"n会计凭证浏览界面中显示出摘要信息和备注\",\"verify_date\":\"2022-06-14\"},\"containment_measure\":{\"expect_solve_date\":\"FRC008\",\"liable_person_name\":\"钱亦（FRC908）\",\"process_date\":\"2022-06-14\",\"containment_measure_detail\":[{\"expect_solve_date\":\"2022-06-14 16:24:03\",\"liable_person_name\":\"钱亦（FRC908）\",\"containment_place\":\"会计凭证浏览界面中显示出摘要信息和备注\",\"attachment_upload_flag\":\"N\",\"liable_person_id\":\"FRC008\",\"uuid\":1655195033432}]}}]}";
        pdf.getReportPdf(JSON.parseObject(text));
    }*/

}
