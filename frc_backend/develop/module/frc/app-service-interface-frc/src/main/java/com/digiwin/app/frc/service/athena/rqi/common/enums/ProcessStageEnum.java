package com.digiwin.app.frc.service.athena.rqi.common.enums;

/**
 * @Author: xieps
 * @Date: 2022/1/3 23:51
 * @Version 1.0
 * @Description  问题处理状态枚举类
 */
public enum  ProcessStageEnum {

    /**
     *  问题处理状态
     *  0.处理中;
     *  1.退回处理;
     *  2.终止结案;
     *  3.正常结案
     *
     */

    PROCESSING("0","处理中"),

    RETURNPROCESSING("1","退回处理"),

    TERMINATE("2","终止结案"),

    NORMALFINISH("3","正常结案");

    private final String code;
    private final String message;


    ProcessStageEnum(String code , String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


}
