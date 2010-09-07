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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;
import org.jfrog.maven.annomojo.annotations.MojoExecute;
import org.jfrog.maven.annomojo.annotations.MojoGoal;
import org.jfrog.maven.annomojo.annotations.MojoParameter;
import org.jfrog.maven.annomojo.annotations.MojoPhase;

import java.util.List;

/**
 * My mojo description
 */
@MojoGoal("doIt")
@MojoPhase("package")
@MojoExecute(phase = "package")
public class MyMojo {

    @MojoParameter
    private List<Artifact> artifacts;

    @MojoParameter(expression = "${project}", required = true, readonly = true,
            description = "The Maven Project")
    private MavenProject project;

    /**
     * The local repository
     */
    @MojoParameter(expression = "${localRepository}")
    private ArtifactRepository localRepository;

    //...
}
