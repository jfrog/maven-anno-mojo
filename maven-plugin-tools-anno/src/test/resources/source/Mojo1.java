package source;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.tools.plugin.extractor.annomojo.annotations.MojoGoal;
import org.apache.maven.tools.plugin.extractor.annomojo.annotations.MojoParameter;
import org.apache.maven.tools.plugin.extractor.annomojo.annotations.MojoPhase;

import java.io.File;
import java.util.List;

/**
 * Jar and (optionally) sign multiple jar artifacts
 */
@MojoGoal("test")
@MojoPhase("test")
@SuppressWarnings({"UNUSED_SYMBOL"})
public class Mojo1 extends MojoSupport {

    @MojoParameter
    private List param1;

    @MojoParameter
    private String param2;

    private File param3;

    public void execute() throws MojoExecutionException, MojoFailureException {
        //NOP
    }

    public File getParam3() {
        return param3;
    }
}
