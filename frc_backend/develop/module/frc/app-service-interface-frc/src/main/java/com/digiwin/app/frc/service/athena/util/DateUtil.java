package com.digiwin.app.frc.service.athena.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

/**
 * @ClassName DateUtil
 * @Description TODO
 * @Author author
 * @Date 2021/12/3 8:06
 * @Version 1.0
 **/
public class DateUtil {
    /**
     * 获取预计完成时间
     * @param expectDays
     * @return
     */
    public static String getExpectDate(int expectDays){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE,expectDays);
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(cal.getTime());
    }

    /**
     * String 转 date
     * @param param
     * @return
     */
    public static Date string2Date(String param,String format){
        if (StringUtils.isEmpty(param)) {
            return null;
        }
        Date date = null;
        try {
            SimpleDateFormat ft = new SimpleDateFormat(format);
            date = ft.parse(param);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取预计完成时间
     * @param expectDaysOfHour
     * @return
     */
    public static String getExpectDateWithHourMinute(int expectDaysOfHour){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY,expectDaysOfHour);
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(cal.getTime());
    }

    /**
     * 获取预计完成时间
     * @param expectMinute
     * @return
     */
    public static String getExpectDateWithHourMinute(String expectMinute){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE,Integer.parseInt(expectMinute));
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(cal.getTime());
    }


    /**
     * 预期完成时间
     * 字符串0.1精度小时 转 字符串分钟
     * @param hours
     * @return
     */
    public static String hours2Minutes(String hours) throws OperationException {
        String regex = "^(([0]|([1-9]\\d*))(\\.\\d)?)$";
        if(!hours.matches(regex)){
            throw new DWRuntimeException("expectCompleteDays does not allow decimals");
        }
        float minutes = Float.parseFloat(hours) * 60;
        if (minutes < 0)
            throw new OperationException("预计完成时间请输入非负数");
        return String.valueOf((int)minutes);
    }

    /**
     * 预期完成时间
     * 字符串分钟 转 字符串0.1精度小时
     * @param minutes
     * @return
     */
    public static String minutes2Hours(String minutes){
        StringBuffer hours = new StringBuffer(String.valueOf(Integer.parseInt(minutes) / 6));
        if(hours.length()==1) {
            hours.insert(0,"0.");
        }else {
            hours.insert(hours.length()-1,'.');
        }
        return hours.toString();
    }
    /**
     *功能描述 处理措施阶段时分秒
     * @author cds
     * @date 2022/5/19
     * @param
     * @return
     */

    public static void measures(JSONArray measures){
        // 时间处理 更新时 添加时分秒
        for (Iterator<Object> iteratorNew = measures.iterator(); iteratorNew.hasNext();) {
            SimpleDateFormat format=  new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            JSONObject newObj = (JSONObject)iteratorNew.next();
            String expectSolveDate = (String) newObj.get("expect_solve_date");
            try {
                newObj.put("expect_solve_date", format.format(format.parse(expectSolveDate)).substring(0,10)+" "+formatter.format(new Date()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *功能描述 处理临时措施阶段时分秒
     * @author cds
     * @date 2022/5/19
     * @param
     * @return
     */

    public static void measuresExecute(JSONArray measuresExecute){

        for (Iterator iteratorNew = measuresExecute.iterator(); iteratorNew.hasNext();) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            JSONObject newObj = (JSONObject)iteratorNew.next();
            String expectSolveDate = (String) newObj.get("expect_solve_date");
            try {
                newObj.put("expect_solve_date", format.format(format.parse(expectSolveDate)).substring(0,10)+" "+formatter.format(new Date()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     *功能描述 处理措施验证阶段时分秒
     * @author cds
     * @date 2022/5/19
     * @param
     * @return
     */
    public static void measuresExecuteVerify(JSONArray measuresExecuteVerify,JSONArray dBVerify){
        //将临时措施验证数据库中的时间中的时分秒赋值
        for (Iterator iteratorNew = measuresExecuteVerify.iterator(); iteratorNew.hasNext(); ) {
            //前端传的措施验证详情
            JSONObject newObj = (JSONObject) iteratorNew.next();
            for (Iterator iteratorDao = dBVerify.iterator(); iteratorDao.hasNext(); ) {
                //数据库中的临时措施验证详情
                JSONObject dao = (JSONObject) iteratorDao.next();
                if (dao.get("uuid").equals(newObj.get("uuid"))) {
                    newObj.put("expect_solve_date", dao.get("expect_solve_date"));
                    break;
                }
            }
        }

    }


    /**
     * 为traceEntity中预计完成时间字段赋值赋值
     *
     * @param entity  traceEntity
     * @param dataDetail  详细信息
     * @param  solveStep  具体的步骤编号
     */
    public static void assignValueForExpectCompleteTime(QuestionActionTraceEntity entity, JSONObject dataDetail, String solveStep){
        JSONArray planArrange = dataDetail.getJSONArray("plan_arrange");
        for(Iterator<Object> iterator = planArrange.iterator(); iterator.hasNext();){
            JSONObject obj = (JSONObject) iterator.next();
            if (solveStep.equals(obj.getString("step_no"))){
                try {
                    entity.setExpectCompleteDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(obj.getString("expect_solve_date")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }



    /**
     * 为一般解决方案的traceEntity中预计完成时间字段赋值赋值
     *
     * @param entity  traceEntity
     * @param dataDetail  详细信息
     * @param  solveStep  具体的步骤编号
     */
    public static void assignValueForCommonExpectCompleteTime(QuestionActionTraceEntity entity, JSONObject dataDetail,String solveStep){
        JSONObject distributeInfo = dataDetail.getJSONArray("question_distribute_info").getJSONObject(0);
        JSONArray distributeDetail = distributeInfo.getJSONArray("question_distribute_detail");
        for(Iterator<Object> iterator = distributeDetail.iterator(); iterator.hasNext();){
            JSONObject obj = (JSONObject) iterator.next();
            if (solveStep.equals(obj.getString("step_id"))){
                try {
                    entity.setExpectCompleteDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(obj.getString("expect_complete_date")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

}
