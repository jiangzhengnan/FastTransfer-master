package com.guohui.fasttransfer.utils.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Dikaros on 2016/5/16.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonListParam {
    String name() default "";

    Class contentClassName();

    JsonListParam.TYPE classType() default JsonListParam.TYPE.LIST;

    public static enum TYPE {
        ARRAY,
        LIST;

        private TYPE() {
        }
    }
}