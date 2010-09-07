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
 * A parameter the plugin is expecting.
 */
@MojoAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
@Inherited
public @interface MojoParameter {
    /**
     * The description of this parameter's use inside the Mojo.
     * Using the toolset, this is detected as the Javadoc description
     * for the field.
     * NOTE: While this is not a required part of the parameter specification,
     * it SHOULD be provided to enable future tool support for browsing, etc.
     * and for clarity.
     * Every child classes inheriting this parameter via jar dependencies,
     * will not see the description if it is not written in this field but in
     * the javadoc.
     *
     * @return the textual description of the plugin-mojo parameter
     */
    String description() default "";

    String property() default "";

    /**
     * Whether this parameter is required for the Mojo to function. This is
     * used to validate the configuration for a Mojo before it is injected,
     * and before the Mojo is executed from some half-state.
     *
     * @return true if the parameter is mandatory
     */
    boolean required() default false;

    /**
     * Specifies that this parameter cannot be configured directly by the user
     * (as in the case of POM-specified configuration). This is useful when
     * you want to force the user to use common POM elements rather than
     * plugin configurations, as in the case where you want to use the
     * artifact's final name as a parameter. In this case, you want the user
     * to modify <build><finalName/></build> rather than specifying a value
     * for finalName directly in the plugin configuration section. It is also
     * useful to ensure that - for example - a List-typed parameter which
     * expects items of type Artifact doesn't get a List full of Strings.
     *
     * @return true if the parameter cannot be used in pom configuration
     */
    boolean readonly() default false;

    /**
     * Marks a parameter as deprecated. The rules on deprecation are the same
     * as normal Java with language elements. This will trigger a warning when
     * a user tries to configure a parameter marked as deprecated.
     *
     * @return true if the this parameter should not be used anymore
     */
    String deprecated() default "";

    /**
     * Specifies an alias which can be used to configure this parameter from
     * the POM. This is primarily useful to improve user-friendliness, where
     * Mojo field names are not intuitive to the user or are otherwise not
     * conducive to configuration via the POM.
     *
     * @return the xml alias tag name usable in pom files
     */
    String alias() default "";

    /**
     * Specifies the expression used to calculate the value to be injected
     * into this parameter of the Mojo at buildtime. This is commonly used
     * to refer to specific elements in the POM.
     * NOTE: If not specified, an expression of ${<name>} is assumed, which
     * can only be satisfied from POM configuration or System properties.
     * The use of '${' and '}' is required to delimit actual expressions which
     * may be evaluated.
     */
    String expression() default "";

    /**
     * The default value is used when the expression evaluates to null.
     *
     * @return the xml alias tag name usable in pom files
     */
    String defaultValue() default "";
}
