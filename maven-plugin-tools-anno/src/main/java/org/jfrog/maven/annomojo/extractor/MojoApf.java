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

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.InterfaceDeclaration;
import com.sun.mirror.declaration.MemberDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.Modifier;
import com.sun.mirror.declaration.TypeDeclaration;
import com.sun.mirror.type.ClassType;
import com.sun.mirror.type.InterfaceType;
import com.sun.mirror.util.DeclarationVisitors;
import com.sun.mirror.util.SimpleDeclarationVisitor;
import org.apache.maven.plugin.descriptor.DuplicateParameterException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.Parameter;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.descriptor.Requirement;
import org.apache.maven.tools.plugin.ExtendedMojoDescriptor;
import org.codehaus.plexus.util.StringUtils;
import org.jfrog.maven.annomojo.annotations.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * APT AnnotationProcessorFactory
 *
 * @author Yoav Landman (ylandman at gmail.com)
 * @author Frederic Simon (frederic.simon at gmail.com)
 * @author Yossi Shaul (yossish at sf.net)
 */
class MojoApf implements AnnotationProcessorFactory {
    //Process any annotations from the MojoAnnotation package
    private static final Collection<String> supportedAnnotations
            = Collections.unmodifiableCollection(Arrays.asList(
            MojoAnnotation.class.getPackage().getName() + ".*"));

    //No supported options
    private static final Collection<String> supportedOptions = Collections.emptySet();

    private final PluginDescriptor descriptor;

