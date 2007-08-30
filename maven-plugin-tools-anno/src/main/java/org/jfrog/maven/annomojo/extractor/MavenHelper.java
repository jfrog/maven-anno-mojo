package org.jfrog.maven.annomojo.extractor;

import org.apache.maven.artifact.repository.ArtifactRepository;

/**
 * @author Yoav Landman (ylandman at gmail.com)
 */
public interface MavenHelper {
    ArtifactRepository getLocalRepository() throws Exception;
}
