package org.jfrog.maven.annomojo.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When this goal is invoked, it will first invoke a parallel lifecycle,
 * ending at the given phase. If a goal is provided instead of a phase,
 * that goal will be executed in isolation. The execution of either will
 * not affect the current project, but instead make available the ${executedProject}
 * expression if required. An alternate lifecycle can also be provided:
 * for more information see the documentation on the build lifecycle.
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
