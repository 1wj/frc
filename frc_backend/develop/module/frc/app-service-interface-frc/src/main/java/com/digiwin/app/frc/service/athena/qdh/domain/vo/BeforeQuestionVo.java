package com.digiwin.app.frc.service.athena.qdh.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName BeforeQuestionVo
 * @Description TODO
 * @Author author
 * @Date 2021/11/21 21:28
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeforeQuestionVo {
    private String oid;
    private Long tenantsid;
    private String questionSolveStep;
    private String processStatus;
    private String processResult;
    /**
     * 退回标识id 识别退回(qir) 问题解决退回(qsr) 验收退回(qar) 问题关闭退回(sc002005r) 遏制审核退回(sc002004r)
     */
    private String returnFlagId;

    /**
     * 退回标识name 识别退回(qir) 问题解决退回(qsr) 验收退回(qar) 问题关闭退回(sc002005r) 遏制审核退回(sc002004r)
     */
    private String returnFlagName;

    private String returnNo;
    private String liablePersonId;
    private String liablePersonName;
    private Date actualCompleteDate;
    private Date expectCompleteDate;
    private String dataContent;

    private int principalStep;


}
