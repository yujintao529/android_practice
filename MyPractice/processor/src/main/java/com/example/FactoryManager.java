package com.example;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javawriter.JavaWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

/**
 * Created by yujintao on 16/9/28.
 */

public class FactoryManager {
    private String qualifiedClassName;

    private Map<String, FactoryAnnotationProcessor> itemsMap =
            new LinkedHashMap<String, FactoryAnnotationProcessor>();

    public FactoryManager(String qualifiedClassName) {
        this.qualifiedClassName = qualifiedClassName;
    }

    public void add(FactoryAnnotationProcessor toInsert) throws IllegalArgumentException {

        FactoryAnnotationProcessor existing = itemsMap.get(toInsert.getId());
        if (existing != null) {
            throw new IllegalArgumentException("id all ready exist");
        }

        itemsMap.put(toInsert.getId(), toInsert);
    }

    private String SUFFIX="Generate";

    public void generateCode(Elements elementUtils, Filer filer) throws IOException {
        TypeElement superClassName = elementUtils.getTypeElement(qualifiedClassName);
        PackageElement packageElement=elementUtils.getPackageOf(superClassName);
        String factoryClassName = superClassName.getSimpleName() + SUFFIX;
//        JavaFileObject jfo = filer.createSourceFile(qualifiedClassName + SUFFIX);
//        Writer writer = jfo.openWriter();
//
//
        //** 测试**//
//MethodSpec main = MethodSpec.methodBuilder("main")
//        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//                .returns(void.class)
//                .addParameter(String[].class, "args")
//                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
//                .build();
//
//        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
//                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
//                .addMethod(main)
//                .build();
//
//        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
//                .build();
//        javaFile.writeTo(filer);
        //** 测试**//


//javapoet 使用
        ClassName className=ClassName.get(superClassName);
        MethodSpec.Builder methodSpec=MethodSpec.methodBuilder("create").addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .returns(Void.TYPE).addParameter(String.class,"id");

        for(FactoryAnnotationProcessor factoryAnnotationProcessor: itemsMap.values()){

        }


        TypeSpec factory = TypeSpec.classBuilder(factoryClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod( methodSpec.build())
                .build();
        JavaFile javaFile;
        if(!packageElement.isUnnamed()){
            javaFile=JavaFile.builder(packageElement.getQualifiedName().toString(), factory).build();
        }else{
            javaFile=JavaFile.builder("",factory).build();
        }


        javaFile.writeTo(filer);






//        JavaWriter jw = new JavaWriter(writer);
//
//        // Write package
//        PackageElement pkg = elementUtils.getPackageOf(superClassName);
//        if (!pkg.isUnnamed()) {
//            jw.emitPackage(pkg.getQualifiedName().toString());
//            jw.emitEmptyLine();
//        } else {
//            jw.emitPackage("");
//        }
//
//        jw.beginType(factoryClassName, "class", EnumSet.of(Modifier.PUBLIC));
//        jw.emitEmptyLine();
//        jw.beginMethod(qualifiedClassName, "create", EnumSet.of(Modifier.PUBLIC), "String", "id");
//
//        jw.beginControlFlow("if (id == null)");
//        jw.emitStatement("throw new IllegalArgumentException(\"id is null!\")");
//        jw.endControlFlow();
//
////        for (FactoryAnnotationProcessor item : itemsMap.values()) {
////            jw.beginControlFlow("if (\"%s\".equals(id))", item.getId());
////            jw.emitStatement("return new %s()", item.getElement().getQualifiedName().toString());
////            jw.endControlFlow();
////            jw.emitEmptyLine();
////        }
//        jw.emitStatement("throw new IllegalArgumentException(\"Unknown id = \" + id)");
//
//        jw.endType();
//
//        jw.close();
    }
}
