package com.xtg.rpc.server.service;

import java.util.Arrays;

public class MyServiceImpl implements MyService{

    @Override
    public String hello(String name, Integer count) {
        StringBuffer sb = new StringBuffer("hello ");
        if(name != null){
            sb.append(name);
        }
        if(count != null){
            sb.append(",");
            sb.append(count);
        }
        return sb.toString();
    }
}
