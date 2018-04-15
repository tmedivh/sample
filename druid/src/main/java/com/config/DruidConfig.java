package com.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Druid的DataResource配置类
 * 凡是被Spring管理的类，实现接口 EnvironmentAware 重写方法 setEnvironment 可以在工程启动时，
 * 获取到系统环境变量和application配置文件中的变量。 还有一种方式是采用注解的方式获取 @value("${变量的key值}")
 * 获取application配置文件中的变量。 这里采用第一种要方便些
 */

@Configuration
@EnableTransactionManagement
public class DruidConfig implements EnvironmentAware {
    private RelaxedPropertyResolver propertyResolver;

    @Override
    public void setEnvironment(Environment env) {
        this.propertyResolver = new RelaxedPropertyResolver(env, "spring.datasource.");
    }

    @Bean
    public DataSource dataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(propertyResolver.getProperty("url"));
        datasource.setDriverClassName(propertyResolver.getProperty("driver-class-name"));
        datasource.setUsername(propertyResolver.getProperty("username"));
        datasource.setPassword(propertyResolver.getProperty("password"));
        datasource.setMinIdle(Integer.valueOf(propertyResolver.getProperty("minIdle")));
        datasource.setMaxWait(Long.valueOf(propertyResolver.getProperty("maxWait")));
        datasource.setMaxActive(Integer.valueOf(propertyResolver.getProperty("maxActive")));
        datasource.setMinEvictableIdleTimeMillis(
                Long.parseLong(propertyResolver.getProperty("druid.minEvictableIdleTimeMillis")));
        try {
            datasource.setFilters(propertyResolver.getProperty("druid.filters"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datasource;
    }
}
