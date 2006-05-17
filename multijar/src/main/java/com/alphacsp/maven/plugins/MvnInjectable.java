package com.alphacsp.maven.plugins;

import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.plugin.logging.Log;

/**
 * Created by IntelliJ IDEA.
 * User: yoavl
 */
public interface MvnInjectable {
    MavenProject getProject();

    MavenProjectHelper getProjectHelper();

    Log getLog();
}
