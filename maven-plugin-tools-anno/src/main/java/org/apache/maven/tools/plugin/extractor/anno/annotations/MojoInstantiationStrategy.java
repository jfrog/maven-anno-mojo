package org.apache.maven.tools.plugin.extractor.anno.annotations;

import java.lang.annotation.*;

/**
 * Created by IntelliJ IDEA.
 * User: yoavl
 */
@MojoAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface MojoInstantiationStrategy {
    String value();
}
