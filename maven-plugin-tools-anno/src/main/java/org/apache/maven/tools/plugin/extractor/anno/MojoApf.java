package org.apache.maven.tools.plugin.extractor.anno;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.declaration.*;
import com.sun.mirror.type.ClassType;
import com.sun.mirror.type.InterfaceType;
import com.sun.mirror.util.DeclarationVisitors;
import com.sun.mirror.util.SimpleDeclarationVisitor;
import org.apache.maven.plugin.descriptor.*;
import org.apache.maven.tools.plugin.extractor.anno.annotations.*;
import org.codehaus.plexus.util.StringUtils;

import java.util.*;

class MojoApf implements AnnotationProcessorFactory {
    //Process any set of annotations
    private static final Collection<String> supportedAnnotations
            = Collections.unmodifiableCollection(Arrays.asList(
            AnnoMojoDescriptorExtractor.class.getPackage().getName() + ".annotations.*"));
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

            private MojoDescriptor mojoDescriptor;
            private Set<Declaration> visitedDeclarations = new HashSet<Declaration>();

            public void visitClassDeclaration(ClassDeclaration d) {
                //Merge supper classes and interfaces declarations in top down fashion
                //(actually we're overriding topmost metadata with bottom-most one)
                Collection<Modifier> modifiers = d.getModifiers();
                boolean abstarct = false;
                for (Modifier modifier : modifiers) {
                    if (modifier.equals(Modifier.ABSTRACT)) {
                        abstarct = true;
                        break;
                    }
                }
                if (checkVisited(d)) {
                    return;
                }
                if (abstarct && mojoDescriptor == null) {
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
                    mojoDescriptor = new MojoDescriptor();
                    mojoDescriptor = new MojoDescriptor();
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
                            methodName.substring(3,4).toLowerCase() + methodName.substring(4);
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
                    if (executePhase == null && executeGoal == null) {
                        throw new IllegalArgumentException(
                                "Eexecute tag requires a 'phase' or 'goal' parameter");
                    } else if (executePhase != null && executeGoal != null) {
                        throw new IllegalArgumentException(
                                "@Execute tag can have only one of a 'phase' or 'goal' parameter");
                    }
                    mojoDescriptor.setExecutePhase(executePhase);
                    mojoDescriptor.setExecuteGoal(executeGoal);
                    String lifecycle = execute.lifecycle();
                    if (lifecycle != null) {
                        mojoDescriptor.setExecuteLifecycle(lifecycle);
                        if (mojoDescriptor.getExecuteGoal() != null) {
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
                    String value = requiresDependencyResolution.value();
                    mojoDescriptor.setDependencyResolutionRequired(value);
                }
                // ----------------------------------------------------------------------
                // Project flag
                // ----------------------------------------------------------------------
                MojoRequiresProject requiresProject = d.getAnnotation(MojoRequiresProject.class);
                if (requiresProject != null) {
                    mojoDescriptor.setProjectRequired(true);
                } else {
                    mojoDescriptor.setProjectRequired(mojoDescriptor.isProjectRequired());
                }
                // ----------------------------------------------------------------------
                // Aggregator flag
                // ----------------------------------------------------------------------
                MojoAggregator aggregator = d.getAnnotation(MojoAggregator.class);
                if (aggregator != null) {
                    mojoDescriptor.setAggregator(true);
                }
                // ----------------------------------------------------------------------
                // requiresDirectInvocation flag
                // ----------------------------------------------------------------------
                MojoRequiresDirectInvocation requiresDirectInvocation =
                        d.getAnnotation(MojoRequiresDirectInvocation.class);
                if (requiresDirectInvocation != null) {
                    mojoDescriptor.setDirectInvocationOnly(true);
                } else {
                    mojoDescriptor.setDirectInvocationOnly(mojoDescriptor.isDirectInvocationOnly());
                }
                // ----------------------------------------------------------------------
                // Online flag
                // ----------------------------------------------------------------------
                MojoRequiresOnline requiresOnline = d.getAnnotation(MojoRequiresOnline.class);
                if (requiresOnline != null) {
                    mojoDescriptor.setOnlineRequired(true);
                } else {
                    mojoDescriptor.setOnlineRequired(mojoDescriptor.isOnlineRequired());
                }
                // ----------------------------------------------------------------------
                // inheritByDefault flag
                // ----------------------------------------------------------------------
                MojoInheritedByDefault inheritedByDefault =
                        d.getAnnotation(MojoInheritedByDefault.class);
                if (inheritedByDefault != null) {
                    mojoDescriptor.setInheritedByDefault(true);
                } else {
                    mojoDescriptor.setInheritedByDefault(mojoDescriptor.isInheritedByDefault());
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
                    pd.setEditable(parameter.readonly());
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
                if (superclass == null) {
                    return false;
                }
                return (!superclass.getDeclaration().getQualifiedName().equals("java.lang.Object"));
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