package com.digiwin.app.frc.service.athena.util;

/**
 * 功能描述：自定义异常
 *
 * @Author: ch
 * @Date: 2022/3/24 11:52
 */
public class OperationException extends  Exception{
    private static final long serialVersionUID = 7701381124216166090L;

    public OperationException(String message) {
        super(message);
    }

}
