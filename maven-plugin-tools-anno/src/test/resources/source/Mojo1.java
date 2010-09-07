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

package source;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jfrog.maven.annomojo.annotations.MojoGoal;
import org.jfrog.maven.annomojo.annotations.MojoParameter;
import org.jfrog.maven.annomojo.annotations.MojoPhase;

import java.io.File;
import java.util.List;

/**
 * Jar and (optionally) sign multiple jar artifacts
 */
@MojoGoal("test")
@MojoPhase("test")
@SuppressWarnings({"UNUSED_SYMBOL"})
public class Mojo1 extends MojoSupport {

    @MojoParameter
    private List param1;

    @MojoParameter
    private String param2;

    private File param3;

    public void execute() throws MojoExecutionException, MojoFailureException {
        //NOP
    }

    public File getParam3() {
        return param3;
    }
}
