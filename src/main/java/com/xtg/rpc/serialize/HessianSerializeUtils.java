package com.xtg.rpc.serialize;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerializeUtils {

    public static byte[] serialize(Object obj) {
        if(obj == null){
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HessianOutput hessianOutput = new HessianOutput(os);
        try {
            hessianOutput.writeObject(obj);
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
            return hessianInput.readObject();
        }catch (Exception e){
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        String[] strings = new String[]{"hello", "java", "world"};
        byte[] bytes = serialize(strings);
        Object obj = deserialize(bytes);
        System.out.println(obj);
    }

}
