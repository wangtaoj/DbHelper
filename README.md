### 简介

为学习MyBatis源码而写的操纵数据库的简易项目, 支持Mybatis常用功能，使用方式与MyBatis保持一致.

需要使用JDK  1.8及其以上版本.

### 特征

1. 支持数据库的增删改查, 会自动封装参数以及结果.
2. 支持插入数据时返回自增主键.
3. 支持复杂映射, 通过点操作符导航.
4. 支持动态SQL. 如if、foreach、trim、where、set、choose when otherwise.
5. 支持类型别名.

### 配置

目前可支持Mybatis以下属性.

* mapUnderscoreToCamelCase

* jdbcTypeForNull

* callSettersOnNulls

* returnInstanceForEmptyRow

* useActualParamName


### 待续

1. 未实现Mybaits的注解方式执行SQL.
2. 没有对查询结果进行缓存, 即Mybatis的本地缓存以及二级缓存未实现.
3. 未实现一对多, 一对一的关系查询.
4. 未实现Mybatis的插件体系.
5. 未实现存储过程调用.
6. 未实现接口绑定.

### 参考

MyBatis 3.5源码

