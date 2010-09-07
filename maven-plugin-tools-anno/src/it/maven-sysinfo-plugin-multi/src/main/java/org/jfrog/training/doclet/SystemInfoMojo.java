package main.java.org.jfrog.training.doclet;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Prints system properties.
 *
 * @goal sysinfo
 * @phase validate
 * @since 1.3-SNAPSHOT
 */
public class SystemInfoMojo extends AbstractSystemInfoMojo {

    /**
     * a message
     *
     * @parameter default-value = "IMPLEMENTATION"
     */
    private String message;

    //public String getMessage() {
    //    return message;
    //}

    public void execute() throws MojoExecutionException {
        getLog().info("Message = " + getMessage());
        //if (properties == null || properties.isEmpty()) {
        //    getLog().warn("Properties list is empty");
        //    return;
        //}
        //
        //Iterator keysIter = properties.iterator();
        //while (keysIter.hasNext()) {
        //    String key = (String) keysIter.next();
        //    String value = System.getProperty(key);
        //    getLog().info(key + "= " + value);
        //}
    }
}
