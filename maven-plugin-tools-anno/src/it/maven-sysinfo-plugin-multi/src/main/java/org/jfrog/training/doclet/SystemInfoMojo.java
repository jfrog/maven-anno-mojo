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

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Prints system properties.
 *
 * @goal sysinfo
 * @phase validate
 * @since 1.3-SNAPSHOT
 */
public class SystemInfoMojo extends AbstractSystemInfoMojo {

    /**
     * a message
     *
     * @parameter default-value = "IMPLEMENTATION"
     */
    private String message;

    //public String getMessage() {
    //    return message;
    //}

    public void execute() throws MojoExecutionException {
        getLog().info("Message = " + getMessage());
        //if (properties == null || properties.isEmpty()) {
        //    getLog().warn("Properties list is empty");
        //    return;
        //}
        //
        //Iterator keysIter = properties.iterator();
        //while (keysIter.hasNext()) {
        //    String key = (String) keysIter.next();
        //    String value = System.getProperty(key);
        //    getLog().info(key + "= " + value);
        //}
    }
}
