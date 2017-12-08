package com.yy.cn2.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author xlg
 * @since 2017/7/13
 */
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {

        SpringApplication.run(DemoApplication.class, args);


        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
        }

    }
}
