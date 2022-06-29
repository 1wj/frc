package com.digiwin.app.frc.service.athena.qdh.biz.impl;

import com.digiwin.app.dao.DWDao;
import com.digiwin.app.frc.service.athena.qdh.biz.DataChangeBiz;
import com.digiwin.app.service.DWServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName DataChangeBizImpl
 * @Description athena侦测服务实现类
 * @Author author
 * @Date 2021/11/10 17:38
 * @Version 1.0
 **/
@Service
public class DataChangeBizImpl implements DataChangeBiz {
    @Autowired
    @Qualifier("dw-dao")
    private DWDao dao;
    private String datetime_s = "";
    private String datetime_e = "";
    private static Map<String, String> cache = new ConcurrentHashMap<String, String>();

    Logger logger = LoggerFactory.getLogger(DataChangeBizImpl.class);

    /**
     * 侦测
     * 备注：侦测借口源码，无需任何调试改动
     * @param headers 侦测请求头参数
     * @param messageBody 侦测请求入参
     * @return 按athena规格返回
     * @throws Exception 异常处理
     */
    @Override
    public Map<String, Object> dataChange(Map<String, String> headers, Map<String, Object> messageBody) throws Exception {
        Map std_data = (Map) messageBody.get("std_data");
        Map para = std_data.containsKey("parameter") ? (Map) std_data.get("parameter") : null;
        List<Map<String, Object>> headList = new ArrayList<Map<String, Object>>();
        DWServiceContext context = DWServiceContext.getContext();
        long tenantSid = (long) context.getProfile().get("tenantSid");
        if (para != null && para.containsKey("rules")) {
            List<Map> rules = (List) para.get("rules");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            if (para.containsKey("datetime_s")) {
                datetime_s = para.get("datetime_s").toString();
            }
            if (para.containsKey("datetime_e")) {
                datetime_e = para.get("datetime_e").toString();
            }
            for (Map<String, Object> item : rules) {
                Map headObj = new HashMap();
                List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
                String ruleId = item.get("rule_id").toString();
                int version = Integer.parseInt(item.get("version").toString());
                String sql = "";
                headObj.put("rule_id", ruleId);
                if (version > -1) {
                    if (cache.containsKey(ruleId)) {
                        sql = cache.get(ruleId);
                    } else {
                        sql = getSqlString(item);
                        cache.put(ruleId, sql);
                    }
                } else if (version == -1) {
                    sql = getSqlString(item);
                    logger.info("---------------------SQL为--------------------"+sql);
                }
                if (!sql.equals("")) {
                    String monitorType = item.get("monitor_type").toString();
                    List<Map<String, Object>> resCol = new ArrayList<Map<String, Object>>();
                    if (!monitorType.equals("CUSTOM")) {
                        resCol = dao.select("-${tenantsid}" + sql, tenantSid, datetime_s, datetime_e);
                    } else {
                        resCol = dao.select("-${tenantsid}" + sql, tenantSid);
                    }
                    //组织返回值
                    List<Map> actionParams = (List) item.get("action_params");
                    if (resCol.size() > 0) {
                        for (Map<String, Object> retObj : resCol) {
                            Map detailObj = new HashMap();
                            if (actionParams != null) {
                                for (Map<String, Object> acPra : actionParams) {
                                    String acName = acPra.get("name").toString();
                                    if (acPra.get("type").toString().equals("COLUMN")) {
                                        String value = acPra.get("value").toString();
                                        String[] valueArr = value.split("\\.");
                                        detailObj.put(acName, retObj.get(valueArr[valueArr.length - 1]));
                                    }
                                    if (acPra.get("type").toString() == "FUNCTION") {
                                        String funcName = acPra.get("func_name").toString();
                                        List<Map> functionParams = (List) acPra.get("function_params");
                                        if (funcName == "CONCAT") {
                                            String concatStr = "";
                                            for (Map<String, Object> funPara : functionParams) {
                                                if (funPara.get("type").toString() == "COLUMN") {
                                                    String value = funPara.get("value").toString();
                                                    String[] valueArr = value.split("\\.");
                                                    concatStr += retObj.get(valueArr[valueArr.length - 1]);
                                                } else if (funPara.get("type").toString() == "CONSTANT") {
                                                    concatStr += funPara.get("value").toString();
                                                }
                                            }
                                            detailObj.put(acName, concatStr);
                                        }
                                        if (acPra.get("type").toString() == "CONSTANT") {
                                            detailObj.put(acName, acPra.get("value").toString());
                                        }
                                    }
                                }
                            }
                            resList.add(detailObj);
                        }
                    }
                }
                headObj.put("change_objects", resList);
                headList.add(headObj);
            }
        }
        Map<String, Object> result = new HashMap<>(16);
        result.put("response_objects", headList);
        return result;
    }

