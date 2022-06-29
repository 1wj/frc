package com.digiwin.app.frc.service.athena.mtw.biz.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.mtw.biz.KanbanFoolproofBiz;
import com.digiwin.app.frc.service.athena.mtw.mapper.KeyBoardDisplayMapper;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionSolutionEditMapper;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWServiceContext;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: xieps
 * @Date: 2022/1/10 13:13
 * @Version 1.0
 * @Description 看板防呆校验Biz-impl
 */
@Service
public class KanbanFoolproofBizImpl implements KanbanFoolproofBiz {

    @Autowired
    private KeyBoardDisplayMapper keyBoardDisplayMapper;

    @Autowired
    private QuestionSolutionEditMapper questionSolutionEditMapper;

    @Override
    public List<Object> getKanbanFoolproofInfo(JSONArray dataContent) throws DWArgumentException {
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<Object> names = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            JSONObject jsonObject = dataContent.getJSONObject(i);
            String deletedType = jsonObject.getString("wait_delete_type");
            String deletedKeyId = jsonObject.getString("wait_delete_key_id");
            if (StringUtils.isEmpty(deletedKeyId)) {
                throw new DWArgumentException("waitDeletedKeyId", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!"1".equals(deletedType) && !"2".equals(deletedType)) {
                throw new DWArgumentException("waitDeletedType", MultilingualismUtil.getLanguage("parameterError"));
            }
            if ("1".equals(deletedType)) {
                //表明删除的是模板信息  返回相关联的方案名称
                List<String> solutionNames = keyBoardDisplayMapper.getSolutionNamesByDeletedModelOid(deletedKeyId,tenantSid);
                List<Map<String, Object>> list = new ArrayList<>();
                for (String solutionName : solutionNames) {
                    Map<String, Object> map = new HashMap<>(16);
                    map.put("related_name", solutionName);
                    list.add(map);
                }
                names = ListUtils.union(names, list);
            } else {
                //表明删除的是方案信息  返回相关联的模板名称
                String solutionNo = questionSolutionEditMapper.getSolutionNoByDeletedKeyId(deletedKeyId,tenantSid);
                List<String> modelNames = keyBoardDisplayMapper.getModelNamesByDeletedSolutionNo(solutionNo,tenantSid);
                List<Map<String, Object>> list = new ArrayList<>();
                for (String modelName : modelNames) {
                    Map<String, Object> map = new HashMap<>(16);
                    map.put("related_name", modelName);
                    list.add(map);
                }
                names = ListUtils.union(names, list);
            }
        }
        return  names.stream().distinct().collect(Collectors.toList());
    }
}
