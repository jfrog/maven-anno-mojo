package org.apache.maven.tools.plugin.extractor.anno.annotations;

import java.lang.annotation.*;

/**
 *
 * . User: yoavl
 */
@MojoAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
@Inherited
public @interface MojoComponent {
    String description() default "";
    String role() default "";
    String roleHint() default "";
}
