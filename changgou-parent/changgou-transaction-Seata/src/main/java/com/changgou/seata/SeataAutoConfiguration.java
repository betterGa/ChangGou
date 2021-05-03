package com.changgou.seata;

import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import io.seata.spring.annotation.GlobalTransactionScanner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

public class SeataAutoConfiguration {

    /***
     * 创建代理数据库
     * 会将undo_log绑定到本地事务中
     * @param environment 环境配置
     * @return 代理数据源
     *
     * @Primary作用：如果一个接口有多个实现类，如果没有指定用那个，那么可以使用@Primary来指定一个默认值
     */
    @Primary
    @Bean
    public DataSourceProxy dataSource(Environment environment) {
        //创建数据源对象
        HikariDataSource dataSource = new HikariDataSource();
        //获取数据源链接地址
        dataSource.setJdbcUrl(environment.getProperty("spring.datasource.url"));
        //设置数据库驱动
        dataSource.setDriverClassName(environment.getProperty("spring.datasource.driver-class-name"));
        //获取数据库名字
        dataSource.setUsername(environment.getProperty("spring.datasource.username"));
        //获取数据库密码
        dataSource.setPassword(environment.getProperty("spring.datasource.password"));
        //将数据库封装成一个代理数据库
        return new DataSourceProxy(dataSource);
    }

    /***
     * 全局事务扫描器
     * 用来解析带有@GlobalTransactional注解的方法，然后采用AOP的机制控制事务
     * @param environment 环境配置
     * @return 全局事务注解扫描
     */
    @Bean
    public GlobalTransactionScanner globalTransactionScanner(Environment environment) {
        //事务分组名称
        String applicationName = environment.getProperty("spring.application.name");
        String groupName = environment.getProperty("seata.group.name");
        if (applicationName == null) {
            return new GlobalTransactionScanner(groupName == null ? "my_test_tx_group" : groupName);
        } else {
            return new GlobalTransactionScanner(applicationName, groupName == null ? "my_test_tx_group" : groupName);
        }
    }
}
