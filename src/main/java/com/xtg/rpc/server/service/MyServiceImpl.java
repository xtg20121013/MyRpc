package com.xtg.rpc.server.service;

public class MyServiceImpl implements MyService{

    @Override
    public String hello(String name, Integer count) {
        StringBuffer sb = new StringBuffer("hello ");
        if(name != null ){
            sb.append(name);
        }
        if(count != null){
            for (int i = 1; i < count; i++) {
                sb.append(",");
                sb.append(name);
            }
        }
        return sb.toString();
    }
}
