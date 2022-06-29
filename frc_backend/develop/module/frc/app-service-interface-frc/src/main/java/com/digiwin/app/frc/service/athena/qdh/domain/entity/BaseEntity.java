package com.digiwin.app.frc.service.athena.qdh.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName baseEntity
 * @Description 基础公共类(创建时间,创建人,修改时间,修改人)
 * @Author author
 * @Date 2021/11/10 08:40
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户
     */
    private Long tenantsid;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人id
     */
    private String createId;

    /**
     * 创建人
     */
    private String createName;

    /**
     * 修改人id
     */
    private String updateId;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 修改人
     */
    private String updateName;
}
