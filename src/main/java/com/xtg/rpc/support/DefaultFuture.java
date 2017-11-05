package com.xtg.rpc.support;

import io.netty.channel.Channel;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@ToString
public class DefaultFuture implements ResponseFuture {

    private static final Map<String, Channel> CHANNELS   = new ConcurrentHashMap<>();
    private static final Map<String, DefaultFuture> FUTURES   = new ConcurrentHashMap<>();

    private final String requestId;
    private final Lock lock = new ReentrantLock();
    private final Condition done = lock.newCondition();
    private volatile boolean isDone = false;
    private volatile Object result;

    public DefaultFuture(String requestId, Channel channel){
        this.requestId = requestId;
        CHANNELS.put(requestId, channel);
        FUTURES.put(requestId, this);
    }

    @Override
    public Object get() {
        if(isDone()){
            return result;
        }else{
            lock.lock();
            try {
                while (!isDone()){
                    log.info("【服务代理】阻塞客户线程,等待RPC调用requestId={}的结果返回", requestId);
                    done.await();
                }
            }catch (Exception e){
                log.warn("get interrupt, ", e);
            }
            finally {
                lock.unlock();
            }
            if(isDone()){
                return result;
            }else {
                throw new RuntimeException("can't get result");
            }
        }
    }

    @Override
    public void setResult(Object result) {
        this.isDone = true;
        this.result = result;
    }

    @Override
    public boolean isDone() {
        return this.isDone;
    }

    private void doReceived(Object result){
        lock.lock();
        try {
            this.setResult(result);
            done.signal();
        }finally {
            lock.unlock();
        }
    }

    public static void received(String requestId, Object result){
        if(FUTURES.containsKey(requestId)){
            log.info("【处理响应】根据requestId={}找到对应future,set返回值,并唤醒阻塞的客户线程", requestId);
            FUTURES.get(requestId).doReceived(result);
        }
    }
}
