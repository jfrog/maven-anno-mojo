package org.jfrog.maven.annomojo.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify the version when the Mojo was added to the API. Similar to Javadoc since.
 */
@MojoAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface MojoSince {
    String value();
}
