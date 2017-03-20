package com.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yujintao on 16/9/28.
 */


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Factory {
   Class type();
   String id();
}
