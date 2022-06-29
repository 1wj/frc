package com.digiwin.app.frc.service.athena.app.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.common.DWApplicationConfigUtils;
import com.digiwin.app.frc.service.athena.app.biz.IQuestionCardServiceBiz;
import com.digiwin.app.frc.service.athena.app.mapper.QuestionCardMapper;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.file.util.ReportCommons;
import com.digiwin.app.frc.service.athena.rqi.constants.TaskCodeConstants;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.frc.service.athena.util.RequestClient;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.frc.service.athena.util.rqi.EocUtils;
import com.digiwin.app.service.DWServiceContext;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
@Slf4j
@Service
public class QuestionCardServiceBizImpl implements IQuestionCardServiceBiz {
    @Autowired
    private QuestionCardMapper questionCardMapper;

    @Autowired
    private ReportCommons reportCommons;
    @Override
    public JSONObject questionCardList(String messageBody) {
        JSONObject param = JSON.parseObject(messageBody);
        // 卡片状态 1-待处理 2-已完成
        Integer status = param.getInteger("status");
        //查询标识 query_flag:待处理[1-逾时 2-本日需完成 3-other] 已完成[ 4-本月 5-更早]
        //问题步骤 process_step 一期只查询QF、QA
        try {
            param.fluentPut("liable_person_id", EocUtils.getEmpId(TenantTokenUtil.getUserId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Integer pageSize = param.getInteger("page_size");
        Integer pageNo = param.getInteger("page_no");
        Long tenantSid = TenantTokenUtil.getTenantSid();
        param.fluentPut("tenantSid",tenantSid);
        List<JSONObject> list = new ArrayList<>();
        long total = 0;
        if(pageSize == null){
            list = questionCardMapper.queryQuestionList(param);
        }else{
            PageHelper.startPage(pageNo,pageSize);
            list = questionCardMapper.queryQuestionList(param);
            PageInfo<JSONObject> pageInfo = new PageInfo<>(list);
            total = pageInfo.getTotal();
        }
        List<JSONObject> overdue = new ArrayList<>();//逾时
        List<JSONObject> today = new ArrayList<>();//本日需完成
        List<JSONObject> other = new ArrayList<>();//其他
        List<JSONObject> thisMonth = new ArrayList<>(); //本月
        List<JSONObject> before = new ArrayList<>();
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM");


        for(JSONObject data : list){
            try{
                String statusStr = "";

                //修改：返回值增加一个taskCodes字段
                String taskCode = TaskCodeConstants.TASK_CODE_MAP.get(data.getString("question_process_step"));
                if(StringUtils.isEmpty(taskCode)){
                    taskCode = TaskCodeConstants.TASK_CODE_MAP.get(data.getString("question_solve_step"));
                }
                data.fluentPut("taskCodes", taskCode);
                String processStep = MultilingualismUtil.getLanguage(data.getString("question_process_step"));
                if(StringUtils.isEmpty(processStep)){
                    processStep = MultilingualismUtil.getLanguage(data.getString("question_solve_step"));
                }
                data.fluentPut("question_process_step",processStep);
                String completeDate = data.getString("expect_complete_date");//期望完成时间
                String compareDate = data.getString("compareDate");//数据库比较时间
                String updateDate = data.getString("update_date");//数据库比较时间
                JSONObject content = JSON.parseObject(data.getString("content"));
                JSONObject basic = reportCommons.getKeyJson(
                        reportCommons.getKeyJson(content,QuestionResponseConst.QUESTION_RESULT),
                        QuestionResponseConst.QUESTION_BASIC_INFO);
                data.fluentPut("important",basic.getInteger("important"))
                        .fluentPut("urgency",basic.getInteger("urgency"))
                        .fluentPut("feedback_person_id",basic.getString("question_proposer_id"))
                        .fluentPut("feedback_person_name",basic.getString("question_proposer_name"));
                data.remove("content");
                Integer questionProcessStatus = data.getInteger("question_process_status");
                Integer questionProcessResult = data.getInteger("question_process_result");
                Integer QAProcessStatus = data.getInteger("processStatus");
                Integer QAProcessResult = data.getInteger("processResult");
                //待处理
                if(status == 1){
                    if(questionProcessStatus == 2 && questionProcessResult == 1 ){
                        if(StringUtils.isEmpty(data.getString("return_flag_id"))){
                            //进行中
                            statusStr = MultilingualismUtil.getLanguage("project_e");
                        }else{
                            //已退回
                            statusStr = MultilingualismUtil.getLanguage("project_b");
                        }
                    }
                    data.fluentPut("status",statusStr);
                    if(compareDate != null && completeDate != null && compareDate(sdf2.parse(compareDate),sdf2.parse(completeDate))){
                        //逾时
                        Long[] distanceTime = getDistanceTime(sdf2.parse(completeDate), sdf2.parse(compareDate));
                        data.fluentPut("overdue_days",distanceTime[0])
                                .fluentPut("overdue_hours",distanceTime[1])
                                .fluentPut("overdue_minutes",distanceTime[2]);
                        overdue.add(data);
                    }else if(compareDate != null && completeDate != null && compareDate(sdf2.parse(completeDate),sdf2.parse(compareDate))
                            && compareDate.split(" ")[0].equals(completeDate.split(" ")[0])){
                        //本日需完成
                        today.add(data);
                    }else{
                        //其他
                        other.add(data);

                    }
                }else{
                    //已完成
                    if((QAProcessStatus != null && QAProcessResult != null) && ((QAProcessStatus == 4 && QAProcessResult == 2) || (QAProcessStatus == 8 && QAProcessResult == 2))){
                        //已结案
                        statusStr = MultilingualismUtil.getLanguage("project_c");
                    }else{
                        //进行中
                        statusStr = MultilingualismUtil.getLanguage("project_e");
                    }
                    if(questionProcessStatus == 5 && questionProcessResult == 3){
                        //已关闭
                        statusStr = MultilingualismUtil.getLanguage("project_cs");
                    }
                    data.fluentPut("status",statusStr);
                    if(sdf1.format(sdf2.parse(compareDate)).
                            equals(sdf1.format(sdf2.parse(updateDate)))){
                        //本月
                        thisMonth.add(data);
                    }else{
                        //更早
                        before.add(data);
                    }
                }

            }catch (ParseException e){
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }
        JSONObject overdueJson = new JSONObject().fluentPut("question_info", overdue).fluentPut("total", overdue.size());
        JSONObject todayJson = new JSONObject().fluentPut("question_info", today).fluentPut("total", today.size());
        JSONObject otherJson = new JSONObject().fluentPut("question_info", other).fluentPut("total", other.size());
        JSONObject thisMonthJson = new JSONObject().fluentPut("question_info", thisMonth)
                .fluentPut("total", thisMonth.size() == 0 ? 0 : total)
                .fluentPut("page_no",pageNo).fluentPut("page_size",pageSize);
        JSONObject beforeJson = new JSONObject().fluentPut("question_info", before)
                .fluentPut("total", before.size() == 0 ? 0 : total)
                .fluentPut("page_no",pageNo).fluentPut("page_size",pageSize);

        return new JSONObject().fluentPut("overdue",overdueJson).fluentPut("today",todayJson)
                .fluentPut("other",otherJson).fluentPut("this_month",thisMonthJson).fluentPut("before",beforeJson);
    }

    @Override
    public JSONObject questionCardDetailInfo(String messageBody) {
        JSONObject message = JSON.parseObject(messageBody);

        // 修改： 获取问题处理步骤
        String taskCodes = message.getString("taskCodes");
        //修改： 拼装参数
        String param ="{\"taskCodes\":[\""+taskCodes+"\"],\"appCodes\":[\"FRC\"],\"businessKeys\":[{\"question_id\":\""+message.getString("questionId")+"\"}]}";
        JSONObject result = null;
        try {
            result = RequestClient.appRequest(DWApplicationConfigUtils.getProperty("queryTaskUrl"),
                    DWServiceContext.getContext().getToken(),param,JSONObject.class);
            String taskId = result.getString("taskId");
            result = RequestClient.appGetRequest(DWApplicationConfigUtils.getProperty("backlogDetailUrl")+"/"+taskId,
                    DWServiceContext.getContext().getToken(),null,null,JSONObject.class);
            String tmTaskId = result.getString("tmTaskId");
            String tmActivityId = result.getString("tmActivityId");
            String backlogId = result.getString("backlogId");
            result = RequestClient.appGetRequest(DWApplicationConfigUtils.getProperty("uibotShowUrl")+"/"+tmTaskId+"/"+tmActivityId+"/"+backlogId,
                    DWServiceContext.getContext().getToken(),null,message.getString("locale"),JSONObject.class);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 比较data1是否大于date2
     * @param date1
     * @param date2
     * @return
     */
    public boolean compareDate(Date date1, Date date2){
        return date1.getTime()>date2.getTime();
    }

    /**
     * 统计两个时间的时间差
     * 相差几秒几毫秒
     */
    public static Long[] getDistanceTime(Date date1, Date date2) {
        long day = 0;//天数差
        long hour = 0;//小时数差
        long min = 0;//分钟数差
        long second=0;//秒数差
        long diff=0 ;//毫秒差
        String result = null;
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        c.setTime(date1);
        long time1 = date1.getTime();
        long time2 = date2.getTime();
        diff = time2 - time1;
        day = diff / (24 * 60 * 60 * 1000);
        hour = (diff / (60 * 60 * 1000) - day * 24);
        min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        second = diff/1000;
//        System.out.println("day="+day+" hour="+hour+" min="+min+" ss="+second%60+" SSS="+diff%1000);
        result=second%60+"秒"+diff%1000+"毫秒";
        return new Long[]{day,hour,min,second};
    }

}
