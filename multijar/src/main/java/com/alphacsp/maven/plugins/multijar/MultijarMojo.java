package com.alphacsp.maven.plugins.multijar;

import com.alphacsp.maven.plugins.MvnInjectableMojoSupport;
import com.alphacsp.maven.plugins.signjar.SignJarSupport;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.tools.plugin.extractor.anno.annotations.MojoGoal;
import org.apache.maven.tools.plugin.extractor.anno.annotations.MojoPhase;
import org.apache.maven.tools.plugin.extractor.anno.annotations.MojoParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Jar and (optionally) sign multiple jar artifacts
 *
 */
@MojoGoal("jar")
@MojoPhase("package")
public class MultijarMojo extends MvnInjectableMojoSupport {

    @MojoParameter
    private List<Multijar> multijars;

    @MojoParameter
    @SuppressWarnings({"UNUSED_SYMBOL"})
    private SignJarSupport signConfig;

    public void execute() throws MojoExecutionException, MojoFailureException {
        //Create a default jar in no jars are configured
        if (multijars == null || multijars.size() == 0) {
            multijars = new ArrayList<Multijar>();
            multijars.add(new Multijar());
        }
        boolean singleJar = multijars.size() == 1;
        try {
            for (Multijar mj : multijars) {
                mj.setMojo(this);
                mj.execute(singleJar);
            }
        } catch (Exception e) {
            getLog().error(e);
            throw new MojoExecutionException("Multijar failed", e);
        }
    }

    public SignJarSupport getSignConfig() {
        return signConfig;
    }
}
