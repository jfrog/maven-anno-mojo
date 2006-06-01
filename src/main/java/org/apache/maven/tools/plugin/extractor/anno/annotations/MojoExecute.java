package org.apache.maven.tools.plugin.extractor.anno.annotations;

import java.lang.annotation.*;

/**
 *
 * . User: yoavl
 */
@MojoAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface MojoExecute {
    String phase() default "";
    String goal() default "";
    String lifecycle() default "";
}
