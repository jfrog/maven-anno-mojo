package org.apache.maven.tools.plugin.extractor.annomojo.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Flags this Mojo as requiring the dependencies in the specified scope
 * (or an implied scope) to be resolved before it can execute.
 * NOTE: Currently supports compile, runtime, and test scopes.
 */
@MojoAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface MojoRequiresDependencyResolution {
    String value() default "runtime";
}
