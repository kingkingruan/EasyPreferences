package com.kingkingduanduan.easypreferences.compiler;

import com.google.auto.service.AutoService;
import com.kingkingduanduan.easypreferences.annotations.Preferences;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import static javax.lang.model.element.ElementKind.INTERFACE;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.kingkingduanduan.easypreferences.annotations.Preferences")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class PreferencesProcessor extends AbstractProcessor {
    private Filer filer;
    private Types types;
    private Elements elements;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        types = processingEnvironment.getTypeUtils();
        elements = processingEnvironment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        findAndParseTargets(roundEnvironment);
        return false;
    }

    private void findAndParseTargets(RoundEnvironment env) {
        for (Element element : env.getElementsAnnotatedWith(Preferences.class)) {
            TypeElement typeElement = (TypeElement) element;
            checkPreferencesValid(element);
            KeySet keySet = new KeySet();
            parseTypeElement(keySet, typeElement);
            JavaFile javaFile = keySet.brewJava(elements, typeElement);
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                error(typeElement, "Unable to write Class for type %s: %s", typeElement, e.getMessage());
            }
        }
    }

    private void parseTypeElement(KeySet keySet, TypeElement typeElement) {
        List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
        for (Element element : enclosedElements) {
            if (!(element instanceof ExecutableElement)) {
                continue;
            }
            ExecutableElement executableElement = (ExecutableElement) element;
            keySet.put(executableElement);
        }
    }

    private void checkPreferencesValid(Element element) {
        if (!isInterface(element.asType())) {
            error(element, ((TypeElement) element).getQualifiedName() + " is not interface");
        }
    }

    private void error(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.ERROR, element, message, args);
    }

    private void note(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.NOTE, element, message, args);
    }

    private void printMessage(Diagnostic.Kind kind, Element element, String message, Object[] args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }

        processingEnv.getMessager().printMessage(kind, message, element);
    }

    private boolean isInterface(TypeMirror typeMirror) {
        return typeMirror instanceof DeclaredType
                && ((DeclaredType) typeMirror).asElement().getKind() == INTERFACE;
    }
}
