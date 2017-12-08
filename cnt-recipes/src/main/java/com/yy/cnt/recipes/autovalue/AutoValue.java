package com.yy.cnt.recipes.autovalue;

import com.yy.cnt.recipes.autovalue.valueparser.ValueParser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AutoValue {

    String propertyKey();

    String controlCenterService() default "controlCenterService";
    
    Class<? extends ValueParser> valueParser() default ValueParser.class;
    
    String defaultValue() default "";
}
