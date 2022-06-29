package com.digiwin.app.frc.service.athena.qdh.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName QuestionPictureModel
 * @Description 问题详情之基础信息 图片
 *
 * @Author author
 * @Date 2021/11/11 17:38
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionPictureModel {
    @JsonProperty(value = "picture_id")
    private String pictureId;
}
