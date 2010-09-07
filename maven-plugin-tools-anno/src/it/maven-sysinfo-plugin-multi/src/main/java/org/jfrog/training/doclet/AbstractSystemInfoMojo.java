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

package main.java.org.jfrog.training.doclet;

import org.apache.maven.plugin.AbstractMojo;

import java.util.Set;

/**
 * A mojo that uses doclets.
 *
 * @author Yossi Shaul
 */
public abstract class AbstractSystemInfoMojo extends AbstractMojo {
    /**
     * System properties to print
     *
     * @parameter
     */
    private Set properties;

    /**
     * a message
     *
     * @parameter default-value = "ABSTRACT DECLARATION"
     */
    private String message;

    public String getMessage() {
        return message;
    }
}
