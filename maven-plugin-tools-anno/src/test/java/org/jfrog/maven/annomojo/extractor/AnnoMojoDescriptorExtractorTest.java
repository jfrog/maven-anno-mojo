package org.jfrog.maven.annomojo.extractor;

import junit.framework.TestCase;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.URL;
import java.util.List;

public class AnnoMojoDescriptorExtractorTest extends TestCase {

    public void testCreateMojoDescriptor() throws Exception {
        AnnoMojoDescriptorExtractor extractor = new AnnoMojoDescriptorExtractor();

        File sourceFile = fileOf("dir-flag.txt");
        System.out.println("found source file: " + sourceFile);

        File dir = sourceFile.getParentFile();

        Model model = new Model();
        model.setArtifactId("maven-unitTesting-plugin");

        MavenProject project = new MavenProject(model);

        project.setFile(new File(dir, "pom.xml"));
        project.addCompileSourceRoot(new File(dir, "source").getCanonicalPath());

        PluginDescriptor pluginDescriptor = new PluginDescriptor();
        pluginDescriptor.setGoalPrefix("test");
        List results = extractor.execute(project, pluginDescriptor);
        assertEquals(2, results.size());
    }

    private File fileOf(String classpathResource) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL resource = cl.getResource(classpathResource);

        File result = null;
        if (resource != null) {
            result = new File(resource.getPath());
        }

        return result;
    }

}