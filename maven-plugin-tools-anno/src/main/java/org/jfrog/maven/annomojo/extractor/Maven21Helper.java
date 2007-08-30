package org.jfrog.maven.annomojo.extractor;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * @author Yoav Landman (ylandman at gmail.com)
 */
public class Maven21Helper implements MavenHelper {

    //private MavenTools tools;

    Maven21Helper(PlexusContainer container) throws ComponentLookupException {
        //tools = (MavenTools) container.lookup(MavenTools.ROLE);
    }

    /*public ArtifactRepository getLocalRepository() throws Exception {
        String localRepositoryPath = tools.getLocalRepositoryPath();
        return tools.createLocalRepository(new File(localRepositoryPath));
    }*/

    public ArtifactRepository getLocalRepository() throws Exception {
        return null;
    }
}
