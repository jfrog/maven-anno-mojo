package source;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.tools.plugin.extractor.anno.annotations.MojoGoal;
import org.apache.maven.tools.plugin.extractor.anno.annotations.MojoPhase;

import java.io.File;

/**
 * Jar and (optionally) sign multiple jar artifacts
 */
@MojoGoal("test")
@MojoPhase("test")
public class Mojo2 implements MojoIfc {

    @SuppressWarnings({"UNUSED_SYMBOL"})
    private File param3;

    public void execute() throws MojoExecutionException, MojoFailureException {
        //NOP
    }

    public File getParam3() {
        return param3;
    }
}
