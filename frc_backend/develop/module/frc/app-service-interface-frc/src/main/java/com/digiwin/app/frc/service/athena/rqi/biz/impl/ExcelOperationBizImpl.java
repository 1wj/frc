package com.digiwin.app.frc.service.athena.rqi.biz.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.ClassificationSourceMidEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionClassificationEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.QuestionClassificationQueryVo;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.QuestionClassificationVo;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.QuestionSourceQueryVo;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.QuestionSourceVo;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionClassificationMapper;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionSolutionEditMapper;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionSourceMapper;
import com.digiwin.app.frc.service.athena.ppc.domain.entity.LiablePersonConfigEntity;
import com.digiwin.app.frc.service.athena.ppc.domain.entity.QuestionClassificationLiablePersonConfigEntity;
import com.digiwin.app.frc.service.athena.ppc.domain.entity.QuestionRiskLevelEntity;
import com.digiwin.app.frc.service.athena.ppc.domain.model.LiablePersonConfigModel;
import com.digiwin.app.frc.service.athena.ppc.domain.vo.ClassificationVo;
import com.digiwin.app.frc.service.athena.ppc.mapper.LiablePersonConfigMapper;
import com.digiwin.app.frc.service.athena.ppc.mapper.QuestionClassificationLiablePersonConfigMapper;
import com.digiwin.app.frc.service.athena.ppc.mapper.QuestionRiskLevelMapper;
import com.digiwin.app.frc.service.athena.rqi.biz.ExcelOperationBiz;
import com.digiwin.app.frc.service.athena.rqi.constants.LiablePersonConfigExcelConstant;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.service.DWFile;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

/**
 * @Author: xieps
 * @Date: 2022/4/27 23:45
 * @Version 1.0
 * @Description
 */
@Service
public class ExcelOperationBizImpl implements ExcelOperationBiz {


    @Autowired
    private QuestionClassificationMapper questionClassificationMapper;

    @Autowired
    private QuestionSourceMapper questionSourceMapper;

    @Autowired
    private QuestionRiskLevelMapper questionRiskLevelMapper;


    @Autowired
    private QuestionSolutionEditMapper questionSolutionEditMapper;

    @Autowired
    private QuestionClassificationLiablePersonConfigMapper questionClassificationLiablePersonConfigMapper;

    @Autowired
    private LiablePersonConfigMapper liablePersonConfigMapper;

