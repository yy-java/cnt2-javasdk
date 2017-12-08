package com.yy.cnt.recipes.autovalue.valueparser;

import com.yy.cnt.recipes.autovalue.valueparser.exception.NotChangeException;

public interface ValueParser<T> {

    /**
     * data pass to target object , if throws NotChangeException,value of the target bean's field wold not change !
     * @param data
     * @return
     * @throws NotChangeException 
     */
    T parse(String data, T old) throws NotChangeException;
}
