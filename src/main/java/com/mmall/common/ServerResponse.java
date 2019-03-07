package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/***
 * 后台统一返回类
 * @param <T>
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//保证类对象在序列化成JSON时空对象不会产生对应的Key值
public class ServerResponse<T> implements Serializable {

    private int status;
    private String msg;
    private T data;

    private ServerResponse(int status){
        this.status=status;
    }

    private ServerResponse(int status, T data){
        this.status=status;
        this.data=data;
    }

    private ServerResponse(int status, String msg, T data){
        this.status=status;
        this.msg=msg;
        this.data=data;
    }

    private ServerResponse(int status, String msg){
        this.status=status;
        this.msg=msg;
    }

    @JsonIgnore
    //该属性不被序列化
    public boolean ifsuccess(){
        return this.status==ResponseCode.SUCCESS.getCode();
    }

    public int getStatus(){
        return status;
    }

    public T getData(){
        return data;
    }

    public String getMsg(){
        return  msg;
    }

    public  static <T> ServerResponse<T> createBySuccess(){
        return  new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    public  static <T> ServerResponse<T> createBySuccessMsg(String msg){
        return  new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }

    public  static <T> ServerResponse<T> createBySuccess(T data){
        return  new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }

    public  static <T> ServerResponse<T> createBySuccess(String msg, T data){
        return  new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }

    public  static <T> ServerResponse<T> createByError(){
        return  new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDes());
    }

    public  static <T> ServerResponse<T> createByErrorMsg(String  errormsg){
        return  new ServerResponse<T>(ResponseCode.ERROR.getCode(),errormsg);
    }

    public  static <T> ServerResponse<T> createByErrorCodeMsg(int errorcode, String  errormsg){
        return  new ServerResponse<T>(errorcode,errormsg);
    }


}
