/*
 * Copyright (C) 2010 JFrog Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
