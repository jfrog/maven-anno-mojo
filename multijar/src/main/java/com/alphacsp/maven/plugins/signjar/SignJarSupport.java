package com.alphacsp.maven.plugins.signjar;

import com.alphacsp.maven.plugins.MvnInjectableMojoSupport;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.tools.plugin.extractor.anno.annotations.MojoParameter;

/**
 * Created by IntelliJ IDEA.
 * User: yoavl
 */
public class SignJarSupport extends MvnInjectableMojoSupport {

    /**
     * The the alias to sign under
     */
    @MojoParameter(required = true)
    private String alias;

    /**
     * The keystore location
     */
    @MojoParameter(required = true)
    private String keystore;

    /**
     * The password for keystore integrity
     */
    @MojoParameter(required = true)
    private String storepass;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getKeystore() {
        return keystore;
    }

    public void setKeystore(String keystore) {
        this.keystore = keystore;
    }

    public String getStorepass() {
        return storepass;
    }

    public void setStorepass(String storepass) {
        this.storepass = storepass;
    }

    public void merge(SignJarSupport config) {
        if (alias == null) {
            setAlias(config.getAlias());
        }
        if (keystore == null) {
            setKeystore(config.getKeystore());
        }
        if (storepass == null) {
            setStorepass(config.getStorepass());
        }
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        throw new UnsupportedOperationException("Override the 'execute' method in your mojo!");
    }
}
