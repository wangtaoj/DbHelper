package com.wangtao.dbhelper.parser;

import com.wangtao.dbhelper.core.Resources;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by wangtao at 2019/1/5 15:12
 */
public class DtdEntityResolver implements EntityResolver {

    private static final String DEFAULT_CONFIG_POSITION = "com/wangtao/dbhelper/builder/xml/mybatis-3-config.dtd";
    private static final String DEFAULT_MAPPER_POSITION = "com/wangtao/dbhelper/builder/xml/mybatis-3-mapper.dtd";

    public DtdEntityResolver() {

    }

    /**
     * 将读取网络位置的文件转向读取本地.
     * 返回null, 将采取默认行为.
     * @param publicId "PUBLIC" 后的字符串
     * @param systemId publicId后的字符串, 代表文件的真实位置.
     * @return InputSource对象
     * @throws SAXException 任何错误发生时抛出
     */
    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
        try {
            if(systemId != null) {
                if(systemId.contains("mybatis-3-config.dtd")) {
                    return getInputSource(publicId, systemId, DEFAULT_CONFIG_POSITION);
                } else if(systemId.contains("mybatis-3-mapper.dtd")) {
                    return getInputSource(publicId, systemId, DEFAULT_MAPPER_POSITION);
                }
            }
            return null;
        } catch (Exception e) {
            throw new SAXException(e.getMessage());
        }
    }

    private InputSource getInputSource(String publicId, String systemId, String position) {
        InputSource source = null;
        try {
            Reader reader = Resources.getResourceAsReader(position);
            source = new InputSource(reader);
            source.setPublicId(publicId);
            source.setSystemId(systemId);
        } catch (IOException e) {
            // just do nothing
        }
        return source;
    }
}
