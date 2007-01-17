package source;

import org.jfrog.maven.annomojo.annotations.annotations.MojoParameter;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: yoavl
 */
public interface MojoIfc {
    @MojoParameter
    public File getParam3();
}
