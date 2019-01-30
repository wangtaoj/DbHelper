package com.wangtao.dbhelper.reflection.property;

import java.util.Iterator;

/**
 * 属性分析器
 * 1. 通过.属性名来访问属性或者字段
 *    1) user 表示访问当前对象的user属性
 *    2) user.name 表示访问当前对象的user属性的name属性
 * 2. map可以通过[key]来访问属性或者字段
 *    1) [user] 表示访问当前对象的user属性, 当前对象是map
 *    2) user[name] 表示当前对象的user属性中的name属性, user是map
 * 3. 通过索引arr[1]来访问数组或者集合.
 * @author wangtao
 * Created at 2019/1/27 16:01
 */
public class PropertyTokenizer implements Iterator<PropertyTokenizer> {

    /*
     * 当前对象bean, 有一个属性map, map对象存了user对象, user对象有个name属性
     * map[user].name
     * 其中:
     * name = map
     * index = user
     * indexName = map[user]
     * children = name
     */

    /**
     * 属性名字
     */
    private String name;

    /**
     * 索引名字, []之间的内容
     */
    private String index;

    /**
     * 属性名字加索引名字
     */
    private String indexName;

    /**
     * 剩下的内容, 以点分割
     */
    private String children;

    private String fullname;

    public PropertyTokenizer(String fullname) {
        this.fullname = fullname;
        int delim = fullname.indexOf('.');
        if(delim > -1) {
            name = fullname.substring(0, delim);
            children = fullname.substring(delim + 1);
            if(children.isEmpty()) {
                throw new IllegalArgumentException("表达式'" + fullname + "'请不要以点结尾.");
            }
        } else {
            name = fullname;
            children = null;
        }
        indexName = name;
        // 检查[]
        int left = name.indexOf('[');
        int right = name.indexOf(']');
        if(left > -1 && right > left) {
            index = name.substring(left + 1, right);
            name = name.substring(0, left);
        } else if(left > -1) {
            throw new IllegalArgumentException("解析" + fullname + "出现错误, 请检查访问属性的数组语法格式.");
        }
    }

    public String getName() {
        return name;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getIndex() {
        return index;
    }

    public String getFullname() {
        return fullname;
    }

    public String getChildren() {
        return children;
    }

    @Override
    public boolean hasNext() {
        return children != null;
    }

    @Override
    public PropertyTokenizer next() {
        return new PropertyTokenizer(children);
    }
}
