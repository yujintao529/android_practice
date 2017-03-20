package com.example;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;

/**
 * Created by yujintao on 16/9/28.
 */

public class FactoryAnnotationProcessor {

    public TypeElement element;

    private String qualifiedSuperClassName;
    private String simpleTypeName;
    private String id;


    public FactoryAnnotationProcessor(TypeElement element) {
        this.element = element;
        Factory factory=element.getAnnotation(Factory.class);
        id=factory.id();
        try {
            Class<?> clazz = factory.type();
            qualifiedSuperClassName = clazz.getCanonicalName();
            simpleTypeName = clazz.getSimpleName();
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            qualifiedSuperClassName = classTypeElement.getQualifiedName().toString();
            simpleTypeName = classTypeElement.getSimpleName().toString();
        }
    }

    public TypeElement getElement() {
        return element;
    }

    public void setElement(TypeElement element) {
        this.element = element;
    }

    public String getQualifiedSuperClassName() {
        return qualifiedSuperClassName;
    }

    public void setQualifiedSuperClassName(String qualifiedSuperClassName) {
        this.qualifiedSuperClassName = qualifiedSuperClassName;
    }

    public String getSimpleTypeName() {
        return simpleTypeName;
    }

    public void setSimpleTypeName(String simpleTypeName) {
        this.simpleTypeName = simpleTypeName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
