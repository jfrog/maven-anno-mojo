package source;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;
import org.apache.maven.tools.plugin.extractor.annomojo.annotations.MojoExecute;
import org.apache.maven.tools.plugin.extractor.annomojo.annotations.MojoGoal;
import org.apache.maven.tools.plugin.extractor.annomojo.annotations.MojoParameter;
import org.apache.maven.tools.plugin.extractor.annomojo.annotations.MojoPhase;

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
