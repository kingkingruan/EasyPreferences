package com.kingkingduanduan.easypreferences.compiler;

import com.google.auto.service.AutoService;
import com.kingkingduanduan.easypreferences.annotations.Preferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import static javax.lang.model.element.ElementKind.INTERFACE;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.kingkingduanduan.easypreferences.annotations.Preferences")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class PreferencesProcessor extends AbstractProcessor {
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Map<TypeElement, KeySet> xmlMap = findAndParseTargets(roundEnvironment);
        return false;
    }

    private Map<TypeElement, KeySet> findAndParseTargets(RoundEnvironment env) {
        Map<TypeElement, KeySet> xmlMap = new HashMap<>();
        for (Element element : env.getElementsAnnotatedWith(Preferences.class)) {
            TypeElement typeElement= (TypeElement) element;
            checkPreferencesValid(element);
            KeySet keySet = new KeySet();
            xmlMap.put(typeElement, keySet);
        }
        return xmlMap;
    }

    private boolean checkPreferencesValid(Element element) {
        if (isInterface(element.asType())) {
            return true;
        }
        error(element, ((TypeElement) element).getQualifiedName() + " is not interface");
        return false;
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
