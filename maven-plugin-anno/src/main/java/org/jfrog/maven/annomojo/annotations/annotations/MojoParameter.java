package org.jfrog.maven.annomojo.annotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * . User: yoavl
 */
@MojoAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
@Inherited
public @interface MojoParameter {
    String description() default "";

    String property() default "";

    boolean required() default false;

    boolean readonly() default false;

    String deprecated() default "";

    String alias() default "";

    String expression() default "";

    String defaultValue() default "";
}
