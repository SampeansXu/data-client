package com.hqwx.bigdata.client.common.utils;

import java.io.*;

/**
 * @Description: SerializationUtil
 * @Author: XuShengBin
 * @Date: 2022-09-08
 * @Ver: v1.0 -create
 */
public class SerializationUtil {
    public SerializationUtil() {
    }

    public static <T extends Serializable> T clone(final T object) {
        if (object == null) {
            return null;
        } else {
            byte[] objectData = serialize(object);
            return (T) deserialize(objectData);
        }
    }

    public static byte[] serialize(Object object) {
        if (object == null) {
            return null;
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);

            try {
                ObjectOutputStream oos = new ObjectOutputStream(baos);

                try {
                    oos.writeObject(object);
                    oos.flush();
                } catch (Throwable var6) {
                    try {
                        oos.close();
                    } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                    }

                    throw var6;
                }

                oos.close();
            } catch (IOException var7) {
                throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass(), var7);
            }

            return baos.toByteArray();
        }
    }

    public static Object deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        } else {
            try {
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));

                Object var2;
                try {
                    var2 = ois.readObject();
                } catch (Throwable var5) {
                    try {
                        ois.close();
                    } catch (Throwable var4) {
                        var5.addSuppressed(var4);
                    }

                    throw var5;
                }

                ois.close();
                return var2;
            } catch (IOException var6) {
                throw new IllegalArgumentException("Failed to deserialize object", var6);
            } catch (ClassNotFoundException var7) {
                throw new IllegalStateException("Failed to deserialize object type", var7);
            }
        }
    }
}
