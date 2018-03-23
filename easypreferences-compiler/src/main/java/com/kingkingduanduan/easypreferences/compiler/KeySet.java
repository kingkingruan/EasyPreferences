package com.kingkingduanduan.easypreferences.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

/**
 * Created by ruanjinjing on 2018/3/22.
 */

public class KeySet {
    private static final String SET = "set";
    private static final String GET = "get";

    Map<String, KeyElements> keyMethodsMap = new HashMap<>();

    public void put(ExecutableElement executableElement) {
        String simpleName = executableElement.getSimpleName().toString();
        TypeMirror returnType = executableElement.getReturnType();
        KeyElements keyElements = keyMethodsMap.get(simpleName);
        if (simpleName.startsWith(SET)) {
            if (returnType.getKind() != TypeKind.VOID) {
                throw new IllegalArgumentException(simpleName + " must return Void");
            }
            //todo check method valid
            if (keyElements == null) {
                keyElements = new KeyElements();
            }
            keyElements.setSetMethod(executableElement);
        } else if (simpleName.startsWith(GET)) {
            if (returnType.getKind() == TypeKind.VOID) {
                throw new IllegalArgumentException(simpleName + " must not return Void");
            }
            // TODO: 2018/3/23  check method valid
            if (keyElements == null) {
                keyElements = new KeyElements();
            }
            keyElements.setGetMethod(executableElement);
        } else {
            throw new IllegalArgumentException(simpleName + "can't parse");
        }
    }

    private void checkSetMethod(ExecutableElement executableElement) {

    }

    public JavaFile brewJava(Elements elements, TypeElement typeElement) {
        return JavaFile.builder("com.kingkingduanduan.preferences", createType(elements, typeElement))
                .addFileComment("Generated code from EasyPreferences. Do not modify!")
                .build();
    }

    private TypeSpec createType(Elements elements, TypeElement typeElement) {
        TypeSpec.Builder builder = TypeSpec.classBuilder("Easy" + typeElement.getSimpleName())
                .addModifiers(Modifier.PUBLIC);
        builder.addSuperinterface(ClassName.get(typeElement));
        return builder.build();
    }
}
