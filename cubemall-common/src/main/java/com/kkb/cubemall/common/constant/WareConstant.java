package com.kkb.cubemall.common.constant;

public class WareConstant {
    /**
     * 采购单状态枚举
     */
    public enum PurchaseStatusEnum{
        CREATED(0, "新建"),
        ASSIGNED(1, "已分配"),
        BUYING(2, "正在采购"),
        RECEIVE(3, "已采购"),
        FINISH(4, "已完成"),
        HASERROR(5, "采购完成");

        private int code;
        private String msg;

        PurchaseStatusEnum(int code, String msg){
            this.code = code;
            this.msg = msg;
        };

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    };

    /**
     * 采购需求项枚举
     */
    public enum PurchaseDetailStatusEnum{
        CREATED(0, "新建"),
        ASSIGNED(1, "已分配"),
        BUYING(2, "正在采购"),
        FINISH(3, "已完成"),
        HASERROR(4, "采购完成");

        private int code;
        private String msg;


        PurchaseDetailStatusEnum(int code, String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