    @Override
    public Boolean importExcelInfo(InputStream inputStream) throws IOException {
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(inputStream);
            // 获取sheet数量
//            int sheetIndexes = workbook.getActiveSheetIndex();
            //目前测试取固定值
            Sheet sheet = workbook.getSheetAt(2);
            // 获取数据
            Logger.getGlobal().info("将表数据映射实体类.......");
            List<QuestionClassificationEntity> entities = sheetData2Entity(sheet);
            //添加问题分类信息
            Logger.getGlobal().info("开始添加分类信息");
            questionClassificationMapper.addQuestionClassificationInfo(entities);
            Logger.getGlobal().info("添加分类信息成功");
        }catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }finally {
            IOUtils.closeQuietly(inputStream);
            workbook.close();
        }
        return true;
    }


    private List<QuestionClassificationEntity> sheetData2Entity(Sheet sheet) {
        List<QuestionClassificationEntity> entityList = new ArrayList<>();
        List<Map<Integer,String>> mapTempStore = new ArrayList<>();
        int lastRowNum = sheet.getLastRowNum();
        for (int i = 4; i <= lastRowNum; i++) {
            Map<Integer,String> objMap = new HashMap<>();
            Row row = sheet.getRow(i);
            for (int j = 0; j < 4; j++) {
                Cell cell = row.getCell(j);
                cell.setCellType(CellType.STRING);
                objMap.put(j, cell.getStringCellValue());
            }
            mapTempStore.add(objMap);
        }
        return convertAndPackageData(mapTempStore, entityList);
    }

    private List<QuestionClassificationEntity> convertAndPackageData(List<Map<Integer, String>> mapTempStore, List<QuestionClassificationEntity> entityList) {
        List<ClassificationSourceMidEntity> midList = new ArrayList<>();
        for (Map<Integer, String> map : mapTempStore) {
            QuestionClassificationEntity entity = new QuestionClassificationEntity();
            entity.setClassificationNo(map.get(0));
            entity.setClassificationName(map.get(1));
            entity.setQuestionAttribution(map.get(2));
            entity.setRemarks(map.get(4));
            entity.setOid(IdGenUtil.uuid());
            entity.setTenantSid(TenantTokenUtil.getTenantSid());
            entity.setCreateTime(new Date());
            entity.setCreateName(TenantTokenUtil.getUserName());
            entity.setManageStatus("Y");
            //获取来源名称
            String sourceName = map.get(3);
            if(!StringUtils.isEmpty(sourceName) && sourceName.contains(",")){
                String[] strings = sourceName.split(",");
                for (int i = 0; i < strings.length; i++) {
                    List<QuestionSourceQueryVo> questionSourceInfo = questionSourceMapper.getQuestionSourceInfo(TenantTokenUtil.getTenantSid(), new QuestionSourceVo(null, null, strings[i], null, null, null, null));
                    midList.add(new ClassificationSourceMidEntity(
                            IdGenUtil.uuid(),TenantTokenUtil.getTenantSid(),entity.getOid(),questionSourceInfo.get(0).getOid(),new Date(),TenantTokenUtil.getUserName(),null,null
                    ));
                }
            }else{
                List<QuestionSourceQueryVo> questionSourceInfo = questionSourceMapper.getQuestionSourceInfo(TenantTokenUtil.getTenantSid(), new QuestionSourceVo(null, null, sourceName, null, null, null, null));
                midList.add(new ClassificationSourceMidEntity(
                        IdGenUtil.uuid(),TenantTokenUtil.getTenantSid(),entity.getOid(),questionSourceInfo.get(0).getOid(),new Date(),TenantTokenUtil.getUserName(),null,null
                ));
            }
            entityList.add(entity);
        }
        //添加问题分类来源关联表信息
        questionClassificationMapper.addClassificationSourceMidInfo(midList);
        return entityList;
    }



    /**
     * 导入Excel表格中的问题责任人配置
     * *任务阶段-问题处理阶段 （1-问题确认-QC，2-问题评审，3-问题处理，4-问题验收）
     * *风险等级-风险等级名称
     * *问题归属-问题归属（1-内部，2-外部）
     * *问题来源-问题来源名称
     * *问题分类（多选）
     * *问题责任人（问题责任人名称）
     * 以上字段都为“*xxx”而非“xxx”
     * @param file
     * @return
     */
    @Override
    @Transactional
    public boolean importLiablePersonConfigInfo(DWFile file) throws Exception {
        String fileName = file.getFileName();
        InputStream inputStream = file.getInputStream();
        List<Map<String, Object>> list= liabliePersonReadExcel(fileName,inputStream);
        List<LiablePersonConfigModel> models = importCheckAndHandleData(list);
        List<LiablePersonConfigEntity> entities = convertModel2Entity(models);
        liablePersonConfigMapper.addLiablePersonConfigInfo(entities);
        List<QuestionClassificationLiablePersonConfigEntity> clList = convertClassificationRelationsList(models);
        questionClassificationLiablePersonConfigMapper.addQuestionClassificationLiablePersonConfig(clList);
        return true;
    }

    /**
     * @Description 读取Excel文件
     * @param fileName
     * @param inputStream
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @author Jiangyw
     * @Date 2022/4/28
     */
    public static List<Map<String, Object>> liabliePersonReadExcel(String fileName, InputStream inputStream) throws Exception{
        boolean ret = isXls(fileName);
        Workbook workbook = null;
        // 根据后缀创建不同的对象
        if(ret){
            workbook = new HSSFWorkbook(inputStream);
        }else{
            workbook = new XSSFWorkbook(inputStream);
        }
        Sheet sheet = workbook.getSheetAt(6);
        // 得到标题行
        Row titleRow = sheet.getRow(9);
        int lastRowNum = sheet.getLastRowNum();
        int lastCellNum = titleRow.getLastCellNum();
        List<Map<String, Object>> list = new ArrayList<>();
        for(int i = 10; i <= lastRowNum; i++ ){
            Map<String, Object> map = new HashMap<>();
            Row row = sheet.getRow(i);

            //空行结束
            boolean flag = true;
            for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
                try{
                    row.getCell(j).setCellType(CellType.STRING);
                    if (!row.getCell(j).getStringCellValue().equals("")) {
                        flag = false;
                    }
                }catch (NullPointerException e) {
                    continue;
                }
            }
            if (flag) {
                break;
            }
            for(int j = 1; j < lastCellNum - 1; j++){
                // 得到列名
                try{
                    String key = titleRow.getCell(j).getStringCellValue();
                    Cell cell = row.getCell(j);
                    cell.setCellType(CellType.STRING);
                    map.put(key, cell.getStringCellValue());
                }catch (NullPointerException e) {
                    continue;
                }
            }
            list.add(map);
        }
        workbook.close();
        return list;
    }

    /**
     * 格式校验
     * @param fileName
     * @return
     */
    public static boolean isXls(String fileName){
        if(fileName.matches("^.+\\.(?i)(xls)$")){
            return true;
        }else if(fileName.matches("^.+\\.(?i)(xlsx)$")){
            return false;
        }else{
            throw new RuntimeException("文件名格式错误");
        }
    }

    private List<LiablePersonConfigEntity> convertModel2Entity(List<LiablePersonConfigModel> models) {
        List<LiablePersonConfigEntity> entities = new ArrayList<>();
        Long tenantsid = TenantTokenUtil.getTenantSid();
        for (LiablePersonConfigModel model : models) {
            model.setOid(IdGenUtil.uuid());
            LiablePersonConfigEntity entity = new LiablePersonConfigEntity();
            BeanUtils.copyProperties(model, entity);
            entity.setTenantSid(tenantsid);
            entity.setCreateName(TenantTokenUtil.getUserName());
            entity.setCreateTime(new Date());
            entities.add(entity);
        }
        return entities;
    }

    /**
     * 获取类别关联表的entity集合
     * @param models
     * @return
     */
    private List<QuestionClassificationLiablePersonConfigEntity> convertClassificationRelationsList(List<LiablePersonConfigModel> models) throws DWArgumentException {
        List<QuestionClassificationLiablePersonConfigEntity> list = new ArrayList<>();
        Long tenantsid = TenantTokenUtil.getTenantSid();
        for(LiablePersonConfigModel model :models){
            String oid = model.getOid();
            for(ClassificationVo vo : model.getClassificationInfo()){
                QuestionClassificationLiablePersonConfigEntity entity = new QuestionClassificationLiablePersonConfigEntity();
                entity.setOid(IdGenUtil.uuid());
                entity.setClassificationOid(vo.getClassificationId());
                entity.setLiablePersonConfigOid(oid);
                entity.setTenantSid(tenantsid);
                entity.setCreateName(TenantTokenUtil.getUserName());
                entity.setCreateTime(new Date());
                list.add(entity);
            }
        }
        return list;
    }

    /**
     * @Description 导入Excel的校验和封装
     * @param list
     * @return java.util.List<com.digiwin.app.frc.service.athena.ppc.domain.entity.LiablePersonConfigEntity>
     * @author Jiangyw
     * @Date 2022/4/28
     */
    private List<LiablePersonConfigModel> importCheckAndHandleData(List<Map<String, Object>> list) throws DWArgumentException {
        List<LiablePersonConfigModel> entityList = new ArrayList<>();
        Map<String,LiablePersonConfigModel> entitiesMap = new HashMap<>();
        Long tenantSid = TenantTokenUtil.getTenantSid();
//        Long tenantSid = 434059337671232L;
        List<QuestionRiskLevelEntity> risks = questionRiskLevelMapper.getQuestionRiskLevelInfo(tenantSid,null,null,null,null,null);
        Map<String,String> risks1 = new HashMap<>();
        for (QuestionRiskLevelEntity risk : risks) {
            risks1.put(risk.getRiskLevelName(),risk.getOid());
        }

        List<QuestionSourceQueryVo> sources = questionSourceMapper.getQuestionSourceInfo(tenantSid,new QuestionSourceVo());
        Map<String,String> sources1 = new HashMap<>();
        for (QuestionSourceQueryVo source : sources) {
            sources1.put(source.getSourceName(),source.getOid());
        }

        List<QuestionClassificationQueryVo> classifications = questionClassificationMapper.getQuestionClassificationInfo(tenantSid, new QuestionClassificationVo());
        Map<String,String> classifications1 = new HashMap<>();
        for (QuestionClassificationQueryVo classification : classifications) {
            classifications1.put(classification.getClassificationName(),classification.getOid());
        }

        //改为写死
//        List<QuestionSolutionEditEntity> solutionEdits = questionSolutionEditMapper.getQuestionSolutionEditInfo(tenantSid, null,null,null,null,null,null);
//        Map<String,String> solutionEdits1 = new HashMap<>();
//        for (QuestionSolutionEditEntity solutionEdit : solutionEdits) {
//            solutionEdits1.put(solutionEdit.getSolutionName(),solutionEdit.getOid());
//        }
        String solutionOid = "65c891edfbaf42c29455235fa7046579";

        for(int row = 1; row <list.size()+1; row++){
            Map<String, Object> map = list.get(row-1);

            //任务阶段校验
            String configFlag = null;
            Map<String, String> configFlagMap = LiablePersonConfigExcelConstant.CONFIG_FLAG_MAP;
            if(configFlagMap.containsKey(map.get(LiablePersonConfigExcelConstant.CONFIG_FLAG))){
                configFlag = configFlagMap.get((String) map.get(LiablePersonConfigExcelConstant.CONFIG_FLAG));
            }else {
                throw new DWArgumentException("config_flag","导入失败，第" + row + "行的任务阶段有误");
            }

            //风险等级存在校验
            String riskName = (String) map.get(LiablePersonConfigExcelConstant.RISK_LEVEL_NAME);
            if(!risks1.containsKey(riskName)){
                throw new DWArgumentException("risk_level_name","导入失败，第" + row + "行的风险等级不存在");
            }

            //问题归属校验
            String attributionNo = (String) map.get(LiablePersonConfigExcelConstant.ATTRIBUTION_NO);
            if (attributionNo == null || (!attributionNo.equals("1") && !attributionNo.equals("2"))) {
                throw new DWArgumentException("attribution_no","导入失败，第" + row + "行的问题归属有误");
            }

            //问题来源校验
            String sourceName = (String) map.get(LiablePersonConfigExcelConstant.SOURCE_NAME);
            if (!sources1.containsKey(sourceName)) {
                throw new DWArgumentException("source_name","导入失败，第" + row + "行的问题来源不存在");
            }

            //问题分类校验
            String classificationName = (String) map.get(LiablePersonConfigExcelConstant.CLASSIFICATION_NAME);
            if (!classifications1.containsKey(classificationName)) {
                throw new DWArgumentException("classification_name","导入失败，第" + row + "行的问题分类不存在");
            }
            ClassificationVo classificationVo = new ClassificationVo();
            classificationVo.setClassificationName((String) map.get(LiablePersonConfigExcelConstant.CLASSIFICATION_NAME));
            classificationVo.setClassificationId(classifications1.get(classificationVo.getClassificationName()));

//            暂时去除校验
//            解决方案校验
//            String solutionStepFlag = (String) map.get(LiablePersonConfigExcelConstant.SOLUTION_EDIT_FLAG);
//            if(configFlag.equals("QH")){
//                if (!solutionStepFlag.equals("1")&&!solutionStepFlag.equals("2")) {
//                    throw new DWArgumentException("solution_step","导入失败，第" + row + "行的问题解决方案有误");
//                }
//            }

            String liablePersonName = (String) map.get(LiablePersonConfigExcelConstant.LIABLE_PERSON_NAME);
            String liablePersonId = (String) map.get(LiablePersonConfigExcelConstant.LIABLE_PERSON_ID);

//                暂时去除校验
//            String acceptanceRole = (String) map.get(LiablePersonConfigExcelConstant.ACCEPTANCE_ROLE);
//            if(configFlag.equals("QAC")){
//                if (!acceptanceRole.equals("1")&&!acceptanceRole.equals("2")&&!acceptanceRole.equals("3")) {
//                    throw new DWArgumentException("acceptanceRole","导入失败，第" + row + "行的验收人角色有误");
//                }
//            }

            StringBuilder keyBuilder = new StringBuilder();
            keyBuilder.append(configFlag).append(riskName).append(attributionNo).append(sourceName).append(liablePersonName).append(liablePersonId);
            String key = keyBuilder.toString();
            if (entitiesMap.containsKey(key)) {
                entitiesMap.get(key).getClassificationInfo().add(classificationVo);
            }else {
                LiablePersonConfigModel model = new LiablePersonConfigModel();
                model.setConfigFlag(configFlag);
                model.setRiskLevelId(risks1.get(riskName));
                model.setAttributionNo(attributionNo);
                model.setSourceOid(sources1.get(sourceName));
                model.setClassificationInfo(new ArrayList<ClassificationVo>(){{
                    add(classificationVo);
                }});
                model.setLiablePersonId(liablePersonId);
                model.setLiablePersonName(liablePersonName);
                if(configFlag.equals("QH")) {
//                    model.setSolutionOid(solutionEdits1.get(LiablePersonConfigExcelConstant.SOLUTION_EDIT_FLAG_MAP.get(solutionStepFlag)));
//                    改为写死
                    model.setSolutionOid(solutionOid);
                }
//                暂时去除校验
//                if (configFlag.equals("QAC")) {
//                    model.setAcceptanceRole(acceptanceRole);
//                }
                entitiesMap.put(key,model);
                entityList.add(model);
            }
        }
        return entityList;
    }

}
