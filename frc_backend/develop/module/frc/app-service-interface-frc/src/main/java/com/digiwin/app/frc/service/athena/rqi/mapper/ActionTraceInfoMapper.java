package com.digiwin.app.frc.service.athena.rqi.mapper;

import com.digiwin.app.frc.service.athena.rqi.domain.entity.QuestionTrackProcessorEntity;
import com.digiwin.app.frc.service.athena.rqi.domain.entity.QuestionTrackProposerEntity;
import com.digiwin.app.frc.service.athena.rqi.domain.entity.QuestionTrackResponsibleEntity;
import com.digiwin.app.frc.service.athena.rqi.domain.model.QuestionTrackProcessorModel;
import com.digiwin.app.frc.service.athena.rqi.domain.model.QuestionTrackProposerModel;
import com.digiwin.app.frc.service.athena.rqi.domain.model.QuestionTrackResponsibleModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: xieps
 * @Date: 2021/12/31 14:39
 * @Version 1.0
 * @Description
 */
@Mapper
public interface ActionTraceInfoMapper {

    /**
     * 获取任务提出者问题追踪数据
     *
     * @param proposerModel 问题追踪提出人信息
     * @param oids
     * @param tenantSid 租户id
     * @return List<QuestionTrackProposerEntity>
     */
    List<QuestionTrackProposerEntity> queryQuestionTrackProposerInfo(@Param("oid") List<String> oids, @Param("model") QuestionTrackProposerModel proposerModel,
                                                                     @Param("tenantSid") Long tenantSid);

    /**
     * 根据反馈开始和结束时间 查询出该范围内的问题单号 并进行分组
     *
     * @param feedbackStartDate 反馈开始时间
     * @param feedbackEndDate   反馈结束时间
     * @param proposerId        追踪者id
     * @param tenantSid         租户id
     * @return List<String>
     */
    List<Map<String, Object>> queryQuestionNoByProposerId(@Param("startTime") String feedbackStartDate, @Param("endTime") String feedbackEndDate,
                                                          @Param("proposerId") String proposerId, @Param("questionNo") String questionNo, @Param("tenantSid") Long tenantSid);


    /**
     * 根据问题单号查询 该单号对应的最后一笔数据
     *
     * @param questionNos 问题单号集合
     * @param tenantSid   租户id
     * @return List<String>
     */
    List<QuestionTrackResponsibleEntity> queryCompleteOidByNos(@Param("questionNos") List<String> questionNos,
                                                               @Param("questionDescription") String questionDescription,
                                                               @Param("questionProcessStage") String questionProcessStage,
                                                               @Param("liablePersonId") String liablePersonId,
                                                               @Param("tenantSid") Long tenantSid);


    /**
     * 根据问题单号查询 待处理的数据
     *
     * @param questionNos 问题单号集合
     * @param tenantSid   租户id
     * @return List<String>
     */
    List<QuestionTrackResponsibleEntity> queryUnderwayOidByNos(@Param("questionNos") List<String> questionNos,
                                                               @Param("questionDescription") String questionDescription,
                                                               @Param("questionProcessStage") String questionProcessStage,
                                                               @Param("liablePersonId") String liablePersonId,
                                                               @Param("tenantSid") Long tenantSid);


    /**
     * 根据时间范围和处理者名称查询出问题单号和反馈发起时间
     *
     * @param feedbackStartDate 反馈开始时间
     * @param feedbackEndDate   反馈结束时间
     * @param responsiblePersonId   登录人id
     * @param tenantSid         租户id
     * @return List<Map < String, Object>>
     */
    List<Map<String, Object>> queryQuestionNoByTime(@Param("startTime") String feedbackStartDate,
                                                    @Param("endTime") String feedbackEndDate, @Param("responsiblePersonId") String responsiblePersonId,
                                                    @Param("tenantSid") Long tenantSid,@Param("questionNo") String questionNo);


    /**
     * 获取任务处理者问题追踪数据
     *
     * @param oids
     * @param processorModel
     * @param tenantSid
     * @param processorId
     * @return
     */
    List<QuestionTrackProcessorEntity> queryQuestionTrackProcessorInfo(@Param("oid") List<String> oids, @Param("model") QuestionTrackProcessorModel processorModel,
                                                                       @Param("tenantSid") Long tenantSid,@Param("processorId") String processorId);

    /**
     * 根据处理人姓名查询问题单号
     *
     * @param processorName 处理人名称
     * @param feedbackIds
     * @param questionNo
     * @param tenantSid     租户id
     * @return List<String>
     */
    List<Map<String, Object>> queryQuestionNoByProcessorId(@Param("processorId") String processorName, @Param("startTime") String feedbackStartDate,
                                                           @Param("endTime") String feedbackEndDate,
                                                           @Param("feedbackIds") List<String> feedbackIds, @Param("questionNo") String questionNo, @Param("tenantSid") Long tenantSid);


    /**
     * 根据当责者的id查询问题单号 并进行分组
     *
     * @param userId    当责者id
     * @param tenantSid 租户id
     * @return List<String>
     */
    List<String> queryQuestionNosByResponsibleId(@Param("responsibleId") String userId, @Param("tenantSid") Long tenantSid);


    /**
     * 获取当责者问题追踪数据
     *
     * @param commonOids
     * @param responsibleModel
     * @param tenantSid
     * @return
     */
    List<QuestionTrackResponsibleEntity> queryQuestionTrackResponsibleInfo(@Param("oid") List<String> commonOids,
                                                                           @Param("model") QuestionTrackResponsibleModel responsibleModel,
                                                                           @Param("tenantSid") Long tenantSid);

    /**
     * 根据前端传值问题单号进行模糊查询
     *
     * @param questionNo 问题单号
     * @param tenantSid  租户id
     * @return List<String>
     */
    List<String> vagueQueryQuestionNos(@Param("questionNo") String questionNo, @Param("tenantSid") Long tenantSid);


    /**
     * 通过最后一笔数据主键和处理人信息 id name
     *
     * @param questionNos
     * @param tenantSid   租户id
     * @return
     */
    List<Map<String, Object>> queryOidAndLiableInfoByNos(@Param("questionNos") List<String> questionNos, @Param("tenantSid") Long tenantSid);

    /**
     * 通过待处理状态和问题编号获取主键和处理人信息 id name
     * @param questionNos
     * @param tenantSid
     * @return
     */
    List<Map<String, Object>> queryOidAndLiableInfoByNos2(@Param("questionNos") List<String> questionNos, @Param("tenantSid") Long tenantSid);

    /**
     * 迭代六：短期结案验收的特殊情况处理查询
     * @param questionNos2
     * @param tenantSid
     * @return
     */
    List<QuestionTrackResponsibleEntity> queryShotTermByQuestionNo(@Param("questionNos") Set<String> questionNos2, @Param("tenantSid") Long tenantSid);

}
