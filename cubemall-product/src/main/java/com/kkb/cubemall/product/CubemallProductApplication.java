package com.kkb.cubemall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.alibaba.nacos")
@ServletComponentScan
@EnableDiscoveryClient
@EnableFeignClients
public class CubemallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(CubemallProductApplication.class, args);
    }

}