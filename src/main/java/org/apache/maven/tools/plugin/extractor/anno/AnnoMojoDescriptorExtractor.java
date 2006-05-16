package org.apache.maven.tools.plugin.extractor.anno;

import com.sun.tools.apt.Main;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaSource;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.factory.DefaultArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.descriptor.InvalidPluginDescriptorException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProject;
import org.apache.maven.tools.plugin.extractor.MojoDescriptorExtractor;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

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
 */
public class AnnoMojoDescriptorExtractor
        extends AbstractLogEnabled implements MojoDescriptorExtractor, Contextualizable {

    protected PlexusContainer container;

    public void contextualize(Context context) throws ContextException {
        container = (PlexusContainer) context.get(PlexusConstants.PLEXUS_KEY);
    }

    @SuppressWarnings({"unchecked"})
    public List<MojoDescriptor> execute(MavenProject project, PluginDescriptor descriptor)
            throws InvalidPluginDescriptorException {
        JavaDocBuilder builder = new JavaDocBuilder();
        List<String> sourceRoots = project.getCompileSourceRoots();
        for (String srcRoot : sourceRoots) {
            builder.addSourceTree(new File(srcRoot));
        }
        Set<String> sourcePathElements = new HashSet<String>();
        JavaSource[] sources = builder.getSources();
        for (JavaSource source : sources) {
            URL url = source.getURL();
            String path;
            try {
                path = new File(url.toURI()).getCanonicalPath();
            } catch (Exception e) {
                throw new InvalidPluginDescriptorException("Failed to get source files.", e);
            }
            sourcePathElements.add(path);
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
            cp.append(ccpe);
            cp.append(File.pathSeparator);
        }
        //Resolve dependencies and add them to the classpath
        resolveDependencies(project, cp);
        //Add the current CL classptah
        URL[] urls = ((URLClassLoader) getClass().getClassLoader()).getURLs();
        for (URL url : urls) {
            String path;
            try {
                path = new File(url.toURI()).getCanonicalPath();
            } catch (Exception e) {
                throw new InvalidPluginDescriptorException("Failed to get classpath files.", e);
            }
            cp.append(path);
            cp.append(File.pathSeparator);
        }
        argsList.add(cp.toString());
        argsList.addAll(sourcePathElements);
        String[] args = argsList.toArray(new String[argsList.size()]);
        ArrayList<MojoDescriptor> descriptors = new ArrayList<MojoDescriptor>();
        MojoDescriptorTls.setDescriptors(descriptors);
        Main.process(new MojoApf(descriptor), new PrintWriter(System.out), args);
        return MojoDescriptorTls.getDescriptors();
    }

    @SuppressWarnings({"unchecked"})
    private void resolveDependencies(MavenProject project, StringBuilder cp) throws InvalidPluginDescriptorException {
        if (container == null) {
            return;
        }
        ArtifactResolver resolver;
        try {
            resolver = (ArtifactResolver) container.lookup(ArtifactResolver.ROLE);
        } catch (ComponentLookupException e) {
            throw new InvalidPluginDescriptorException(
                    "Failed to get the ArtifactResolver.", e);
        }
        ArtifactRepository localRepository = null;
        /*try {
            MavenTools tools = (MavenTools) container.lookup(MavenTools.ROLE);
            String localRepositoryPath = tools.getLocalRepositoryPath();
            localRepository = tools.createLocalRepository(new File(localRepositoryPath));
        } catch (Exception e) {
            throw new InvalidPluginDescriptorException(
                    "Failed to get the local repository.", e);
        }*/
        DefaultArtifactFactory artifactFactory;
        try {
            artifactFactory = (DefaultArtifactFactory) container.lookup(ArtifactFactory.ROLE);
        } catch (ComponentLookupException e) {
            throw new InvalidPluginDescriptorException(
                    "Failed to locate the artifact factory.", e);
        }
        List<Dependency> dependencies = project.getDependencies();
        for (Dependency dependency : dependencies) {
            Artifact artifact = artifactFactory.createArtifact(
                    dependency.getGroupId(),
                    dependency.getArtifactId(),
                    dependency.getVersion(),
                    dependency.getScope(),
                    dependency.getType());
            try {
                resolver.resolve(artifact, project.getRemoteArtifactRepositories(),
                        localRepository);
                File file = artifact.getFile();
                cp.append(file.getCanonicalPath());
                cp.append(File.pathSeparator);
            } catch (Exception e) {
                throw new InvalidPluginDescriptorException(
                        "Failed to resolve artifact: " + artifact, e);
            }
        }
    }
}
