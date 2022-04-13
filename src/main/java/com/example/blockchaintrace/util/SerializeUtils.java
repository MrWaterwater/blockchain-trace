package com.example.blockchaintrace.util;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class SerializeUtils {
    private static KryoUtils kryoUtils = new KryoUtils();
    /**
     * 反序列化
     * @param bytes 对象对应的字节数组
     */
    public static Object deserialize(byte[] bytes){
        Input input = new Input(bytes);
        kryoUtils.setReferences(true);
        Object object = kryoUtils.readClassAndObject(input);
        input.close();
        return object;
    }
    /**
     * 序列化
     * @param object 序列化对象
     */
    public static byte[] serialize(Object object){
        Output output = new Output(4096,-1);
        kryoUtils.setReferences(true);
        kryoUtils.writeClassAndObject(output, object);
        byte[] bytes = output.toBytes();
        output.close();
        return bytes;
    }
}
