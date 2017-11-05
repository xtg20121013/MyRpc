package com.xtg.rpc.serialize;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
public class HessianSerializeUtils {

    public static byte[] serialize(Object obj) {
        if(obj == null){
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HessianOutput hessianOutput = new HessianOutput(os);
        try {
            hessianOutput.writeObject(obj);
            log.info("【序列化】通过hessian将message序列化为byteArray,{}", obj);
            return os.toByteArray();
        }catch (Exception e){
            return null;
        }
    }

    public static Object deserialize(byte[] bytes){
        if(bytes == null){
            return null;
        }
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        HessianInput hessianInput = new HessianInput(is);
        try {
            Object obj = hessianInput.readObject();
            log.info("【反序列化】通过hessian将byteArray反序列化为{}", obj);
            return obj;
        }catch (Exception e){
            return null;
        }
    }

}
