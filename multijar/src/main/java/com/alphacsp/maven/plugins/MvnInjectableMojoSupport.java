package com.alphacsp.maven.plugins;

import org.apache.maven.plugin.ContextEnabled;
import org.apache.maven.plugin.Mojo;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: yoavl
 */
public abstract class MvnInjectableMojoSupport extends MvnInjectableSupport
        implements Mojo, ContextEnabled {

    private Map pluginContext;

    public Map getPluginContext() {
        return pluginContext;
    }

    public void setPluginContext(Map pluginContext) {
        this.pluginContext = pluginContext;
    }
}
