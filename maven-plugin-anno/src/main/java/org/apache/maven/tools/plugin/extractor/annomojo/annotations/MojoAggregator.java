package org.apache.maven.tools.plugin.extractor.annomojo.annotations;

import java.lang.annotation.*;

/**
 * Determines how Maven will execute this mojo in the context of a
 * multimodule build. If a mojo is marked as an aggregator, it will only
 * execute once, regardless of the number of project instances in the
 * current build. Mojos that are marked as aggregators should use the
 * ${reactorProjects} expression to retrieve a list of the project
 * instances in the current build.
 * If the mojo is not marked as an aggregator (this is the defaut value
 * false in maven), it will be executed once for each project instance in the
 * current build.
 */
@MojoAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface MojoAggregator {
    /**
     * The default is true for annotation readability.
     *
     * @return true if mojo needs to be executed only once per maven execution.
     *         false and mojo will be executed for all reactor projects.
     */
    boolean value() default true;
}
