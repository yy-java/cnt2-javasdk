package com.yy.cnt.recipes.autovalue.holder;

import java.lang.reflect.Field;

import com.google.common.base.Objects;
import com.yy.cnt.recipes.autovalue.valueparser.ValueParser;
import com.yy.cnt.recipes.autovalue.valueparser.exception.NotChangeException;

/**
 * 保存所有注解相关的bean和对应的field
 * 
 * @author tanghengde
 *
 */
public class BeanPropertyHolder {
    private final Object bean;
    private final Field field;
    private final String defaultValue;
    private final ValueParser valueParser;

    private final String controlCenterServiceBeanName;

    public BeanPropertyHolder(Object bean, Field field, ValueParser valueParser, String controlCenterServiceBeanName,
            String defaultValue) {
        this.bean = bean;
        this.field = field;
        this.valueParser = valueParser;
        this.controlCenterServiceBeanName = controlCenterServiceBeanName;
        this.defaultValue = defaultValue;
    }

    public Object getBean() {
        return this.bean;
    }

    public Field getField() {
        return this.field;
    }

    public String getControlCenterServiceBeanName() {
        return controlCenterServiceBeanName;
    }

    @SuppressWarnings("unchecked")
    public void setFieldValue(String value) throws IllegalArgumentException, IllegalAccessException {
        Object old = field.get(bean);
        if (null != valueParser) {
            Object t = null;
            boolean updateValue = true;
            try {
                t = valueParser.parse(value, old);
            } catch (NotChangeException e) {
                updateValue = false;
            }
            if (updateValue) {
                field.set(bean, t);
            }
        } else {
            field.set(bean, value);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.bean, this.field, this.controlCenterServiceBeanName, this.defaultValue);
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof BeanPropertyHolder) {
            BeanPropertyHolder that = (BeanPropertyHolder) object;
            return Objects.equal(this.bean, that.bean) && Objects.equal(this.field, that.field)
                    && Objects.equal(this.controlCenterServiceBeanName, that.controlCenterServiceBeanName);
        }
        return false;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("bean", this.bean).add("field", this.field)
                .add("controlCenterServiceBeanName", this.controlCenterServiceBeanName)
                .add("defaultValue", this.defaultValue).toString();
    }

}
