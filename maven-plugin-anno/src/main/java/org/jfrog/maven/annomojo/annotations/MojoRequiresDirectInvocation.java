package org.jfrog.maven.annomojo.annotations;

import java.lang.annotation.*;

/**
 * Tells Maven that this mojo can ONLY be invoked directly, via the
 * command line. The default in maven is false.
 */
@MojoAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface MojoRequiresDirectInvocation {
    /**
     * The default is true for annotation readability.
     *
     * @return true if the mojo goal can ONLY be activated from command line.
     */
    boolean value() default true;
}
