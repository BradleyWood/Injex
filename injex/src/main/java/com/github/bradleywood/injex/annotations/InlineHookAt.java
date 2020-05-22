package com.github.bradleywood.injex.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface InlineHookAt {
    String value();
    String desc() default "()V";
    int line() default 0;
}
