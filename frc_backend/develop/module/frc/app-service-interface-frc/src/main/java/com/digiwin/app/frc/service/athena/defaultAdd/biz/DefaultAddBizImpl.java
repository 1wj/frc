package com.digiwin.app.frc.service.athena.defaultAdd.biz;

import com.alibaba.fastjson.JSONArray;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.defaultAdd.DefaultAddBiz;
import com.digiwin.app.frc.service.athena.mtw.biz.*;
import com.digiwin.app.frc.service.athena.mtw.mapper.*;
import com.digiwin.app.frc.service.athena.ppc.biz.QuestionOccurStageBiz;
import com.digiwin.app.frc.service.athena.ppc.biz.QuestionRiskLevelBiz;
import com.digiwin.app.frc.service.athena.ppc.mapper.QuestionOccurStageMapper;
import com.digiwin.app.frc.service.athena.ppc.mapper.QuestionRiskLevelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * @version: 1.0
 * @Description: 默认基础资料入库
 * @Author: Author
 * @Date: 2022/6/22 15:09
 */
public class DefaultAddBizImpl implements DefaultAddBiz {

   /* @Autowired
    private QuestionSourceMapper questionSourceMapper;
    @Autowired
    private QuestionClassificationMapper questionClassificationMapper;
    @Autowired
    private QuestionOccurStageMapper questionOccurStageMapper;
    @Autowired
    private QuestionRiskLevelMapper questionRiskLevelMapper;
    @Autowired
    private QuestionSolutionEditMapper questionSolutionEditMapper;
    @Autowired
    private CraftDataMapper craftDataMapper;
    @Autowired
    private EquipmentMapper equipmentMapper;
    @Autowired
    private ProductSeriesMapper productSeriesMapper;
    @Autowired
    private QuestionItemMapper questionItemMapper;
    @Autowired
    private DefectCodeMapper defectCodeMapper;*/

    @Autowired
    private QuestionSourceBiz questionSourceBiz;
    @Autowired
    private QuestionClassificationBiz questionClassificationBiz;
    @Autowired
    private QuestionOccurStageBiz questionOccurStageBiz;
    @Autowired
    private QuestionRiskLevelBiz questionRiskLevelBiz;
    @Autowired
    private KeyBoardTemplateBiz keyBoardTemplateBiz;
    @Autowired
    private KeyBoardDisplayBiz keyBoardDisplayBiz;
    @Autowired
    private KeyBoardAuthorityBiz keyBoardAuthorityBiz;
    @Autowired
    private QuestionSolutionEditMapper questionSolutionEditMapper;
    @Autowired
    private CraftDataBiz craftDataBiz;
    @Autowired
    private EquipmentBiz equipmentBiz;
    @Autowired
    private ProductSeriesBiz productSeriesBiz;
    @Autowired
    private QuestionItemBiz questionItemBiz;
    @Autowired
    private DefectCodeBiz defectCodeBiz;



    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public int addCraftData(JSONArray dataContent) throws IOException, DWArgumentException {


      /*  //问题归属默认插入
        int i1=questionSourceMapper.addQuestionSourceInfo(questionSourceList);

        //问题分类默认插入
        int i2=questionClassificationMapper.addQuestionClassificationInfo(questionClassificationList);

        //问题发生阶段默认插入
        int i3=questionOccurStageMapper.addQuestionOccurStageInfo(questionOccurStageList);

        //风险等级默认插入
        int i4=questionRiskLevelMapper.addQuestionRiskLevelInfo(questionRiskLevelList);

        //解决方案默认插入
        int i5=questionSolutionEditMapper.addQuestionSolutionEditInfo(questionSolutionEditList);

        //工艺信息默认插入
        int i6=craftDataMapper.addCraftDataInfo(craftDataList);

        //生产线信息默认插入
        int i7=equipmentMapper.addEquipmentInfo(equipmentList);

        //产品信息默认插入
        int i8=productSeriesMapper.addProductSeriesInfo(productSeriesList);

        //物料信息默认插入
        int i9=questionItemMapper.addQuestionItemInfo(questionItemList);

        //缺陷信息默认插入
        int i10=defectCodeMapper.addDefectCodeInfo(defectCodeList);*/

        //问题归属默认插入
     /*   questionSourceBiz.addQuestionSource(questionSourceArray);
        //问题分类默认插入
        questionClassificationBiz.addQuestionClassification(questionClassificationArray);
        //问题发生阶段默认插入
        questionOccurStageBiz.addQuestionOccurStageInfo(questionOccurStageArray);
        //风险等级默认插入
        questionRiskLevelBiz.addQuestionRiskLevel(questionRiskLevelArray);

        //看板模板默认插入（分情况讨论 注意）
        keyBoardTemplateBiz.addKeyBoardTemplate(KeyBoardTemplateArray);
        //看板显示默认插入
        keyBoardDisplayBiz.addKeyBoardDisplay(keyBoardDisplayArray);
        //看板权限默认插入
        keyBoardAuthorityBiz.addKeyBoardAuthority(KeyBoardAuthorityArray);

        //解决方案默认插入
        questionSolutionEditMapper.addQuestionSolutionEditInfo(questionSolutionEditArray);

        //工艺信息默认插入
        craftDataBiz.addCraftData(craftDataArray);
        //生产线信息默认插入
        equipmentBiz.addEquipment(equipmentArray);
        //产品信息默认插入
        productSeriesBiz.addProductSeries(productSeriesArray);
        //物料信息默认插入
        questionItemBiz.addQuestionItem(questionItemArray);
        //缺陷信息默认插入
        defectCodeBiz.addDefectCodeInfo(defectCodeInfoArray);*/
        return 0;
    }
}
