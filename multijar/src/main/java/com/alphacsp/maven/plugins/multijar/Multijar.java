package com.alphacsp.maven.plugins.multijar;

import com.alphacsp.maven.plugins.common.ant.AntProjectFactory;
import com.alphacsp.maven.plugins.common.injectable.MvnInjectableSupport;
import com.alphacsp.maven.plugins.signjar.SignJarSupport;
import com.alphacsp.maven.plugins.signjar.SignjarMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.ManifestException;

import java.io.File;
import java.util.jar.Attributes;

/**
 * Created by IntelliJ IDEA.
 * User: yoavl
 */
public class Multijar extends MvnInjectableSupport {

    /**
     * The output jar name
     *
     * @noinspection UNUSED_SYMBOL
     */
    private File jarfile;

    /**
     * The target base directory of the output jar
     *
     * @noinspection UNUSED_SYMBOL
     */
    private File basedir;

    /**
     * An optional manifest files
     *
     * @noinspection UNUSED_SYMBOL
     */
    private File manifest;

    /**
     * A comma-seperated list of exclusion filters for the jar
     *
     * @noinspection UNUSED_SYMBOL
     */
    private String excludes;

    /**
     * A comma-seperated list of inclusion filters for the jar
     *
     * @noinspection UNUSED_SYMBOL
     */
    private String includes;

    /**
     * Optional jar signing configuration
     *
     * @noinspection UNUSED_SYMBOL
     */
    private SignjarMojo sign;

    private MultijarMojo mojo;

    /**
     * Default ctor
     */
    public Multijar() {
    }

    public void setMojo(MultijarMojo mojo) {
        this.mojo = mojo;
        updateFromMvnInjectable(mojo);
        //Init the defaults (no maven injection for non-mojos)
        if (jarfile == null) {
            String jarfileName = getProject().getBuild().getDirectory() + "/"
                    + getProject().getArtifactId() + "-"
                    + getProject().getVersion() + ".jar";
            jarfile = new File(jarfileName);
        }

        if (basedir == null) {
            String basedirName = getProject().getBuild().getOutputDirectory();
            basedir = new File(basedirName);
        }
        //Set includes and excludes defaults
        //(cannot be configured in javadoc annotations, due to no comment escapes
        if (includes == null) {
            includes = "**/**";
        }
        if (excludes == null) {
            excludes = "";
        }
    }

    public void execute(boolean singleJar)
            throws MojoExecutionException {
        //Create an Ant project and attach it to the current Maven execution
        Project antProject = AntProjectFactory.newAntProject(this);

        //Create the Jar task
        Jar jarTask = new Jar();
        jarTask.setProject(antProject);

        //Set the manifest
        if (manifest != null) {
            jarTask.setManifest(manifest);
        }
        try {
            //Patch the manifest with some mvn properties
            Manifest mf = new Manifest();
            mf.addConfiguredAttribute(
                    new Manifest.Attribute(Attributes.Name.MANIFEST_VERSION.toString(), "1.0"));
            mf.addConfiguredAttribute(
                    new Manifest.Attribute("Created-By", "Apache Maven Bootstrap Mini"));
            jarTask.addConfiguredManifest(mf);
            //Set the basedir
            jarTask.setBasedir(basedir);

            //Set includes and excludes patterns
            jarTask.setIncludes(includes);
            jarTask.setExcludes(excludes);

            //Set the output jar
            jarTask.setDestFile(jarfile);

            //Execute the task
            getLog().info("Jarring: " + jarfile.getAbsolutePath());
            jarTask.execute();

            handleSignjar(singleJar);
        } catch (ManifestException e) {
            throw new MojoExecutionException("Jar manifest creation failed", e);
        }
    }

    private void handleSignjar(boolean singleJar) throws MojoExecutionException {
        //If signing is enabled, sign the jar and add it to the artifacts
        //First check if there is a general signning config, then use specific sign configs to
        //overrride the general config.
        SignJarSupport config = mojo.getSignConfig();
        if (config == null && sign == null) {
            getLog().info("No signning configured for " + jarfile.getName() + ". Doing nothing.");
            //Add the new jar artifact to mvn artifacts
            getProjectHelper().attachArtifact(getProject(), "jar", "multijar", jarfile);
            return;
        }
        //We have a general sign config but no specific one
        if (sign == null) {
            sign = new SignjarMojo(config);
        } else {
            sign.merge(config);
        }
        sign.setJar(jarfile);
        //Replace the original jar if no target is specified, and generate a jarNameNoExt based on
        //the signed jar name.
        String jarName = jarfile.getName();
        String jarNameNoExt;
        File signedjar = sign.getSignedjar();
        if (signedjar == null) {
            //Create default signed jar
            File defaultSignedJar = null;
            int extIdx = jarName.lastIndexOf('.');
            if (extIdx > 0) {
                //Create the default based on the original jar name
                jarNameNoExt = jarName.substring(0, extIdx);
                defaultSignedJar =
                        new File(getProject().getBuild().getDirectory() + "/"
                                + jarNameNoExt + "-signed" + jarName.substring(extIdx));
            } else {
                jarNameNoExt = jarName;
            }
            sign.setSignedjar(defaultSignedJar);
        } else {
            String signedJarName = signedjar.getName();
            int extIdx = signedJarName.lastIndexOf('.');
            jarNameNoExt = signedJarName.substring(0, extIdx);
        }
        if (!singleJar) {
            sign.setClassifier(jarNameNoExt);
        }
        //Execute and add the signed jar as an artifact
        sign.updateFromMvnInjectable(this);
        sign.execute();
    }
}
