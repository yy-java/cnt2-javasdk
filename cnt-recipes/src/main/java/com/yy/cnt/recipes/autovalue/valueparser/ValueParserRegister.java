package com.yy.cnt.recipes.autovalue.valueparser;

import com.yy.cnt.recipes.autovalue.valueparser.impl.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ValueParserRegister {

    private static final Map<Class<?>, ValueParser<?>> parsers = new ConcurrentHashMap<>();

    static {
        register(Integer.class, new IntegerValueParser());
        register(int.class, parsers.get(Integer.class));

        register(Long.class, new LongValueParser());
        register(long.class, parsers.get(Long.class));

        register(Double.class, new DoubleValueParser());
        register(double.class, parsers.get(Double.class));

        register(Boolean.class, new BooleanValueParser());
        register(boolean.class, parsers.get(Boolean.class));

        register(String.class, new StringValueParser());
    }

    public static ValueParser<?> getParser(Class<?> type) {
        return parsers.get(type);
    }

    public static void register(Class<?> type, ValueParser<?> parser) throws IllegalArgumentException {
        if (null == type || null == parser) {
            throw new IllegalArgumentException("type or parser must not be null");
        }
        parsers.put(type, parser);
    }
}
