package com.alphacsp.maven.plugins.common.injectable;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

/**
 * Created by IntelliJ IDEA.
 * User: yoavl
 */
public interface MvnInjectable {
    MavenProject getProject();

    MavenProjectHelper getProjectHelper();

    Log getLog();
}
