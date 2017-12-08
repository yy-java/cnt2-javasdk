package com.yy.cnt.recipes.autovalue.valueparser.impl;

import com.yy.cnt.recipes.autovalue.valueparser.ValueParser;
import com.yy.cnt.recipes.autovalue.valueparser.exception.NotChangeException;

public class IntegerValueParser implements ValueParser<Integer> {

    private final Integer defaultValue = 0;

    @Override
    public Integer parse(String data, Integer old) throws NotChangeException {
        try {
            return Integer.parseInt(data);
        } catch (Exception e) {
            return defaultValue;
        }
    }

}
