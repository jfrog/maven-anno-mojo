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
