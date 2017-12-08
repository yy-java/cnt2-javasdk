package com.yy.cn2.test.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.yy.cnt.recipes.autovalue.spring.AutoValueBeanPostProcessor;
import com.yy.cnt2.Cnt2Service;

/**
 * @author xlg
 * @since 2017/7/13
 */

@Configuration
public class DemoConfiguration {

    public static class Config {
        private String appName;
        private String profile;
        private String localFilePath;
        private String etcdEndpoints;
        private Integer etcdLeaseTtl;

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getProfile() {
            return profile;
        }

        public void setProfile(String profile) {
            this.profile = profile;
        }

        public String getLocalFilePath() {
            return localFilePath;
        }

        public void setLocalFilePath(String localFilePath) {
            this.localFilePath = localFilePath;
        }

        public String getEtcdEndpoints() {
            return etcdEndpoints;
        }

        public void setEtcdEndpoints(String etcdEndpoints) {
            this.etcdEndpoints = etcdEndpoints;
        }

        public Integer getEtcdLeaseTtl() {
            return etcdLeaseTtl;
        }

        public void setEtcdLeaseTtl(Integer etcdLeaseTtl) {
            this.etcdLeaseTtl = etcdLeaseTtl;
        }

    }

    @Bean
    @ConfigurationProperties(prefix = "configCenter")
    public Config config() {
        return new Config();
    }

    @Bean(name = "cnt2Service", destroyMethod = "close")
    public Cnt2Service configCenterService(Config config) {
        return new Cnt2Service(config.getAppName(), config.getProfile(), config.getLocalFilePath(), config.getEtcdEndpoints(), config.getEtcdLeaseTtl());
    }

    /**
     *@AutoValue和@RegisterEventHandler注解处理器,不使用注解关联配置中心属性时不需要实例化此bean
     * 
     */
    @Bean
    public AutoValueBeanPostProcessor autoValueProcessor() {
        return new AutoValueBeanPostProcessor();
    }

}
