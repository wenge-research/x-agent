package com.wg.appframe.mybatisflex.annotate;

import com.wg.appframe.mybatisflex.enums.FieldFill;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface OnFieldFill {
    String value() default "";
    FieldFill fill() default FieldFill.DEFAULT;
    String mdcKey() default "" ;
    Class fieildClass() default String.class ;
}
