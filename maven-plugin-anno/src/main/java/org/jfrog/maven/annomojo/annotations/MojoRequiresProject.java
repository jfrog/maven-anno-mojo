package org.jfrog.maven.annomojo.annotations;

import java.lang.annotation.*;

/**
 * Tells Maven that a valid project instance must be present for this mojo to
 * execute.
 * The default in maven is true.
 */
@MojoAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface MojoRequiresProject {
    /**
     * The default is true for annotation readability.
     * NOTE: Adding this annotation will not change the default
     * maven behavior that sets this to true.
     *
     * @return false if the mojo can be excuted without a POM file
     */
    boolean value() default true;
}
