package com.hqwx.bigdata.client.common.lambda;

import java.io.*;

/**
 * @Description: SerializedLambda
 * @Author: XuShengBin
 * @Date: 2022-09-08
 * @Ver: v1.0 -create
 */
public class SerializedLambda implements Serializable {
    private static final long serialVersionUID = 8025925345765570181L;
    private Class<?> capturingClass;
    private String functionalInterfaceClass;
    private String functionalInterfaceMethodName;
    private String functionalInterfaceMethodSignature;
    private String implClass;
    private String implMethodName;
    private String implMethodSignature;
    private int implMethodKind;
    private String instantiatedMethodType;
    private Object[] capturedArgs;

    public SerializedLambda() {
    }

    public static SerializedLambda extract(Serializable serializable) {
        Object lambda;
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try{
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(serializable);
            oos.flush();

            ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())) {
                @Override
                protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                    Class<?> clazz = super.resolveClass(desc);
                    return clazz == java.lang.invoke.SerializedLambda.class ? SerializedLambda.class : clazz;
                }
            };
            lambda = ois.readObject();

            baos.flush();
            return (SerializedLambda)lambda;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public String getInstantiatedMethodType() {
        return this.instantiatedMethodType;
    }

    public Class<?> getCapturingClass() {
        return this.capturingClass;
    }

    public String getImplMethodName() {
        return this.implMethodName;
    }
}
