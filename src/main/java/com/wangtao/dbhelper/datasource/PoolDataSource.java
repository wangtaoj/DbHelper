package com.wangtao.dbhelper.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Properties;

/**
 * Created by wangtao at 2018/12/24 16:45
 */
public class PoolDataSource implements DataSource {

    private static Logger logger = LoggerFactory.getLogger(PoolDataSource.class);

    private SimpleDataSource dataSource;

    /**
     * 最大连接数量
     */
    private int maxSize = 20;

    /**
     * 是否已初始化
     **/
    private boolean flag;

    /**
     * 初始数量
     */
    private int initSize = 5;

    /**
     * 最大空闲数量
     */
    private int maxIdleSize = 10;

    /**
     * 从池中获取连接的最大等待时间, 默认10秒
     */
    private int maxWaitTime = 10000;

    /**
     * 保存空闲连接
     */
    private LinkedList<PoolConnection> idleConnections = new LinkedList<>();

    /**
     * 活动连接数量
     **/
    private int activeSize;

    public PoolDataSource() {

    }

    public PoolDataSource(String driver, String url, String username, String password) {
        this.dataSource = new SimpleDataSource(driver, url, username, password);
    }

    public PoolDataSource(Properties properties) {
        this.dataSource = new SimpleDataSource(properties);
    }

    /**
     * 返回一个PoolConnection对象
     * @param username 用户名
     * @param password 密码
     * @return connection
     */
    private PoolConnection wrapConnection(String username, String password) throws SQLException {
        Connection connection = dataSource.getConnection(username, password);
        if (logger.isDebugEnabled()) {
            logger.debug("connection{} 被创建", connection.hashCode());
        }
        return new PoolConnection(connection, this);
    }

    public synchronized void initConnection() {
        if (initSize > maxIdleSize) {
            throw new DataSourceException(String.format("初始连接数量(%d)大于最大空闲数量(%d)", initSize, maxIdleSize));
        }
        try {
            for (int i = 0; i < initSize; i++) {
                idleConnections.addFirst(wrapConnection(dataSource.getUsername(), dataSource.getPassword()));
            }
        } catch (SQLException e) {
            throw new DataSourceException("初始化连接失败. 原因:" + e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("已经初始化好{}个连接", initSize);
        }
    }

    public synchronized Connection popConnection(String username, String password) throws SQLException {
        PoolConnection connection = null;
        // 初始连接数量
        if (!flag) {
            initConnection();
            flag = true;
        }
        // 有空闲连接, 直接返回
        if (idleConnections.size() > 0) {
            activeSize++;
            connection = idleConnections.removeLast();
        } else if (activeSize + idleConnections.size() < maxSize) {
            // 没有达到最大连接, 创建新连接
            connection = wrapConnection(username, password);
            activeSize++;
        } else {
            long beforeTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - beforeTime < maxWaitTime) {
                if (idleConnections.size() > 0) {
                    activeSize++;
                    connection = idleConnections.removeLast();
                    break;
                }
            }
        }
        if (connection != null) {
            return connection.getProxyConnection();
        }
        throw new DataSourceException("获取连接失败, 已经达到最大等待时间, 没有空闲连接可用");
    }

    /**
     * 将连接放到连接池中
     * @param connection PoolConnection
     */
    public synchronized void pushConnection(PoolConnection connection) throws SQLException {
        if (idleConnections.size() < maxIdleSize) {
            idleConnections.addLast(connection);
            activeSize--;
            if (logger.isDebugEnabled()) {
                logger.debug("connection{}已被归还到连接池中", connection.getRealConnection().hashCode());
            }
        } else {
            Connection realConection = connection.getRealConnection();
            realConection.close();
            if (logger.isDebugEnabled()) {
                logger.debug("connection{}已经被释放", realConection.hashCode());
            }
        }
    }

    /**
     * 关闭所有连接
     */
    public synchronized void forceClose() throws SQLException{
        for(PoolConnection connection : idleConnections) {
            connection.getRealConnection().close();
        }
        idleConnections.clear();
        activeSize = 0;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getConnection(dataSource.getUsername(), dataSource.getPassword());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return popConnection(username, password);
    }

    public SimpleDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(SimpleDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getInitSize() {
        return initSize;
    }

    public void setInitSize(int initSize) {
        this.initSize = initSize;
    }

    public int getMaxIdleSize() {
        return maxIdleSize;
    }

    public void setMaxIdleSize(int maxIdleSize) {
        this.maxIdleSize = maxIdleSize;
    }

    public int getMaxWaitTime() {
        return maxWaitTime;
    }

    public void setMaxWaitTime(int maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    @Override
    public <T> T unwrap(Class<T> iface) {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) {

    }

    @Override
    public void setLoginTimeout(int seconds) {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public java.util.logging.Logger getParentLogger() {
        return null;
    }
}
