package com.yy.cnt.recipes.autovalue.spring;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;

import com.yy.cnt.api.IControlCenterService;
import com.yy.cnt.api.event.ConfigEventHandler;
import com.yy.cnt.api.event.EventType;
import com.yy.cnt.recipes.autovalue.AutoValue;
import com.yy.cnt.recipes.autovalue.RegisterEventHandler;
import com.yy.cnt.recipes.autovalue.holder.BeanPropertyHolder;
import com.yy.cnt.recipes.autovalue.holder.ConfigRegister;
import com.yy.cnt.recipes.autovalue.valueparser.ValueParser;
import com.yy.cnt.recipes.autovalue.valueparser.ValueParserRegister;

public class AutoValueBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
        implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(AutoValueBeanPostProcessor.class);

    private ApplicationContext applicationContext = null;

    /**
     * 用来保存bean
     */
    private Map<String, IControlCenterService> controlCenterServiceBeans = new ConcurrentHashMap<>();

    private Map<Class<? extends ValueParser>, ValueParser> customValueParsers = new ConcurrentHashMap<>();

    /**
     * 绑定事件处理
     */
    private ConfigRegister processor = new ConfigRegister();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean postProcessAfterInstantiation(final Object bean, final String beanName) throws BeansException {
        if (log.isDebugEnabled()) {
            log.debug("Setting Reloadable Properties on [{}]", beanName);
        }
        setPropertiesOnBean(bean);
        registerMethodHandler(bean);
        return true;
    }

    private void registerMethodHandler(final Object bean) {
        ReflectionUtils.doWithMethods(bean.getClass(), new ReflectionUtils.MethodCallback() {

            @Override
            public void doWith(final Method method) throws IllegalArgumentException, IllegalAccessException {
                final RegisterEventHandler annotation = method.getAnnotation(RegisterEventHandler.class);
                if (null == annotation) {
                    return;
                }

                String propertyKey = annotation.propertyKey();
                String controlCenterServiceBeanName = annotation.controlCenterService();

                if (StringUtils.isBlank(propertyKey)) {
                    throw new BeanInitializationException(
                            "not propertyKey[val=" + propertyKey + "] set for RegisterEventHandler on method ["
                                    + method.getName() + "] for [" + bean.getClass().getName() + "] !");
                }

                if (StringUtils.isBlank(controlCenterServiceBeanName)) {
                    throw new BeanInitializationException("not propertyKey[val=" + controlCenterServiceBeanName
                            + "] set for RegisterEventHandler on method [" + method.getName() + "] for ["
                            + bean.getClass().getName() + "] !");
                }

                IControlCenterService cc = getControlCenterService(bean, controlCenterServiceBeanName);
                if (validateMethodEventHandler(method)) {
                    ReflectionUtils.makeAccessible(method);
                    cc.registerEventHandler(propertyKey, new ConfigEventHandler() {

                        @Override
                        public void handle(String key, EventType event, String value) {
                            if (event == EventType.PUT) {
                                ReflectionUtils.invokeMethod(method, bean, key, value);
                            } else if (event == EventType.DELETE) {
                                ReflectionUtils.invokeMethod(method, bean, key, null);
                            }
                        }
                    });
                    if (annotation.initializing()) {
                        // get&set default value
                        String value = getPropertyValue(cc, propertyKey);
                        try {
                            ReflectionUtils.invokeMethod(method, bean, propertyKey, value);
                        } catch (Exception e) {
                            throw new BeanInitializationException("Error to set defaultValue for propertyKey[val="
                                    + propertyKey + "]  using HandlerMethod [" + method.getName() + "] for ["
                                    + bean.getClass().getName() + "] !", e);
                        }
                    }
                } else {
                    throw new BeanInitializationException("Error parameter types [val=" + controlCenterServiceBeanName
                            + "] set for RegisterEventHandler on method [" + method.getName() + "] for ["
                            + bean.getClass().getName() + "] !");
                }
            }
        });
    }

    private boolean validateMethodEventHandler(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 2) {
            return false;
        }
        if (!parameterTypes[0].isAssignableFrom(String.class)) {
            return false;
        }
        if (!parameterTypes[1].isAssignableFrom(String.class)) {
            return false;
        }
        return true;

    }

    private void setPropertiesOnBean(final Object bean) {
        ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {

            @Override
            public void doWith(final Field field) throws IllegalArgumentException, IllegalAccessException {

                final AutoValue annotation = field.getAnnotation(AutoValue.class);
                if (null == annotation) {
                    return;
                }

                ReflectionUtils.makeAccessible(field);
                validateFieldNotFinal(bean, field);

                String propertyKey = annotation.propertyKey();
                String controlCenterServiceBeanName = annotation.controlCenterService();

                if (StringUtils.isBlank(propertyKey)) {
                    throw new BeanInitializationException(
                            "not propertyKey[val=" + propertyKey + "] set for AutoValue on field [" + field.getName()
                                    + "] for [" + bean.getClass().getName() + "] !");
                }

                if (StringUtils.isBlank(controlCenterServiceBeanName)) {
                    throw new BeanInitializationException(
                            "not propertyKey[val=" + controlCenterServiceBeanName + "] set for AutoValue on field ["
                                    + field.getName() + "] for [" + bean.getClass().getName() + "] !");
                }

                setBeanValueAndMonitor(bean, field, propertyKey, controlCenterServiceBeanName, annotation);

            }
        });
    }

    private ValueParser getValueParser(Object bean, Field field, AutoValue autovalue) {
        Class<? extends ValueParser> parser = autovalue.valueParser();
        if (null == parser) {
            return null;
        }
        if (parser == ValueParser.class) {
            return ValueParserRegister.getParser(field.getType());
        }
        try {

            if (customValueParsers.containsKey(parser)) {
                return customValueParsers.get(parser);
            }

            Constructor<? extends ValueParser> m = parser.getDeclaredConstructor();
            ReflectionUtils.makeAccessible(m);
            ValueParser instance = m.newInstance();
            customValueParsers.put(parser, instance);
            return instance;

        } catch (Exception e) {
            throw new BeanInitializationException("Initializing ValueParser[" + parser.getName()
                    + "] for AutoValue on field [" + field.getName() + "] for [" + bean.getClass().getName() + "] !",
                    e);
        }
    }

    private void setBeanValueAndMonitor(Object bean, Field field, String propertyKey,
            String controlCenterServiceBeanName, AutoValue autovalue) {
        IControlCenterService cc = getControlCenterService(bean, controlCenterServiceBeanName);
        ValueParser<?> valueParser = getValueParser(bean, field, autovalue);
        BeanPropertyHolder holder = new BeanPropertyHolder(bean, field, valueParser, controlCenterServiceBeanName,
                autovalue.defaultValue());
        // register watcher
        processor.register(propertyKey, cc, holder);

        // get&set default value
        String defautlValue = getPropertyValue(cc, propertyKey);
        if (null == defautlValue && !"".equals(autovalue.defaultValue())) {
            defautlValue = autovalue.defaultValue();
        }
        if (null != defautlValue) {
            try {
                holder.setFieldValue(defautlValue);
            } catch (Exception e) {
                throw new BeanInitializationException(
                        "Error to set defaultValue for propertyKey[val=" + controlCenterServiceBeanName
                                + "]  on field [" + field.getName() + "] for [" + bean.getClass().getName() + "] !",
                        e);
            }
        }
    }

    private String getPropertyValue(IControlCenterService controlCenterService, String propertyKey) {
        return controlCenterService.getValue(propertyKey);
    }

    private IControlCenterService getControlCenterService(Object bean, String beanName) {
        if (controlCenterServiceBeans.containsKey(beanName)) {
            return controlCenterServiceBeans.get(beanName);
        }
        IControlCenterService cc = applicationContext.getBean(beanName, IControlCenterService.class);
        if (null == cc) {
            throw new BeanInitializationException(
                    "cannot found bean of ControlCenterService using name " + beanName + "!" + beanName + "] !");
        }
        controlCenterServiceBeans.put(beanName, cc);
        return cc;
    }

    private void validateFieldNotFinal(final Object bean, final Field field) {
        if (Modifier.isFinal(field.getModifiers())) {
            throw new BeanInitializationException(
                    String.format("Unable to set field [%s] of class [%s] as is declared final", field.getName(),
                            bean.getClass().getCanonicalName()));
        }
    }

}
