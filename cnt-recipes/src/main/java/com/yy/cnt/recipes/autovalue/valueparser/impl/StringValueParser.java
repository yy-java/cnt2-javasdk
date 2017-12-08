package com.yy.cnt.recipes.autovalue.valueparser.impl;

import com.yy.cnt.recipes.autovalue.valueparser.ValueParser;
import com.yy.cnt.recipes.autovalue.valueparser.exception.NotChangeException;

public class StringValueParser implements ValueParser<String> {

    @Override
    public String parse(String data,String old)  throws NotChangeException {
        return data;
    }

}
