package com.wangtao.dbhelper.datasource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Created by wangtao at 2018/12/20 16:59
 */
public class SimpleDataSource implements DataSource {

    /**
     * 记录注册过的驱动
     */
    private static Map<String, Class<?>> registerDrivers = new ConcurrentHashMap<>();

    private Properties driverProperties;
    private String driver;
    private String username;
    private String password;
    private String url;

    private Boolean autoCommit;
    private Integer transactionIsolation;

    static {
        /* 获取所有加载过的驱动 */
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            registerDrivers.put(driver.getClass().getName(), driver.getClass());
        }
    }

    public SimpleDataSource(String driver, String url, String username, String password) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public SimpleDataSource(Properties properties) {
        this.driver = properties.getProperty("driver");
        this.url = properties.getProperty("url");
        this.username = properties.getProperty("username");
        this.password = properties.getProperty("password");
        this.driverProperties = properties;
        String autoCommit = driverProperties.getProperty("autoCommit");
        if(autoCommit != null) {
            this.autoCommit = Boolean.valueOf(autoCommit);
        }
        String transactionIsolation = driverProperties.getProperty("transactionIsolation");
        if(transactionIsolation != null) {
            this.transactionIsolation = Integer.valueOf(transactionIsolation);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return doGetConnection(username, password);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return doGetConnection(username, password);
    }

    private Connection doGetConnection(String username, String password) throws SQLException {
        initDriver();
        Properties driverInfo = new Properties();
        if(driverProperties != null) {
            driverInfo.putAll(driverProperties);
        }
        if(username != null) {
            driverInfo.setProperty("user", username);
        }
        if(password != null) {
            driverInfo.setProperty("password", password);
        }
        Connection connection = DriverManager.getConnection(url, driverInfo);
        configureConnection(connection);
        return connection;
    }

    /**
     * 注册驱动
     */
    private synchronized void initDriver() throws SQLException {
        if (!registerDrivers.containsKey(driver)) {
            try {
                Class<?> driverClass = Class.forName(driver);
                registerDrivers.put(driver, driverClass);
            } catch (ClassNotFoundException e) {
                throw new SQLException("错误的加载驱动类. 原因: " + e);
            }
        }
    }

    /**
     * 配置connection自动提交、隔离级别.
     */
    public void configureConnection(Connection connection) throws SQLException {
        if (autoCommit != null) {
            connection.setAutoCommit(autoCommit);
        }
        if (transactionIsolation != null) {
            connection.setTransactionIsolation(transactionIsolation);
        }
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(Boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public Integer getTransactionIsolation() {
        return transactionIsolation;
    }

    public void setTransactionIsolation(Integer transactionIsolation) {
        this.transactionIsolation = transactionIsolation;
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
    public Logger getParentLogger() {
        return null;
    }
}
