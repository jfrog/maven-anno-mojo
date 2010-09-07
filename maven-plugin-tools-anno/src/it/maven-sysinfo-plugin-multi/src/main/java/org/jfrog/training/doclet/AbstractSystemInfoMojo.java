package main.java.org.jfrog.training.doclet;

import org.apache.maven.plugin.AbstractMojo;

import java.util.Set;

/**
 * A mojo that uses doclets.
 *
 * @author Yossi Shaul
 */
public abstract class AbstractSystemInfoMojo extends AbstractMojo {
    /**
     * System properties to print
     *
     * @parameter
     */
    private Set properties;

    /**
     * a message
     *
     * @parameter default-value = "ABSTRACT DECLARATION"
     */
    private String message;

    public String getMessage() {
        return message;
    }
}
