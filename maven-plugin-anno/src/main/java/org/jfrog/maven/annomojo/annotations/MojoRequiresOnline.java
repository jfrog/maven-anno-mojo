package org.jfrog.maven.annomojo.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Some mojos cannot execute if they don't have access to a network
 * connection. If Maven is operating in offline mode, such mojos will
 * cause the build to fail. This flag controls whether the mojo requires
 * Maven to be online.
 */
@MojoAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface MojoRequiresOnline {
}
