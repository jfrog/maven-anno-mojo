package com.alphacsp.maven.plugins.common.injectable;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.tools.plugin.extractor.anno.annotations.MojoComponent;
import org.apache.maven.tools.plugin.extractor.anno.annotations.MojoParameter;

/**
 * Created by IntelliJ IDEA.
 * User: yoavl
 */
public abstract class MvnInjectableSupport implements MvnInjectable {

    @MojoParameter(expression = "${project}", required = true, readonly = true,
            description = "The Maven Project")
    private MavenProject project;

    /**
     * project-helper instance, used to make addition of resources
     * simpler.
     */
    @MojoComponent
    private MavenProjectHelper projectHelper;

    private Log log;

    public MvnInjectableSupport() {
    }

    public MavenProject getProject() {
        return project;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }

    public MavenProjectHelper getProjectHelper() {
        return projectHelper;
    }

    public void setProjectHelper(MavenProjectHelper projectHelper) {
        this.projectHelper = projectHelper;
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public void updateFromMvnInjectable(MvnInjectable injectable) {
        //Set the properties that we expect has been injected to the mojo
        setProject(injectable.getProject());
        setProjectHelper(injectable.getProjectHelper());
        setLog(injectable.getLog());
    }
}
