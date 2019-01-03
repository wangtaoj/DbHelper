package com.wangtao.dbhelper.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by wangtao at 2018/12/24 15:58
 */
public class Resources {

    private Resources() {

    }

    /**
     * 读取资源
     * @param resource 资源名字
     * @return 字符流
     */
    public static Reader getResourceAsReader(String resource) throws IOException {
        return new InputStreamReader(getResourceAsStream(resource));
    }

    /**
     * 读取资源
     * @param resource 资源名字
     * @return 字节流
     */
    public static InputStream getResourceAsStream(String resource) throws IOException {
        InputStream inputStream = Resources.class.getClassLoader().getResourceAsStream(resource);
        if(inputStream == null) {
            throw new IOException("不能读取该资源: " + resource);
        }
        return inputStream;
    }

    /**
     * 根据完全限定名获取Class
     * @param className 完全限定名
     * @return 对应的Class
     */
    public static Class<?> classForName(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }
}
