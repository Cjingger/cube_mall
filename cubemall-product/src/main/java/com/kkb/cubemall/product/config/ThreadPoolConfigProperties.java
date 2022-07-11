package com.kkb.cubemall.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName ThreadPoolConfigProperties
 * @Description
 * @Version V1.0
 **/
@Component
@ConfigurationProperties(prefix = "cubemall.thread")
@Data
public class ThreadPoolConfigProperties {

    private Integer corePoolSize;

    private Integer maxPoolSize;

    private Integer keepAliveTime;


}

