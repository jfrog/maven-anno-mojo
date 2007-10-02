package org.apache.maven.tools.plugin.extractor.annomojo.annotations;

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
    /**
     * The phase to execute in the parallel lifecycle
     */
    String phase() default "";

    /**
     * Ensure that this other mojo within the same plugin executes before
     * this one. It's restricted to this plugin to avoid creating inter-plugin
     * dependencies.
     */
    String goal() default "";

    /**
     * This is optionally used in conjunction with the phase element,
     * and specifies a custom life-cycle overlay that should be added to the
     * cloned life cycle before the specified phase is executed. This is
     * useful to inject specialized behavior in cases where the main life
     * cycle should remain unchanged.
     */
    String lifecycle() default "";
}
