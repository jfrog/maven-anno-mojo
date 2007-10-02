package org.apache.maven.tools.plugin.extractor.annomojo.annotations;

import java.lang.annotation.*;

/**
 * Some mojos cannot execute if they don't have access to a network
 * connection. If Maven is operating in offline mode, such mojos will
 * cause the build to fail. This flag controls whether the mojo requires
 * Maven to be online.
 * The default is false in MAven.
 */
@MojoAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface MojoRequiresOnline {
    /**
     * The default is true for easy flagging.
     *
     * @return true if the mojo goal fails in offline mode (mvn -o).
     */
    boolean value() default true;
}
