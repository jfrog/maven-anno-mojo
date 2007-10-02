package org.apache.maven.tools.plugin.extractor.annomojo;

import org.apache.maven.plugin.descriptor.MojoDescriptor;

import java.util.List;

/**
 * @author Yoav Landman (ylandman at gmail.com)
 */
class MojoDescriptorTls {

    private static ThreadLocal<List<MojoDescriptor>> TL = new ThreadLocal<List<MojoDescriptor>>();

    static void addDescriptor(MojoDescriptor descriptor) {
        List<MojoDescriptor> descriptors = TL.get();
        descriptors.add(descriptor);
    }

    static List<MojoDescriptor> getDescriptors() {
        return TL.get();
    }

    static void setDescriptors(List<MojoDescriptor> descriptors) {
        TL.set(descriptors);
    }
}
