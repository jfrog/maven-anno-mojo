package org.apache.maven.tools.plugin.extractor.annomojo;

import org.apache.maven.artifact.repository.ArtifactRepository;

/**
 * @author Yoav Landman (ylandman at gmail.com)
 */
public interface MavenHelper {
    ArtifactRepository getLocalRepository() throws Exception;
}
