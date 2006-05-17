package com.alphacsp.maven.plugins.signjar;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.tools.plugin.extractor.anno.annotations.MojoGoal;
import org.apache.maven.tools.plugin.extractor.anno.annotations.MojoPhase;
import org.apache.maven.tools.plugin.extractor.anno.annotations.MojoParameter;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.SignJar;

import java.io.File;

/**
 * Jar and (optionally) sign multiple jar artifacts
 *
 */
@MojoGoal("sign")
@MojoPhase("package")
public class SignjarMojo extends SignJarSupport {

    /**
     * The source jar name
     */
    @MojoParameter(
            defaultValue = "${project.build.directory}/${project.artifactId}-${project.version}.jar",
            required = true)
    protected File jar;

    /**
     * The name of signed JAR file
     *
     */
    @MojoParameter
    private File signedjar;

    private String classifier;

    public SignjarMojo() {
    }

    public SignjarMojo(SignJarSupport config) {
        setAlias(config.getAlias());
        setKeystore(config.getKeystore());
        setStorepass(config.getStorepass());
    }

    /*
    sign - alias, signedjar, keystore, storepass
    */
    public void execute() throws MojoExecutionException {

        //Create an Ant project and attach it to the current Maven execution
        Project antProject = new Project();
        antProject.setBaseDir(getProject().getBasedir());
        antProject.init();

        //Create the Jar task
        SignJar signJarTask = new SignJar();
        signJarTask.setProject(antProject);
        signJarTask.setVerbose(true);

        //Some validation
        if (jar != null) {
            if (!jar.exists()) {
                fail(new MojoExecutionException(
                        "Source jar for signing cannot be found: " + jar.getPath()));
            }
            //Validate source and target are different
            if (jar.equals(signedjar)) {
                fail(new MojoExecutionException(
                        "Source and target for signing must be different."));
            }
        }
        String keystore = getKeystore();
        if (keystore != null && !new File(keystore).exists()) {
            fail(new MojoExecutionException(
                    "Keystore cannot be found: " + jar.getPath()));
        }
        //Create the roots for the target jar
        if (signedjar != null) {
            signedjar.getParentFile().mkdirs();
        }

        //Setting a default alias in case none exits
        if (getAlias() == null) {
            setAlias(System.getProperty("os.user"));
        }

        //Set the default signed jar if none exists
        if (signedjar == null) {
            signedjar =
                    new File(getProject().getBuild().getDirectory() + "/"
                            + getProject().getArtifactId()
                            + "-signed-" + getProject().getVersion() + ".jar");
        }

        signJarTask.setAlias(getAlias());
        signJarTask.setJar(jar);
        signJarTask.setSignedjar(signedjar);
        signJarTask.setKeystore(keystore);
        signJarTask.setStorepass(getStorepass());

        try {
            //Execute the task
            signJarTask.execute();
            getLog().info("Signed: " + signedjar.getCanonicalPath());
            //Add the new jar artifact to mvn artifacts
            getProjectHelper().attachArtifact(getProject(), "jar", classifier, signedjar);
        } catch (Exception e) {
            fail(e);
        }
    }

    public File getSignedjar() {
        return signedjar;
    }

    public void setSignedjar(File signedjar) {
        this.signedjar = signedjar;
    }

    public File getJar() {
        return jar;
    }

    public void setJar(File jar) {
        this.jar = jar;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    private void fail(Exception e) throws MojoExecutionException {
        getLog().error(e);
        throw new MojoExecutionException("Jar signing failed.", e);
    }

}
