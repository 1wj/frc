package com.digiwin.app.frc.service.athena.qdh.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.Const.SolutionStepConstant;
import com.digiwin.app.frc.service.athena.common.enums.qdh.Question8DSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUniversalSolveEnum;
import com.digiwin.app.frc.service.athena.qdh.biz.RecordBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionRecordEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.model.QuestionRecordInfoModel;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.RecordMessageVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.RecordMapper;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.service.DWServiceContext;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName RecordBizImpl
 * @Description TODO
 * @Author author
 * @Date 2021/11/15 22:19
 * @Version 1.0
 **/
@Service
public class RecordBizImpl implements RecordBiz {

    private static final Map<String, Object> map = new HashMap<>();

    static{
        initMap(map);
    }

    private static void initMap(Map<String, Object> map) {
        Stream.of(Question8DSolveEnum.values()).forEach(item -> map.put(item.getCode(),item.getMessage()));
        Stream.of(QuestionUniversalSolveEnum.values()).forEach(item -> map.put(item.getCode(),item.getMessage()));
        Stream.of(QuestionSolveEnum.values()).forEach(item -> map.put(item.getCode(),item.getMessage()));
    }

    @Autowired
    RecordMapper recordMapper;

    @Override
    public int updateRecord(QuestionRecordEntity recordEntity) {
        // 查询责任人是否已维护
        return  recordMapper.updateRecord(recordEntity);
    }

    @Override
    public JSONObject insertRecord(QuestionRecordEntity recordEntity) {
        String oid = IdGenUtil.uuid();
        recordEntity.setOid(oid);
        recordEntity.setStartTime(new Date());
        recordEntity.setCreateTime(new Date());
        recordEntity.setCreateName((String) DWServiceContext.getContext().getProfile().get("userName"));
        recordMapper.insertRecord(recordEntity);

        JSONArray questionReturnInfos = new JSONArray();
        JSONObject questionReturnInfo = new JSONObject();
        questionReturnInfo.put("question_record_id",oid);
        questionReturnInfos.add(questionReturnInfo);
        JSONObject result = new JSONObject();
        result.put("question_return_info",questionReturnInfos);

        return result;
    }


