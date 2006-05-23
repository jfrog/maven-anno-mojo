package com.alphacsp.maven.plugins.multijar;

import com.alphacsp.maven.plugins.MvnInjectableMojoSupport;
import com.alphacsp.maven.plugins.signjar.SignJarSupport;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.tools.plugin.extractor.anno.annotations.MojoGoal;
import org.apache.maven.tools.plugin.extractor.anno.annotations.MojoParameter;
import org.apache.maven.tools.plugin.extractor.anno.annotations.MojoPhase;

import java.util.ArrayList;
import java.util.List;

/**
 * Jar and (optionally) sign multiple jar artifacts
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
        if (multijars == null) {
            multijars = new ArrayList<Multijar>();
        }
        //Always create the default jar
        multijars.add(new Multijar());
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
