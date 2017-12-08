package com.yy.cn2.test.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yy.cnt.recipes.autovalue.AutoValue;
import com.yy.cnt.recipes.autovalue.RegisterEventHandler;
import com.yy.cnt2.Cnt2Service;

/**
 * @author xlg
 * @since 2017/7/13
 */
@Service
public class DemoServiceByAnnotation {

    // 配置中心的对应配置项
    private static final String MY_CONFIG_KEY = "my_config_2";

    @Autowired
    private Cnt2Service cnt2Service;

    /**
     *绑定bean属性跟配置中心的对应配置项，监听配置中心对应配置项的发布事件
     *简单类型（不需要对配置值做二次处理的）采用{@link AutoValue}}注解绑定，复杂类型采用{@link RegisterEventHandler}注解绑定
     */
    @AutoValue(propertyKey = MY_CONFIG_KEY, controlCenterService = "cnt2Service", defaultValue = "default")
    private String myConfig2;

    @PostConstruct
    public void init() {
        // // 测试配置下发是否生效
        // Thread monitor = new Thread(new Runnable() {
        //
        // @Override
        // public void run() {
        // while (true) {
        // System.out.println("myConfig2 :" + myConfig2);
        // try {
        // TimeUnit.SECONDS.sleep(1);
        // } catch (InterruptedException e) {
        // return;
        // }
        // }
        // }
        //
        // });
        // monitor.setDaemon(true);
        // monitor.start();
    }


    /**
     * 
     * 监听配置中心对应配置项的发布事件
     * 当需要对配置值进行二次处理，比如分割字符串或者json反系列化时采用{@link RegisterEventHandler}注解绑定
     * 
     * @param key 配置中心配置项名称
     * @param value 配置中心配置项对应的值，修改后的，如果是null，表示该配置项被删除
     */
    @RegisterEventHandler(propertyKey = MY_CONFIG_KEY, controlCenterService = "cnt2Service", initializing = true)
    public void handleUpdate(String key, String value) {
        System.out.println("RegisterEventHandler Key : " + key + " Value :" + value);
    }

}