    private String getSqlString(Map rule) {
        StringBuffer stringBufferSql = new StringBuffer();
        String conGroup = "";//where条件
        StringBuffer selList = new StringBuffer();//select字段集合
        String monitorType = rule.get("monitor_type").toString();
        String table = rule.get("table").toString();//主表
        String alias = rule.containsKey("alias") && !rule.get("alias").toString().equals("") ? rule.get("alias").toString() : table;
        String dateColName = "";
        conGroup = " WHERE " + alias + ".tenantsid = ? AND ";

        if (!monitorType.equals("CUSTOM")) {
            if (monitorType.equals("MODIFIED")) {
                dateColName = "modified_date";
            } else if (monitorType.equals("CREATED")) {
                dateColName = "create_date";
            } else if (monitorType.equals("APPROVED")) {
                dateColName = "approve_date";
            }
            List<Map> defaultPara = (List) rule.get("default_params");
            if (defaultPara != null) {
                for (Map<String, Object> map : defaultPara) {
                    if (map.get("name") != null) {
                        List<String> values = (List) map.get("values");
                        if (values != null && values.size() > 0) {
                            if (map.get("name").equals("APPROVE_STATUS")) {
                                conGroup += alias + "." + values.get(0) + " = 'Y' AND ";
                            }
                        }
                    }
                }
            }
            conGroup += alias + "." + dateColName + " BETWEEN ? AND ? ";//侦测时间条件参数化
        }
        String selectType = rule.containsKey("select_type") ? rule.get("select_type").toString() : "";
        Boolean isDistinct = false;
        if (!selectType.equals("") && selectType.equals("DISTINCT")) {//判断是否需要去重
            isDistinct = true;
        }
        List<Map> returnColumns = (List) rule.get("return_columns");//获取需要select的字段
        for (Map<String, Object> retObj : returnColumns) {
            String retName = retObj.get("name").toString();
            String retAlias = retObj.containsKey("alias") && !retObj.get("alias").toString().equals("") ? retObj.get("alias").toString() : "";
            Boolean isFirst = selList.toString().equals("");
            if (!retAlias.equals("")) {
                selList.append((isFirst ? " " : ",") + retName + " AS " + retAlias);
            } else {
                selList.append((isFirst ? " " : ",") + retName);
            }
        }

        //添加与具体业务有关的动态附件条件
        Map<String, Object> dynConObj = rule.containsKey("dynamic_condition") ? (Map) rule.get("dynamic_condition") : null;
        if (dynConObj != null && dynConObj.size() > 0) {
            String dynConType = dynConObj.get("type").toString();
            if (dynConType.equals("SINGLE")) {
                String qryCon = GetSingleCon(dynConObj);
                if (!qryCon.equals("")) {
                    conGroup += " AND " + qryCon;
                }
            } else if (dynConType.equals("AND_GROUP")) {
                String andGroupCon = GetAndOrGroupCon(dynConObj);
                if (!andGroupCon.equals("")) {
                    conGroup += " AND (" + andGroupCon + ") ";
                }

            } else if (dynConType.equals("OR_GROUP")) {
                String orGroupCon = GetAndOrGroupCon(dynConObj);
                if (!orGroupCon.equals("")) {
                    conGroup += " AND (" + orGroupCon + ") ";
                }
            }
        }

        stringBufferSql.append(" SELECT " + (isDistinct ? "DISTINCT" : "")).append(selList.toString());
        stringBufferSql.append(" FROM " + table + (alias.equals("") ? "" : " AS " + alias));

        //拼接join语句
        List<Map<String, Object>> joinParams = rule.containsKey("join_params") ? (List) rule.get("join_params") : null;
        if (joinParams != null && joinParams.size() > 0) {
            Collections.sort(joinParams, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    Integer seq1 = Integer.valueOf(o1.get("seq").toString());
                    Integer seq2 = Integer.valueOf(o2.get("seq").toString());
                    return seq1.compareTo(seq2);
                }
            });//join定义的顺序，SQL对此会有依赖,按顺序添加join项
            for (Map<String, Object> joinObj : joinParams) {
                String joinType = joinObj.get("type").toString();
                String joinTable = joinObj.get("table").toString();
                String joinAlias = joinObj.containsKey("alias") && !joinObj.get("alias").toString().equals("") ? joinObj.get("alias").toString() : joinTable;
                Map<String, Object> conObj = (Map<String, Object>) joinObj.get("condition");
                String qryConGroup = "";
                if (conObj != null) {
                    String conType = conObj.get("type").toString();
                    if (conType.equals("SINGLE")) {
                        String qryCon = GetSingleCon(conObj);
                        if (!qryCon.equals("")) {
                            qryConGroup = qryCon;
                        }
                    } else if (conType.equals("AND_GROUP")) {
                        List<Map<String, Object>> items = (List<Map<String, Object>>) conObj.get("items");
                        String andGroupCon = "";
                        for (Map<String, Object> subCon : items) {
                            String qryCon = GetSingleCon(subCon);
                            if (!qryCon.equals("")) {
                                andGroupCon += andGroupCon.equals("") ? " " + qryCon : " AND " + qryCon;
                            }
                        }
                        qryConGroup = andGroupCon;
                    } else if (conType.equals("OR_GROUP")) {
                        List<Map<String, Object>> items = (List<Map<String, Object>>) conObj.get("items");
                        String orGroupCon = "";
                        for (Map<String, Object> subCon : items) {
                            String qryCon = GetSingleCon(subCon);
                            if (!qryCon.equals("")) {
                                orGroupCon += orGroupCon.equals("") ? " " + qryCon : " OR " + qryCon;
                            }
                        }
                        qryConGroup = orGroupCon;
                    }
                }
                if (qryConGroup != null) {
                    stringBufferSql.append(" " + joinType + " JOIN " + joinTable + (joinAlias.equals("") ? " " : " AS " + joinAlias))
                            .append(" ON " + qryConGroup);
                }
            }
        }

        return stringBufferSql.append(conGroup).toString();
    }

    private String GetAndOrGroupCon(Map<String, Object> dynConObj) {
        String dynConType = dynConObj.get("type").toString();
        String type = "";
        if (dynConType.equals("AND_GROUP")) {
            type = " AND ";
        } else if (dynConType.equals("OR_GROUP")) {
            type = " OR ";
        }
        List<Map> items = (List) dynConObj.get("items");
        String groupCon = "";
        for (Map<String, Object> subCon : items) {
            String subConType = subCon.get("type").toString();
            if (subConType.equals("SINGLE")) {
                String qryCon = GetSingleCon(subCon);
                if (!qryCon.equals("")) {
                    groupCon += (groupCon.equals("") ? " " : type) + qryCon;
                }
            } else if (subConType.equals("OR_GROUP") || subConType.equals("AND_GROUP")) {
                String groupType = "";
                if (subConType.equals("OR_GROUP")) {
                    groupType = " OR ";
                } else if (subConType.equals("AND_GROUP")) {
                    groupType = " AND ";
                }
                List<Map> orItems = (List) subCon.get("items");
                String orGroupCon = "";
                for (Map<String, Object> orCon : orItems) {
                    if (orCon.get("type").equals("SINGLE")) {
                        String qryCon = GetSingleCon(orCon);
                        if (!qryCon.equals("")) {
                            orGroupCon += orGroupCon.equals("") ? " " + qryCon : groupType + qryCon;
                        }
                    } else {
                        String orGroupCon2 = GetAndOrGroupCon(orCon);
                        if (!orGroupCon2.equals("")) {
                            orGroupCon += orGroupCon.equals("") ? " (" + orGroupCon2 + ") " : groupType + "(" + orGroupCon2 + ") ";
                        }
                    }
                }
                if (!groupCon.equals("")) {
                    groupCon += type + "(" + orGroupCon + ") ";
                } else {
                    groupCon = "(" + orGroupCon + ") ";
                }
            }
        }
        return groupCon;
    }

    private String GetSingleCon(Map<String, Object> conObj) {
        Object conLeft = conObj.get("left").toString();
        String conLeftValueType = conObj.get("left_value_type").toString();
        String conOp = conObj.get("op").toString();
        Object conRight = conObj.containsKey("right") ? conObj.get("right").toString() : "";
        String conRightValueType = conObj.containsKey("right_value_type") ? conObj.get("right_value_type").toString() : "";
        Object leftPro = getValue(conLeft, conLeftValueType);
        Object rightPro = conRight.equals("") ? null : getValue(conRight, conRightValueType);
        String qryCon = "";
        switch (conOp) {
            case "EQUAL":
                qryCon = leftPro + " = " + rightPro;
                break;
            case "NOT_EQUAL":
                qryCon = leftPro + " != " + rightPro;
                break;
            case "GREATER_EQUAL":
                qryCon = leftPro + " >= " + rightPro;
                break;
            case "GREATER_THAN":
                qryCon = leftPro + " > " + rightPro;
                break;
            case "LESS_EQUAL":
                qryCon = leftPro + " <= " + rightPro;
                break;
            case "LESS_THAN":
                qryCon = leftPro + " < " + rightPro;
                break;
            case "IS_NULL":
                if (leftPro != null && !leftPro.toString().equals("")) {
                    qryCon = leftPro + " IS NULL";
                }
                break;
            case "IS_NOT_NULL":
                if (leftPro != null && !leftPro.toString().equals("")) {
                    qryCon = leftPro + " IS NOT NULL";
                }
                break;
        }
        return qryCon;
    }

    private Object getValue(Object proObj, String valueType) {
        Object retObj = null;
        switch (valueType) {
            case "COLUMN":
                retObj = proObj;
                break;
            case "STRING":
                retObj = "'" + proObj + "'";
                break;
            case "BOOLEAN":
                retObj = proObj;
                break;
            case "DATE":
                retObj = "'" + proObj + "'";
                break;
            case "TIME":
                retObj = "'" + proObj + "'";
                break;
            case "DATE_TIME":
                retObj = "'" + proObj + "'";
                break;
            case "INT":
                retObj = proObj;
                break;
            case "DECIMAL":
                retObj = proObj;
                break;
        }
        return retObj;
    }

}
