package main.java.org.jfrog.training.anno;

import org.apache.maven.plugin.MojoExecutionException;
import org.jfrog.maven.annomojo.annotations.MojoGoal;
import org.jfrog.maven.annomojo.annotations.MojoParameter;
import org.jfrog.maven.annomojo.annotations.MojoPhase;

import java.util.Set;

/**
 * Prints system properties.
 */
@MojoGoal(value = "sysinfoanno")
@MojoPhase("validate")
public class SystemInfoAnnoMojo extends AbstractSystemInfoAnnoMojo {

    /**
     * System properties to print
     */
    @MojoParameter(defaultValue = "")
    private Set<String> properties;

    @MojoParameter(defaultValue = "Annotated plugin active and working")
    private String message;

    public String getMessage() {
        return message;
    }

    public void execute() throws MojoExecutionException {
        getLog().info(getMessage());
        if (properties == null || properties.isEmpty()) {
            getLog().warn("Properties list is empty");
            return;
        }

        getLog().info("Properties:");
        for (String property : properties) {
            String value = System.getProperty(property);
            getLog().info(property + "= " + value);
        }

    }
}
