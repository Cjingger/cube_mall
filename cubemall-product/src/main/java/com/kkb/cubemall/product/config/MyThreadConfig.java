package com.kkb.cubemall.product.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName MyThreadConfig
 * @Description
 * @Author hubin
 * @Date 2021/5/16 20:15
 * @Version V1.0
 **/
@Configuration
public class MyThreadConfig {

    @Autowired
    ThreadPoolConfigProperties threadPoolConfigProperties;

    @Bean
    public ThreadPoolExecutor getThreadPool(){

        // 创建线程池对象
        ThreadPoolExecutor executor = new ThreadPoolExecutor(threadPoolConfigProperties.getCorePoolSize(),
                threadPoolConfigProperties.getMaxPoolSize(),
                threadPoolConfigProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

        return executor;

    }

}

