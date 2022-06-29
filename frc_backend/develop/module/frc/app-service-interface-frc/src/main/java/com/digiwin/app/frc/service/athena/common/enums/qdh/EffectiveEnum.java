package com.digiwin.app.frc.service.athena.common.enums.qdh;

/**
 * @Author: xieps
 * @Date: 2021/11/9 13:40
 * @Version 1.0
 * @Description
 */
public enum EffectiveEnum {

        /**
         * 是否生效
         *
         * "Y"-生效
         * "V"-失效
         */
        EFFECTIVE("Y","生效"),

        INVALID("V","失效");

        private final String code;
        private final String message;


        EffectiveEnum(String code , String message) {
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
