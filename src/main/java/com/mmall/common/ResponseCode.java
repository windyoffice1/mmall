package com.mmall.common;

public enum ResponseCode {

    SUCCESS(0,"success"),

    ERROR(1,"error"),

    NEED_LOGIN(10,"needLogin"),

    ILLEGAL_ARGUMENT(3,"ILLEGAL_ARGUMENT");

    private final int code;
    private final String des;

    ResponseCode(int code,String des){
        this.code=code;
        this.des=des;
    }

    public int getCode(){
       return this.code;
    }

    public  String getDes(){
        return this.des;
    }
}
