package org.jfrog.maven.annomojo.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
    String value();
}
