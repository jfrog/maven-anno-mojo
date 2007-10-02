package source;

import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.tools.plugin.extractor.annomojo.annotations.MojoComponent;

/**
 * User: freds
 * Date: Jul 8, 2007
 * Time: 12:00:56 PM
 */
public interface MvnAnnoIfc {
    @MojoComponent(description =
        "project-helper instance, used to make addition of resources simpler.")
    MavenProjectHelper getProjectHelper();
}
