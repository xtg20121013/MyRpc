package com.xtg.rpc.protocol;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class Header implements Serializable{

    private Long length;
    private MessageType type;
    private String requestId;
    private String service;
    private String method;

}
