package com.digiwin.app.frc.service.athena.qdh.domain.model.init;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName InitModel
 * @Description TODO
 * @Author HeX
 * @Date 2022/2/11 18:42
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitModel {
    /**
     * 问题处理状态
     */
    private Integer questionProcessStatus;

    /**
     * 问题处理结果
     */
    private Integer questionProcessResult;

    /**
     * 处理人处理顺序
     */
    private Integer principalStep;

    /**
     * 步骤
     */
    private String questionProcessStep;


}
