package com.digiwin.app.frc.service.athena.qdh.biz;

import java.util.List;
import java.util.Map;

/**
 * @ClassName IamEocBiz
 * @Description TODO
 * @Author author
 * @Date 2020/9/16 15:24
 * @Version 1.0
 **/
public interface IamEocBiz {
    /**
     * 获取所有部门
     * @return
     */
    List<Map<String,Object>> getDepartments() throws Exception;

    /**
     * 获取所有人
     * @return
     */
    List<Map<String,Object>> getUsers() throws Exception;

    /**
     * 获取职能
     * @return
     * @throws Exception
     */
    List<Map<String,Object>> getDuty() throws Exception;

    /**
     * 根据职能id，查询职能下的所有人员
     * @param dutyId
     * @return
     * @throws Exception
     */
    List<Map<String,Object>> getUsersByDutyId(String dutyId) throws Exception;

    /**
     * 获取员工的直属主管
     * @param userId
     * @return
     */
    String getEmpDirector(String userId) throws Exception;

    /**
     * 獲取員工userId
     * @param id
     * @return
     * @throws Exception
     */
    Map<String,Object>getEmpUserId(String id) throws Exception;

    /**
     * 根据部门id获取部门主管信息
     * @param deptId
     * @return
     */
    String getDeptDirector(String deptId) throws Exception;

    /**
     * 获取员工的所有上级部门
     * @param userId
     * @return
     * @throws Exception
     */
    List<String> getDeptUsers(String userId) throws Exception;

    /**
     * 根据用户id获取员工id
     * @param userId
     * @return
     * @throws Exception
     */
    String getEmpIdByUserId(String userId) throws Exception;


}
