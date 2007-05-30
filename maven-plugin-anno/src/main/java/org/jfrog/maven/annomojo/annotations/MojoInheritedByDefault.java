package org.jfrog.maven.annomojo.annotations;

import java.lang.annotation.*;

/**
 * Tells Maven that the this plugin's configuration should be inherted from
 * a parent POM by default. The default is true in Maven.
 * If the user specifies <inherit>false</inherit> in the plugin configuration
 * it will ovveride this annotation value.
 */
@MojoAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface MojoInheritedByDefault {
    /**
     * The default is true for annotation readability.
     * NOTE: Adding this annotation will not change the default
     * maven behavior that sets this to true.
     *
     * @return false if inheritance should be stop.
     */
    boolean value() default true;
}
