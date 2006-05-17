package org.apache.maven.tools.plugin.extractor.anno;

import org.apache.maven.artifact.repository.ArtifactRepository;

/**
 * Created by IntelliJ IDEA.
 * User: yoavl
 */
public interface MavenHelper {
    ArtifactRepository getLocalRepository() throws Exception;
}
