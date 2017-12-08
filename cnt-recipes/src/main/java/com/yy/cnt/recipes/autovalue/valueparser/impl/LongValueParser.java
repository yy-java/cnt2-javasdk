package com.yy.cnt.recipes.autovalue.valueparser.impl;

import com.yy.cnt.recipes.autovalue.valueparser.ValueParser;
import com.yy.cnt.recipes.autovalue.valueparser.exception.NotChangeException;

public class LongValueParser implements ValueParser<Long> {

    private final Long defaultValue = 0l;

    @Override
    public Long parse(String data, Long old) throws NotChangeException {
        try {
            return Long.parseLong(data);
        } catch (Exception e) {
            return defaultValue;
        }
    }

}
