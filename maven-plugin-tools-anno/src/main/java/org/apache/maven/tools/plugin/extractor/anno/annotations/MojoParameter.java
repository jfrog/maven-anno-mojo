package org.apache.maven.tools.plugin.extractor.anno.annotations;

import java.lang.annotation.*;

/**
 * Created by IntelliJ IDEA.
 * User: yoavl
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
