package main.java.org.jfrog.training.anno;

import org.apache.maven.plugin.AbstractMojo;
import org.jfrog.maven.annomojo.annotations.MojoParameter;

/**
 * A mojo that usses annotations.
 *
 * @author Yossi Shaul
 */
public abstract class AbstractSystemInfoAnnoMojo extends AbstractMojo {

    @MojoParameter(defaultValue = "ANNO_ABSRACTION")
    protected String message;

    public String getMessage() {
        return message;
    }
}