package com.digiwin.app.frc.service.athena.app.mapper;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
@Mapper
public interface QuestionCardMapper {

    /**
     * APP取任务卡列表数据
     * @param param
     * @return
     */
    public List<JSONObject> queryQuestionList(JSONObject param);

    /**
     * 获取需要产生消息的数据
     * @param param
     * @return
     */
    public List<JSONObject> queryQuestionNewsList(JSONObject param);

    @Select("Select DATE_FORMAT(startDate,'%Y-%m-%d %H:%i:%s') from frc_task where type= #{type} and status = 0")
    public String getStartDate(@Param("type") int type);

    @Update("update frc_task set startDate = #{startDate} where type= #{type} and status = 0")
    public int updateStartDate(@Param("startDate") String startDate,@Param("type") int type);
}
