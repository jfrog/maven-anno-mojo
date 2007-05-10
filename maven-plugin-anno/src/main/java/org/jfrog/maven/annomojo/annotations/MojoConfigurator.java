package org.jfrog.maven.annomojo.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The configurator type to use when injecting parameter values into this Mojo.
 * The value is normally deduced from the Mojo's implementation language, but can
 * be specified to allow a custom ComponentConfigurator implementation to be used.
 * NOTE: This will only be used in very special cases, using a highly controlled vocabulary
 * of possible values. (Elements like this are why it's a good idea to use the descriptor tools.)
 */
@MojoAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface MojoConfigurator {
    String value();
}
