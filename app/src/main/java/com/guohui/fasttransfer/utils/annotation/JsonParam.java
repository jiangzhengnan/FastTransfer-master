package com.guohui.fasttransfer.utils.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Dikaros on 2016/5/16.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonParam {
    String name() default "";
}
