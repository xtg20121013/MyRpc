package com.xtg.rpc.support;

public interface ResponseFuture {

    Object get();

    void setResult(Object result);

    boolean isDone();

}
