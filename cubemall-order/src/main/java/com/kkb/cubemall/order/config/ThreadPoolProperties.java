package com.kkb.cubemall.order.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName ThreadPoolProperties
 * @Description
 * @Author 
 * @Date 2021/5/26 17:14
 * @Version V1.0
 **/
@ConfigurationProperties(prefix = "cubemall.thread")
@Data
@ToString
public class ThreadPoolProperties {
    private Integer coreSize;
    private Integer maxSize;
    private Integer keepAliveTime;

}

