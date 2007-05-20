package org.jfrog.maven.annomojo.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Determines how Maven will execute this mojo in the context of a
 * multimodule build. If a mojo is marked as an aggregator, it will only
 * execute once, regardless of the number of project instances in the
 * current build. Mojos that are marked as aggregators should use the
 * ${reactorProjects} expression to retrieve a list of the project
 * instances in the current build. If the mojo is not marked as an
 * aggregator, it will be executed once for each project instance in the
 * current build.
 */
@MojoAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface MojoAggregator {
    String value();
}
