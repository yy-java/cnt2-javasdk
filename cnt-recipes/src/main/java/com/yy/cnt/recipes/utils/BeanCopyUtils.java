package com.yy.cnt.recipes.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;

public class BeanCopyUtils {

    public static void copyNoneNullProperties(Object dest, Object orig) throws IllegalAccessException,
            InvocationTargetException {
        PropertyDescriptor[] origDescriptors = getPropertyUtils().getPropertyDescriptors(orig);
        for (int i = 0; i < origDescriptors.length; i++) {
            String name = origDescriptors[i].getName();
            if ("class".equals(name)) {
                continue; // No point in trying to set an object's class
            }
            if (getPropertyUtils().isReadable(orig, name) && getPropertyUtils().isWriteable(dest, name)) {
                try {
                    Object value = getPropertyUtils().getSimpleProperty(orig, name);
                    if (null != value) {
                        copyProperty(dest, name, value);
                    }
                } catch (NoSuchMethodException e) {
                }
            }
        }
    }

    public static PropertyUtilsBean getPropertyUtils() {
        return BeanUtilsBean.getInstance().getPropertyUtils();
    }

    public static void copyProperty(Object dest, String name, Object value) throws IllegalAccessException,
            InvocationTargetException {
        BeanUtilsBean.getInstance().copyProperty(dest, name, value);
    }
}
