package com.yy.cnt.recipes.autovalue.valueparser.impl;

import com.yy.cnt.recipes.autovalue.valueparser.ValueParser;
import com.yy.cnt.recipes.autovalue.valueparser.exception.NotChangeException;

public class DoubleValueParser implements ValueParser<Double> {

    private final Double defaultValue = 0.0d;

    @Override
    public Double parse(String data, Double old) throws NotChangeException {
        try {
            return Double.parseDouble(data);
        } catch (Exception e) {
            return defaultValue;
        }
    }

}
