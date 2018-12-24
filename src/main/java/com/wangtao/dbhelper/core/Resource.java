package com.wangtao.dbhelper.core;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

/**
 * Created by wangtao at 2018/12/24 15:58
 */
public class Resource {

    private Resource() {

    }

    /**
     * 读取资源
     * @param resource 资源名字
     * @return 字符流
     */
    public static Reader getResourceAsReader(String resource) throws IOException {
        URL url = Resource.class.getClassLoader().getResource(resource);
        if(url != null) {
            return new FileReader(url.getPath());
        }
        throw new IOException("不能读取该资源: " + resource);
    }
}
