package source;

import org.apache.maven.tools.plugin.extractor.anno.annotations.MojoParameter;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: yoavl
 */
public interface MojoIfc {
    @MojoParameter
    public File getParam3();
}
