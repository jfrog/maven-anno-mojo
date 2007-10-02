package org.apache.maven.tools.plugin.extractor.annomojo.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds this Mojo to a particular phase of the standard build lifecycle, if specified.
 * NOTE: This is only required if this Mojo is to participate in the standard build process.
 */
@MojoAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface MojoPhase {
    /**
     * @return The phase name to bind this mojo
     */
    String value();
}
