package com.wangtao.dbhelper.builder;

import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.mapping.ResultMap;
import com.wangtao.dbhelper.mapping.ResultMapping;

import java.util.List;
import java.util.Objects;

/**
 * @author wangtao
 * Created at 2019/1/20 13:14
 */
public class MapperBuilderAssistant extends BaseBuilder {

    private String currentNamespace;

    private String resource;

    public MapperBuilderAssistant(Configuration configuration, String resource) {
        super(configuration);
        this.resource = resource;
    }

    public void setCurrentNamespace(String currentNamespace) {
        if(currentNamespace == null) {
            throw new BuilderException(resource + " Mapper文件必须要一个名称空间");
        }
        if(this.currentNamespace != null && !Objects.equals(this.currentNamespace, currentNamespace)) {
            throw new BuilderException("期待名称空间的是" + this.currentNamespace + ", 实际上是" + currentNamespace);
        }
        this.currentNamespace = currentNamespace;
    }

    /**
     * 申请名称空间, 未当前Mapper所有的id加上名称空间
     * @param base Mapper文件定义的id
     * @return namespace + "." + id
     */
    public String applyNamespace(String base) {
        if(base.contains(".")) {
            throw new BuilderException("定义的id名字不能包括点符号, 点符号代表着名称空间.");
        }
        if(base.startsWith(this.currentNamespace + ".")) {
            return base;
        }
        return currentNamespace + "." + base;
    }

    public void addResultMap(String id, Class<?> type, List<ResultMapping> resultMappings) {
        id = applyNamespace(id);
        ResultMap resutMap = new ResultMap.Builder(id, type).resultMappings(resultMappings).build();
        configuration.addResultMap(resutMap);
    }

    public String getResource() {
        return resource;
    }

    public String getCurrentNamespace() {
        return currentNamespace;
    }
}
