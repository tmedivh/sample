package com.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
@EnableTransactionManagement  // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
public class DruidConfig implements EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(environment.getProperty("spring.datasource.url"));
        datasource.setDriverClassName(environment.getProperty("spring.datasource.driverClassName"));
        datasource.setUsername(environment.getProperty("spring.datasource.username"));
        datasource.setPassword(environment.getProperty("spring.datasource.password"));
        datasource.setMinIdle(Integer.parseInt(environment.getProperty("spring.datasource.minIdle")));
        datasource.setMaxWait(Long.parseLong(environment.getProperty("spring.datasource.maxWait")));
        datasource.setMaxActive(Integer.valueOf(environment.getProperty("spring.datasource.maxActive")));
        datasource.setMinEvictableIdleTimeMillis(
                Long.parseLong(environment.getProperty("spring.datasource.druid.minEvictableIdleTimeMillis")));

        try {
            datasource.setFilters(environment.getProperty("spring.datasource.druid.filters"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return datasource;
    }
}
