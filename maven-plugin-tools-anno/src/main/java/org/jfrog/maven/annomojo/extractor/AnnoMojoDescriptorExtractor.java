/*
 * Copyright (C) 2010 JFrog Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jfrog.maven.annomojo.extractor;

import com.sun.tools.apt.Main;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.descriptor.InvalidPluginDescriptorException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProject;
import org.apache.maven.tools.plugin.PluginToolsRequest;
import org.apache.maven.tools.plugin.extractor.ExtractionException;
import org.apache.maven.tools.plugin.extractor.MojoDescriptorExtractor;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Java 1.5+ Annotation-based MojoDescriptorExtractor
 *
 * @author Yoav Landman
 * @author Frederic Simon
 * @author Yossi Shaul
 */
public class AnnoMojoDescriptorExtractor
        extends AbstractLogEnabled implements MojoDescriptorExtractor, Contextualizable {

    public void contextualize(Context context) throws ContextException {
    }

    public List<MojoDescriptor> execute(PluginToolsRequest pluginToolsRequest)
            throws ExtractionException, InvalidPluginDescriptorException {
        List<MojoDescriptor> list = execute(pluginToolsRequest.getProject(), pluginToolsRequest.getPluginDescriptor());
        return list;
    }

    @SuppressWarnings({"unchecked"})
    public List<MojoDescriptor> execute(MavenProject project, PluginDescriptor pluginDescriptor)
            throws InvalidPluginDescriptorException {
        List<String> sourceRoots = project.getCompileSourceRoots();
        Set<String> sourcePathElements = new HashSet<String>();
        String srcRoot = null;
        try {
            for (String sourceRoot : sourceRoots) {
                srcRoot = sourceRoot;
                List<File> files = FileUtils.getFiles(new File(srcRoot), "**/*.java", null, true);
                for (File file : files) {
                    String path = file.getPath();
                    sourcePathElements.add(path);
                }
            }
        } catch (Exception e) {
            throw new InvalidPluginDescriptorException(
                    "Failed to get source files from " + srcRoot, e);
        }
        List<String> argsList = new ArrayList<String>();
        argsList.add("-nocompile");
        argsList.add("-cp");
        StringBuilder cp = new StringBuilder();
        //Add the compile classpath
        List<String> compileClasspathElements;
        try {
            compileClasspathElements = project.getCompileClasspathElements();
        } catch (DependencyResolutionRequiredException e) {
            throw new InvalidPluginDescriptorException(
                    "Failed to get compileClasspathElements.", e);
        }
        for (String ccpe : compileClasspathElements) {
            appendToPath(cp, ccpe);
        }

        //Add the current CL classptah
        URL[] urls = ((URLClassLoader) getClass().getClassLoader()).getURLs();
        for (URL url : urls) {
            String path;
            try {
                path = url.getPath();
            } catch (Exception e) {
                throw new InvalidPluginDescriptorException(
                        "Failed to get classpath files from " + url, e);
            }
            appendToPath(cp, path);
        }
        
        // Attempts to add dependencies to the classpath so that parameters inherited from abstract mojos in other
        // projects will be processed.
        Set s = project.getDependencyArtifacts();
        if (s != null) {
            for (Object untypedArtifact : project.getDependencyArtifacts()) {
                if ( untypedArtifact instanceof Artifact) {
                    Artifact artifact = (Artifact) untypedArtifact;
                    File artifactFile = artifact.getFile();
                    if (artifactFile != null) {
                        appendToPath(cp, artifactFile.getAbsolutePath());
                    }
                }
            }
        }
        
        String classpath = cp.toString();
        debug("cl=" + classpath);
        argsList.add(classpath);
        argsList.addAll(sourcePathElements);
        String[] args = argsList.toArray(new String[argsList.size()]);
        List<MojoDescriptor> descriptors = new ArrayList<MojoDescriptor>();
        MojoDescriptorTls.setDescriptors(descriptors);
        try {
            Main.process(new MojoApf(pluginDescriptor), new PrintWriter(System.out), args);
        } catch (Throwable t) {
            //TODO: [by yl] This is never caught - apt swallows the exception.
            //Use the TLS to hold thrown exception
            throw new InvalidPluginDescriptorException(
                    "Failed to extract plugin descriptor.", t);
        }
        return MojoDescriptorTls.getDescriptors();
    }

    private void debug(String msg) {
        Logger log = getLogger();
        if (log != null) {
            log.debug(msg);
        } else {
            System.out.println(msg);
        }
    }

    private void appendToPath(StringBuilder cp, String path) {
        if (path != null && path.length() > 0) {
            cp.append(path);
            cp.append(File.pathSeparator);
        }
    }

}
