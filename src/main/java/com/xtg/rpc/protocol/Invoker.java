package com.xtg.rpc.protocol;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class Invoker implements Serializable {

    private String requestId;
    private String service;
    private String method;
    private MessageType messageType;

    private Object[] requestParams;
    private Object responseResult;

    public static String newRequestId(){
        String reqId = String.valueOf(Thread.currentThread().getId());
        reqId += String.valueOf(System.currentTimeMillis());
        return reqId;
    }

}