    public MojoApf(PluginDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public Collection<String> supportedAnnotationTypes() {
        return supportedAnnotations;
    }

    public Collection<String> supportedOptions() {
        return supportedOptions;
    }

    public AnnotationProcessor getProcessorFor(
            Set<AnnotationTypeDeclaration> atds, AnnotationProcessorEnvironment env) {
        return new MojoAp(env);
    }

    private class MojoAp implements AnnotationProcessor {
        private final AnnotationProcessorEnvironment env;

        MojoAp(AnnotationProcessorEnvironment env) {
            this.env = env;
        }

        public void process() {
            Collection<TypeDeclaration> declarations = env.getSpecifiedTypeDeclarations();
            for (TypeDeclaration typeDecl : declarations) {
                try {
                    typeDecl.accept(DeclarationVisitors.getDeclarationScanner(
                            new MojoClassVisitor(), DeclarationVisitors.NO_OP));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private class MojoClassVisitor extends SimpleDeclarationVisitor {

            private ExtendedMojoDescriptor mojoDescriptor;

            private Set<Declaration> visitedDeclarations = new HashSet<Declaration>();

            public void visitClassDeclaration(ClassDeclaration d) {
                //Merge supper classes and interfaces declarations in top down fashion
                //(actually we're overriding topmost metadata with bottom-most one)
                if (checkVisited(d)) {
                    return;
                }

                boolean isAbstract = d.getModifiers().contains(Modifier.ABSTRACT);
                if (isAbstract && mojoDescriptor == null) {
                    return;
                }
                // ----------------------------------------------------------------------
                // Goal flag
                // ----------------------------------------------------------------------
                MojoGoal goal = d.getAnnotation(MojoGoal.class);
                //Do not process classes/interfaces that are non-mojos.
                //These will be processes later on on the chain if inherited by a mojo.
                if (goal == null && mojoDescriptor == null) {
                    return;
                }
                //Create a new descriptor and set the following only for the concrete mojo
                if (mojoDescriptor == null) {
                    mojoDescriptor = new ExtendedMojoDescriptor();
                    mojoDescriptor.setPluginDescriptor(descriptor);
                    mojoDescriptor.setLanguage("java");
                    mojoDescriptor.setImplementation(d.getQualifiedName());
                    mojoDescriptor.setDescription(d.getDocComment());
                    mojoDescriptor.setGoal(goal.value());
                    MojoDescriptorTls.addDescriptor(mojoDescriptor);
                }
                //First visit super classes and interfaces recursively
                ClassType superclass = d.getSuperclass();
                if (shouldProcessClass(superclass)) {
                    visitClassDeclaration(superclass.getDeclaration());
                }
                Collection<InterfaceType> superinterfaces = d.getSuperinterfaces();
                for (InterfaceType superinterface : superinterfaces) {
                    visitInterfaceDeclaration(superinterface.getDeclaration());
                }
                //Then process our own metadata and override
                if (mojoDescriptor != null) {
                    processTypeMetadata(d);
                }
                // ----------------------------------------------------------------------
                // Phase flag
                // ----------------------------------------------------------------------
                MojoPhase phase = d.getAnnotation(MojoPhase.class);
                if (phase != null) {
                    mojoDescriptor.setPhase(phase.value());
                }
                //Continue the delegation chain
                d.accept(DeclarationVisitors.getDeclarationScanner(
                        this, DeclarationVisitors.NO_OP));
            }

            public void visitInterfaceDeclaration(InterfaceDeclaration d) {
                //Do nothing unless this is called from a concrete type
                if (checkVisited(d) || mojoDescriptor == null) {
                    return;
                }
                Collection<InterfaceType> superinterfaces = d.getSuperinterfaces();
                for (InterfaceType superinterface : superinterfaces) {
                    visitInterfaceDeclaration(superinterface.getDeclaration());
                }
                processTypeMetadata(d);
                //Continue the delegation chain
                d.accept(DeclarationVisitors.getDeclarationScanner(
                        this, DeclarationVisitors.NO_OP));
            }

            public void visitMethodDeclaration(MethodDeclaration d) {
                if (checkVisited(d) || mojoDescriptor == null) {
                    return;
                }
                //Find getters
                Parameter pd;
                String propertyName;
                String propertyType;
                String methodName = d.getSimpleName();
                if (methodName.startsWith("get")) {
                    pd = new Parameter();
                    propertyName =
                            methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
                    propertyType = d.getReturnType().toString();
                    processPropertyMetadata(d, pd, propertyType, propertyName);
                }
                d.accept(DeclarationVisitors.getDeclarationScanner(
                        this, DeclarationVisitors.NO_OP));
            }

            public void visitFieldDeclaration(FieldDeclaration d) {
                if (checkVisited(d) || mojoDescriptor == null) {
                    return;
                }
                String propertyName = d.getSimpleName();
                String propertyType = d.getType().toString();
                Parameter pd = new Parameter();
                processPropertyMetadata(d, pd, propertyType, propertyName);
                d.accept(DeclarationVisitors.getDeclarationScanner(
                        this, DeclarationVisitors.NO_OP));
            }

            private void processTypeMetadata(Declaration d) {
                // ----------------------------------------------------------------------
                // Instantiation strategy
                // ----------------------------------------------------------------------
                MojoInstantiationStrategy instantiationStrategy =
                        d.getAnnotation(MojoInstantiationStrategy.class);
                if (instantiationStrategy != null) {
                    mojoDescriptor.setInstantiationStrategy(instantiationStrategy.value());
                }
                // ----------------------------------------------------------------------
                // Multi execution
                // ----------------------------------------------------------------------
                MojoMultiExecution multiExecution =
                        d.getAnnotation(MojoMultiExecution.class);
                if (multiExecution != null) {
                    mojoDescriptor.setExecutionStrategy(MojoDescriptor.MULTI_PASS_EXEC_STRATEGY);
                } else {
                    mojoDescriptor.setExecutionStrategy(MojoDescriptor.SINGLE_PASS_EXEC_STRATEGY);
                }
                // ----------------------------------------------------------------------
                // Configurator hint
                // ----------------------------------------------------------------------
                MojoConfigurator configurator = d.getAnnotation(MojoConfigurator.class);
                if (instantiationStrategy != null) {
                    mojoDescriptor.setComponentConfigurator(configurator.value());
                }
                // ----------------------------------------------------------------------
                // Execute flag
                // ----------------------------------------------------------------------
                MojoExecute execute = d.getAnnotation(MojoExecute.class);
                if (execute != null) {
                    String executePhase = execute.phase();
                    String executeGoal = execute.goal();
                    String lifecycle = execute.lifecycle();
                    //Nullify empty values
                    if (executePhase.length() == 0) {
                        executePhase = null;
                    }
                    if (executeGoal.length() == 0) {
                        executeGoal = null;
                    }
                    if (lifecycle.length() == 0) {
                        lifecycle = null;
                    }
                    if (executePhase == null && executeGoal == null) {
                        throw new IllegalArgumentException(
                                "Eexecute tag requires a 'phase' or 'goal' parameter");
                    } else if (executePhase != null && executeGoal != null) {
                        throw new IllegalArgumentException(
                                "@Execute tag can have only one of a 'phase' or 'goal' parameter");
                    }
                    mojoDescriptor.setExecutePhase(executePhase);
                    mojoDescriptor.setExecuteGoal(executeGoal);

                    if (lifecycle != null) {
                        mojoDescriptor.setExecuteLifecycle(lifecycle);
                        if (executeGoal != null) {
                            throw new IllegalArgumentException(
                                    "@Execute lifecycle requires a phase instead of a goal");
                        }
                    }
                }
                // ----------------------------------------------------------------------
                // Dependency flag
                // ----------------------------------------------------------------------
                MojoRequiresDependencyResolution requiresDependencyResolution =
                        d.getAnnotation(MojoRequiresDependencyResolution.class);
                if (requiresDependencyResolution != null) {
                    mojoDescriptor.setDependencyResolutionRequired(requiresDependencyResolution.value());
                }
                // ----------------------------------------------------------------------
                // Collection flag
                // ----------------------------------------------------------------------
                MojoRequiresDependencyCollection requiresDependencyCollection =
                        d.getAnnotation(MojoRequiresDependencyCollection.class);
                if (requiresDependencyCollection != null) {
                    mojoDescriptor.setDependencyCollectionRequired(requiresDependencyCollection.value());
                }
                // ----------------------------------------------------------------------
                // Project flag
                // ----------------------------------------------------------------------
                MojoRequiresProject requiresProject = d.getAnnotation(MojoRequiresProject.class);
                if (requiresProject != null) {
                    mojoDescriptor.setProjectRequired(requiresProject.value());
                }
                // ----------------------------------------------------------------------
                // Aggregator flag
                // ----------------------------------------------------------------------
                MojoAggregator aggregator = d.getAnnotation(MojoAggregator.class);
                if (aggregator != null) {
                    mojoDescriptor.setAggregator(aggregator.value());
                }
                // ----------------------------------------------------------------------
                // requiresDirectInvocation flag
                // ----------------------------------------------------------------------
                MojoRequiresDirectInvocation requiresDirectInvocation =
                        d.getAnnotation(MojoRequiresDirectInvocation.class);
                if (requiresDirectInvocation != null) {
                    mojoDescriptor.setDirectInvocationOnly(requiresDirectInvocation.value());
                }
                // ----------------------------------------------------------------------
                // Online flag
                // ----------------------------------------------------------------------
                MojoRequiresOnline requiresOnline = d.getAnnotation(MojoRequiresOnline.class);
                if (requiresOnline != null) {
                    mojoDescriptor.setOnlineRequired(requiresOnline.value());
                }
                // ----------------------------------------------------------------------
                // inheritByDefault flag
                // ----------------------------------------------------------------------
                MojoInheritedByDefault inheritedByDefault = d.getAnnotation(MojoInheritedByDefault.class);
                if (inheritedByDefault != null) {
                    mojoDescriptor.setInheritedByDefault(inheritedByDefault.value());
                }
                // ----------------------------------------------------------------------
                // Since flag
                // ----------------------------------------------------------------------
                MojoSince since = d.getAnnotation(MojoSince.class);
                if (since != null) {
                    mojoDescriptor.setSince(since.value());
                }
                // ----------------------------------------------------------------------
                // Since flag
                // ----------------------------------------------------------------------
                MojoThreadSafe threadSafe = d.getAnnotation(MojoThreadSafe.class);
                if (threadSafe != null) {
                    mojoDescriptor.setThreadSafe(threadSafe.value());
                }
            }

            private void processPropertyMetadata(
                    MemberDeclaration d, Parameter pd, String propertyType, String propertyName) {
                MojoParameter parameter = d.getAnnotation(MojoParameter.class);
                MojoComponent component = d.getAnnotation(MojoComponent.class);
                if (parameter != null || component != null) {
                    pd.setName(propertyName);
                    pd.setType(propertyType);
                } else {
                    return;
                }
                if (component != null) {
                    String description = component.description();
                    if (StringUtils.isEmpty(description)) {
                        description = d.getDocComment();
                    }
                    pd.setDescription(description);
                    String role = component.role();
                    if (role.length() == 0) {
                        role = propertyType;
                    }
                    String roleHint = component.roleHint();
                    if (roleHint.length() == 0) {
                        roleHint = null;
                    }
                    pd.setRequirement(new Requirement(role, roleHint));
                    pd.setName(propertyName);
                } else {
                    String description = parameter.description();
                    if (StringUtils.isEmpty(description)) {
                        description = d.getDocComment();
                    }
                    pd.setDescription(description);
                    String property = parameter.property();
                    if (!StringUtils.isEmpty(property)) {
                        pd.setName(property);
                    } else {
                        pd.setName(propertyName);
                    }
                    pd.setRequired(parameter.required());
                    pd.setEditable(!parameter.readonly());
                    String deprecated = parameter.deprecated();
                    if (!StringUtils.isEmpty(deprecated)) {
                        pd.setDeprecated(deprecated);
                    }
                    String alias = parameter.alias();
                    if (!StringUtils.isEmpty(alias)) {
                        pd.setAlias(alias);
                    }
                    String expression = parameter.expression();
                    if (StringUtils.isEmpty(expression)) {
                        expression = null;
                    }
                    pd.setExpression(expression);
                    if ("${reports}".equals(pd.getExpression())) {
                        mojoDescriptor.setRequiresReports(true);
                    }
                    String defaultValue = parameter.defaultValue();
                    if (!StringUtils.isEmpty(defaultValue)) {
                        pd.setDefaultValue(defaultValue);
                    }
                }
                try {
                    List params = mojoDescriptor.getParameters();
                    if (params != null && params.contains(pd)) {
                        // remove the supercalss param declaration
                        params.remove(pd);
                    }
                    mojoDescriptor.addParameter(pd);
                } catch (DuplicateParameterException e) {
                    throw new IllegalArgumentException(
                            "DuplicateParameter parameter: " + propertyName);
                }
            }

            @SuppressWarnings({"UNUSED_SYMBOL"})
            private boolean isMojo(TypeDeclaration d) {
                Collection<InterfaceType> superinterfaces = d.getSuperinterfaces();
                for (InterfaceType superinterface : superinterfaces) {
                    if (superinterface.getDeclaration().getQualifiedName().equals(
                            "org.apache.maven.plugin.Mojo")) {
                        return true;
                    } else {
                        if (isMojo(superinterface.getDeclaration())) {
                            return true;
                        }
                    }
                }
                //Check in super classes
                if (d instanceof ClassDeclaration) {
                    ClassType superclass = ((ClassDeclaration) d).getSuperclass();
                    if (shouldProcessClass(superclass)) {
                        if (isMojo(superclass.getDeclaration())) {
                            return true;
                        }
                    }
                }
                return false;
            }

            private boolean shouldProcessClass(ClassType superclass) {
                return superclass != null &&
                        superclass.getDeclaration() != null &&
                        !superclass.getDeclaration().getQualifiedName().equals("java.lang.Object");
            }

            private boolean checkVisited(Declaration d) {
                if (visitedDeclarations.contains(d)) {
                    return true;
                } else {
                    if (mojoDescriptor != null) {
                        visitedDeclarations.add(d);
                    }
                    return false;
                }
            }
        }
    }
}
