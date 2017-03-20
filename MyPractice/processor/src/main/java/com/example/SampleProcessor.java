package com.example;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Completion;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(value = Processor.class)
public class SampleProcessor extends AbstractProcessor {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    Set<Element> elements=new HashSet<>();

    private Map<String, FactoryManager> factoryClasses =
            new LinkedHashMap<String, FactoryManager>();

    public SampleProcessor() {
        super();
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Factory.class)) {

            if(annotatedElement.getKind()== ElementKind.CLASS){
//                elements.add(annotatedElement);

                try {
                    FactoryAnnotationProcessor factoryAnnotationProcessor=new FactoryAnnotationProcessor((TypeElement) annotatedElement);

                    if (!isValidClass(factoryAnnotationProcessor)) {
                        return true; // Error message printed, exit processing
                    }

                    // Everything is fine, so try to add
                    FactoryManager factoryClass =
                            factoryClasses.get(factoryAnnotationProcessor.getQualifiedSuperClassName());
                    if (factoryClass == null) {
                        String qualifiedGroupName = factoryAnnotationProcessor.getQualifiedSuperClassName();
                        factoryClass = new FactoryManager(qualifiedGroupName);
                        factoryClasses.put(qualifiedGroupName, factoryClass);
                    }

                    // Throws IdAlreadyUsedException if id is conflicting with
                    // another @Factory annotated class with the same id
                    factoryClass.add(factoryAnnotationProcessor);
                } catch (IllegalArgumentException e) {
                    // @Factory.id() is empty --> printing error message
                    error(annotatedElement, e.getMessage());
                    return true;
//                } catch (IllegalArgumentException e) {
//                    FactoryAnnotatedClass existing = e.getExisting();
                    // Already existing
//                    error(annotatedElement,
//                            "Conflict: The class %s is annotated with @%s with id ='%s' but % already uses the same id",
//                            typeElement.getQualifiedName().toString(), Factory.class.getSimpleName(),
//                            existing.getTypeElement().getQualifiedName().toString());
//                    return true;
                }

            }else{
                error(annotatedElement, "Only classes can be annotated with @%s",
                        Factory.class.getSimpleName());
            }
        }

        try {
            for (FactoryManager factoryClass : factoryClasses.values()) {
                factoryClass.generateCode(elementUtils, filer);
            }
            factoryClasses.clear();
        } catch (IOException e) {
            error(null, e.getMessage());
        }

        return true;

    }

    private boolean isValidClass(FactoryAnnotationProcessor item) {

        // Cast to TypeElement, has more type specific methods
        TypeElement classElement = item.getElement();

        if (!classElement.getModifiers().contains(Modifier.PUBLIC)) {
            error(classElement, "The class %s is not public.",
                    classElement.getQualifiedName().toString());
            return false;
        }

        // Check if it's an abstract class
        if (classElement.getModifiers().contains(Modifier.ABSTRACT)) {
            error(classElement, "The class %s is abstract. You can't annotate abstract classes with @%",
                    classElement.getQualifiedName().toString(), Factory.class.getSimpleName());
            return false;
        }

        // Check inheritance: Class must be childclass as specified in @Factory.type();
        TypeElement superClassElement =
                elementUtils.getTypeElement(item.getQualifiedSuperClassName());
        if (superClassElement.getKind() == ElementKind.INTERFACE) {
            // Check interface implemented
            if (!classElement.getInterfaces().contains(superClassElement.asType())) {
                error(classElement, "The class %s annotated with @%s must implement the interface %s",
                        classElement.getQualifiedName().toString(), Factory.class.getSimpleName(),
                        item.getQualifiedSuperClassName());
                return false;
            }
        } else {
            // Check subclassing
            TypeElement currentClass = classElement;
            while (true) {
                TypeMirror superClassType = currentClass.getSuperclass();

                if (superClassType.getKind() == TypeKind.NONE) {
                    // Basis class (java.lang.Object) reached, so exit
                    error(classElement, "The class %s annotated with @%s must inherit from %s",
                            classElement.getQualifiedName().toString(), Factory.class.getSimpleName(),
                            item.getQualifiedSuperClassName());
                    return false;
                }

                if (superClassType.toString().equals(item.getQualifiedSuperClassName())) {
                    // Required super class found
                    break;
                }

                // Moving up in inheritance tree
                currentClass = (TypeElement) typeUtils.asElement(superClassType);
            }
        }

        // Check if an empty public constructor is given
        for (Element enclosed : classElement.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement constructorElement = (ExecutableElement) enclosed;
                if (constructorElement.getParameters().size() == 0 && constructorElement.getModifiers()
                        .contains(Modifier.PUBLIC)) {
                    // Found an empty constructor
                    return true;
                }
            }
        }

        // No empty constructor found
        error(classElement, "The class %s must provide an public empty default constructor",
                classElement.getQualifiedName().toString());
        return false;
    }

    private void error(Element e, String msg, Object... args) {
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }

    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(Factory.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils=processingEnv.getTypeUtils();
        elementUtils=processingEnv.getElementUtils();
        filer=processingEnv.getFiler();
        messager=processingEnv.getMessager();

    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText) {
        return super.getCompletions(element, annotation, member, userText);
    }

    @Override
    protected synchronized boolean isInitialized() {
        return super.isInitialized();
    }
}
