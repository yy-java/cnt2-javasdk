package com.yy.cnt.recipes.autovalue.valueparser.impl;

import com.yy.cnt.recipes.autovalue.valueparser.ValueParser;
import com.yy.cnt.recipes.autovalue.valueparser.exception.NotChangeException;

public class BooleanValueParser implements ValueParser<Boolean> {

    private final Boolean defaultValue = false;

    @Override
    public Boolean parse(String data, Boolean old) throws NotChangeException {
        try {
            if ("1".equals(data) || "true".equalsIgnoreCase(data)) {
                return true;
            }
        } catch (Exception e) {

        }
        return defaultValue;
    }

}
