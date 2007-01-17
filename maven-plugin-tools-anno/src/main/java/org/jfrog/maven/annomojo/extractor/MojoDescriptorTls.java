package org.jfrog.maven.annomojo.extractor;

import org.apache.maven.plugin.descriptor.MojoDescriptor;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: yoavl
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