    @Override
    public List getRecordNew(List<QuestionRecordInfoModel> recordInfoModelList) {
        List responseParam = new ArrayList<>();
        //根据record_oid进行去重操作
        recordInfoModelList = recordInfoModelList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(
                () -> new TreeSet<>(Comparator.comparing(QuestionRecordInfoModel::getQuestionRecordId))), ArrayList::new)
        );
        for (QuestionRecordInfoModel recordInfoModel : recordInfoModelList) {
            // 封装入参
            String status = recordInfoModel.getCurrentQuestionProcessStatus();
            recordInfoModel.setTenantsid(TenantTokenUtil.getTenantSid());
            // QS 单独处理  (一般解决方方案、8D解决方案、通用解决方案)
            if ("QS".equals(recordInfoModel.getQuestionProcessStep())) {
                String questionSolveStep = recordInfoModel.getQuestionSolveStep();
                if(StringUtils.isEmpty(questionSolveStep)){
                    responseParam = ListUtils.union(responseParam,processSolution(status,recordInfoModel));
                    continue;
                }
                List<RecordMessageVo> recordMessageVoList = processRecordMessageNew(status,recordInfoModel.getQuestionSolveStep(),recordInfoModel);
                // 判空
                JSONArray array = new JSONArray();
                if (!CollectionUtils.isEmpty(recordMessageVoList)) {
                    array = assembleResponse(recordInfoModel,recordMessageVoList,status);
                }
                responseParam = ListUtils.union(responseParam,array);
                continue;
            }
            // 获取项目卡数据 QF QIA QIR QA
            List<RecordMessageVo> recordMessageVoList = processRecordMessageNew(status,null,recordInfoModel);
            // 判空
            JSONArray array = new JSONArray();
            if (!CollectionUtils.isEmpty(recordMessageVoList)) {
                array = assembleResponse(recordInfoModel,recordMessageVoList,status);
            }
            // 封装 QF QIA QIR QA 结果集   responseParam = (JSONArray)
            responseParam = ListUtils.union(responseParam, array);

        }
        return responseParam;
    }



    private JSONArray assembleResponse(QuestionRecordInfoModel recordInfoModel,List<RecordMessageVo> recordMessageVoList,String status) {
        JSONArray jsonArray = new JSONArray();
        for (RecordMessageVo recordMessageVo : recordMessageVoList) {
            JSONObject obj = new JSONObject();
            String dataContent = recordMessageVo.getDataContent();
            // 获取前一结点的表单数据 string转json
            JSONObject resultJsonObject = JSON.parseObject(dataContent);
            // 获取最外层 question_result，特殊的，当解决方案为通用时，表单数据的包装格式不同，question_result不为数组
            JSONObject dataDetail;
            JSONObject basicInfo;
            JSONObject identifyInfo = null;
            String questionSolveStep = recordMessageVo.getQuestionSolveStep();
            if(StringUtils.isEmpty(questionSolveStep) || !"SE003".equals(questionSolveStep.substring(0, 5))){
                JSONArray questionResult =resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
                dataDetail = questionResult.getJSONObject(0);
                JSONArray basicInfos = dataDetail.getJSONArray("question_basic_info");
                basicInfo = basicInfos.getJSONObject(0);
                if("QS".equals(recordInfoModel.getQuestionProcessStep())||"QA".equals(recordInfoModel.getQuestionProcessStep())){
                    JSONArray identifyInfos = dataDetail.getJSONArray("question_identify_info");
                    identifyInfo = identifyInfos.getJSONObject(0);
                }
            }else {
                dataDetail = resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
                basicInfo = dataDetail.getJSONObject("question_basic_info");
                if("QS".equals(recordInfoModel.getQuestionProcessStep())||"QA".equals(recordInfoModel.getQuestionProcessStep())){
                    identifyInfo = dataDetail.getJSONObject("question_identify_info");
                }

            }
            obj.put("question_no",recordMessageVo.getQuestionNo());
            obj.put("task_status",recordMessageVo.getQuestionProcessResult());
            obj.put("process_person_name",recordMessageVo.getLiablePersonName());
            obj.put("question_id",recordMessageVo.getQuestionId());
            if(recordInfoModel.getQuestionProcessStep().equals("QA")){
                obj.put("solution_name",identifyInfo.get("solution_name"));
                obj.put("solution_liable_person",identifyInfo.get("liable_person_name"));
            }
            if ("QS".equals(recordInfoModel.getQuestionProcessStep()) && !StringUtils.isEmpty(questionSolveStep)){
                obj.put("expect_complete_date",new SimpleDateFormat("yyyy-MM-dd").format(recordMessageVo.getExpectCompleteDate()));
                obj.put("actual_complete_date",StringUtils.isEmpty(recordMessageVo.getActualCompleteDate())? null:new SimpleDateFormat("yyyy-MM-dd").format(recordMessageVo.getActualCompleteDate()));
                obj.put("solution_name",identifyInfo.get("solution_name"));
                obj.put("overdue",overDue(recordMessageVo,status));
                obj.put("step_id",recordInfoModel.getQuestionSolveStep());
                obj.put("step_name", SolutionStepConstant.SOLUTION_STEP_MAP.get(recordInfoModel.getQuestionSolveStep()));
            }

            if("QS".equals(recordInfoModel.getQuestionProcessStep()) && StringUtils.isEmpty(recordInfoModel.getQuestionSolveStep())){
                String solutionName = identifyInfo.getString("solution_name");
                obj.put("solution_name",solutionName);
                String solveStepName = (String) map.get(questionSolveStep);
                obj.put("step_id",questionSolveStep);
                obj.put("step_name",solveStepName);
                obj.put("overdue",overDue(recordMessageVo,status));
            }

            packageDataInfo(recordInfoModel,recordMessageVo,obj,dataDetail,basicInfo);
            jsonArray.add(obj);
        }
        return jsonArray;
    }

    /**
     * 计算逾期时间差
     *
     * @param recordMessageVo
     * @param status
     * @return  String
     */
    private String overDue(RecordMessageVo recordMessageVo,String status) {
        long expectCompleteDateTime = recordMessageVo.getExpectCompleteDate().getTime();
        //待处理 实际完成时间取当前时间
        if("1".equals(status)){
            long currentTime = System.currentTimeMillis();
            long diffTime = Math.subtractExact(currentTime,expectCompleteDateTime)>0?Math.subtractExact(currentTime,expectCompleteDateTime):0;
            return getTimeDifference(diffTime);
        }
        long actualCompleteTime = recordMessageVo.getActualCompleteDate().getTime();
        long diffTime = Math.subtractExact(actualCompleteTime,expectCompleteDateTime)>0?Math.subtractExact(actualCompleteTime,expectCompleteDateTime):0;
        return getTimeDifference(diffTime);
    }

    /**
     *功能描述 计算时间差保留一位有效小数
     * @author cds
     * @date 2022/5/27
     * @param
     * @return
     */

    private String getTimeDifference(long diffTime) {
        float hour = diffTime/ (1000 * 60 * 60);
        return  String.format("%.1f", hour);
    }

    private void packageDataInfo(QuestionRecordInfoModel recordInfoModel,RecordMessageVo recordMessageVo,JSONObject obj, JSONObject dataDetail,JSONObject basicInfo) {
        obj.put("question_proposer_name",basicInfo.get("question_proposer_name"));
        obj.put("question_happen_date",basicInfo.get("happen_date"));
        obj.put("question_description",basicInfo.get("question_description"));
        obj.put("risk_level",basicInfo.get("risk_level_name"));
        obj.put("question_classification_name",basicInfo.get("question_classification_name"));
        obj.put("question_source_name",basicInfo.get("question_source_name"));
        //根据DTD要求 对前端传参进行回传
        obj.put("question_solve_step", recordInfoModel.getQuestionSolveStep() == null ? "":recordInfoModel.getQuestionSolveStep());
        obj.put("question_record_id",recordInfoModel.getQuestionRecordId());
        obj.put("question_process_step",recordInfoModel.getQuestionProcessStep());
        obj.put("question_status",recordInfoModel.getCurrentQuestionProcessStatus());
        obj.put("process_detail","");
    }


    private List<RecordMessageVo> processRecordMessageNew(String status,String questionSolveStep, QuestionRecordInfoModel recordInfoModel) {
        String processStep = recordInfoModel.getQuestionProcessStep();
        return "1".equals(status) ? recordMapper.getPendingRecordMsg(processStep,questionSolveStep,recordInfoModel.getQuestionRecordId(),recordInfoModel.getTenantsid()):recordMapper.getRecordMsg(processStep,questionSolveStep,recordInfoModel.getQuestionRecordId(),recordInfoModel.getTenantsid());
    }


    /**
     * 查询解决方案信息 针对solveStep传值为空的情况下
     *
     * @param status
     * @param recordInfoModel
     * @return
     */
    private List processSolution(String status, QuestionRecordInfoModel recordInfoModel) {
        recordInfoModel.setQuestionSolveStep(null);
        List<RecordMessageVo> recordMessageVoList = ("1".equals(status) ? recordMapper.getPendingRecordMsg(recordInfoModel.getQuestionProcessStep(),recordInfoModel.getQuestionSolveStep(),recordInfoModel.getQuestionRecordId(), TenantTokenUtil.getTenantSid()) : recordMapper.getRecordMsg(recordInfoModel.getQuestionProcessStep(),recordInfoModel.getQuestionSolveStep(),recordInfoModel.getQuestionRecordId(), TenantTokenUtil.getTenantSid()));
        //进行筛选DTD需要的解决方案
        recordMessageVoList = recordMessageVoList.stream().filter(item -> recordInfoModel.getSolutionId().equals(item.getQuestionSolveStep().substring(0,5))).collect(Collectors.toList());
        JSONArray array = new JSONArray();
        if (!CollectionUtils.isEmpty(recordMessageVoList)) {
            array = assembleResponse(recordInfoModel,recordMessageVoList,status);
        }
        return array;
    }

}
