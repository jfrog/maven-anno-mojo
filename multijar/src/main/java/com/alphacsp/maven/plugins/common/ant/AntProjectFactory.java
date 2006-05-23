package com.alphacsp.maven.plugins.common.ant;

import com.alphacsp.maven.plugins.common.injectable.MvnInjectable;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.Project;

/**
 * Created by IntelliJ IDEA.
 * User: yoavl
 */
public abstract class AntProjectFactory {
    private AntProjectFactory() {
    }

    public static Project newAntProject(MvnInjectable injectable) {
        Project antProject = new Project();
        antProject.addBuildListener(new AntBuildListener(injectable.getLog()));
        MavenProject mvnProject = injectable.getProject();
        if (mvnProject == null) {
            throw new IllegalArgumentException("Maven project cannot be null.");
        }
        antProject.setBaseDir(mvnProject.getBasedir());
        antProject.init();
        return antProject;
    }
}
