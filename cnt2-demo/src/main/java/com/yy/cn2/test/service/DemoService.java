package com.yy.cn2.test.service;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yy.cnt2.Cnt2Service;
import com.yy.cnt2.event.AbstractEventHandler;

/**
 * @author xlg
 * @since 2017/7/13
 */
@Service
public class DemoService {
    // 配置中心的对应配置项
    private static final String MY_CONFIG_KEY = "my_config_1";

    @Autowired
    private Cnt2Service cnt2Service;

    // 本地属性
    private int myConfig1;

    public int getMyConfig1() {
        return myConfig1;
    }

    public void setMyConfig1(int myConfig1) {
        this.myConfig1 = myConfig1;
    }

    @PostConstruct
    public void init() {
        // 初始化时从本地文件加载配配置
        try {
            setMyConfig1(Integer.parseInt(cnt2Service.getValue(MY_CONFIG_KEY, "-1")));
        } catch (Exception e) {
            setMyConfig1(-1);
        }
        // 注册监听配置修改事件处理器
        cnt2Service.registerEventHandler(new AbstractEventHandler() {
            @Override
            public String getKey() {
                return MY_CONFIG_KEY;
            }

            @Override
            // 修改配置事件
            public void handlePutEvent(String key, String value) {
                System.out.println("Inner Key : " + key + " Value :" + value);
                // 跟新Bean属性
                setMyConfig1(Integer.parseInt(value));
            }

            // 删除配置事件
            public void handleDeleteEvent(String key, String value) {
                System.out.println("Inner Key : " + key + " Value :" + value);
                // 跟新Bean属性为默认值
                setMyConfig1(-1);
            }
        });

        // 测试配置下发是否生效
//        Thread monitor = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                while (true) {
//                    System.out.println("myConfig1 :" + myConfig1);
//                    try {
//                        TimeUnit.SECONDS.sleep(1);
//                    } catch (InterruptedException e) {
//                        return;
//                    }
//                }
//            }
//
//        });
//        monitor.setDaemon(true);
//        monitor.start();
    }

}
