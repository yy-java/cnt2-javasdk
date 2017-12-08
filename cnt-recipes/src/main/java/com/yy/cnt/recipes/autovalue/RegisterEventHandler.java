package com.yy.cnt.recipes.autovalue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RegisterEventHandler {

    String propertyKey();

    String controlCenterService() default "controlCenterService";

    boolean initializing() default false;

}
