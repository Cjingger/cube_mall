package com.kkb.cubemall.seckill.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;

public class DataSourceConfig {

    @ConfigurationProperties("spring.datasource.local")
    public DataSource masterDataSource() {return DataSourceBuilder.create().build();}

}
