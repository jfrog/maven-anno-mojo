package org.jfrog.maven.annomojo.annotations;

import java.lang.annotation.*;

/**
 * The name for the Mojo that users will reference from the command line
 * to execute the Mojo directly, or inside a POM in order to provide
 * Mojo-specific configuration.
 */
@MojoAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface MojoGoal {
    /**
     * The goal name that can be used directly from maven command line,
     * or in POM files with plugin execution, or in lifecycle declaration.
     *
     * @return the goal name
     */
    String value();
}
