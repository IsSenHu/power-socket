package com.cdsen.powersocket;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author HuSen
 */
@EnableDubbo
@SpringBootApplication
public class PowerSocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(PowerSocketApplication.class, args);
    }

}
