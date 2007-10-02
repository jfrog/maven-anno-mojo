package source;

import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.tools.plugin.extractor.annomojo.annotations.MojoComponent;
import org.apache.maven.tools.plugin.extractor.annomojo.annotations.MojoParameter;


/**
 * Created by IntelliJ IDEA.
 * User: yoavl
 */
@SuppressWarnings({"UNUSED_SYMBOL"})
public abstract class MojoSupport implements MojoIfc {
    @MojoParameter(expression = "${project}", required = true, readonly = true,
            description = "The Maven Project")
    private MavenProject project;

    /**
     * project-helper instance, used to make addition of resources
     * simpler.
     */
    @MojoComponent
    private MavenProjectHelper projectHelper;

}
