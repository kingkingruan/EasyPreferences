package com.kingkingduanduan.easypreferences.compiler;

import com.kingkingduanduan.easypreferences.annotations.Clear;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * Created by ruanjinjing on 2018/3/22.
 */

public class KeySet {
    private static final String SET = "set";
    private static final String GET = "get";
    private final ClassName contextClassName;
    private final ClassName sharedPreferencesClassName;

    private TypeElement typeElement;
    private final String spName;

    Map<String, KeyElements> keyMethodsMap = new HashMap<>();
    private ExecutableElement clearElement;

    public KeySet(TypeElement typeElement) {
        this.typeElement = typeElement;
        spName = "Easy" + typeElement.getSimpleName().toString();
        contextClassName = ClassName.get("android.content", "Context");
        sharedPreferencesClassName = ClassName.get("android.content", "SharedPreferences");
    }

    public void put(ExecutableElement executableElement) {
        String methodName = executableElement.getSimpleName().toString();
        TypeMirror returnType = executableElement.getReturnType();
        List<? extends AnnotationMirror> annotationMirrorList = executableElement.getAnnotationMirrors();
        if (annotationMirrorList != null && annotationMirrorList.size() > 0) {
            String clearClassName = Clear.class.getName();
            for (AnnotationMirror annotationMirror : annotationMirrorList) {
                if (annotationMirror.getAnnotationType().toString().equals(clearClassName)) {
                    List<? extends VariableElement> params = executableElement.getParameters();
                    if (params != null && params.size() > 0) {
                        throw new IllegalArgumentException(methodName + " is clear method, need not params");
                    }
                    if (returnType.getKind() != TypeKind.VOID) {
                        throw new IllegalArgumentException(methodName + " : clear method must return void");
                    }
                    if (clearElement == null) {
                        clearElement = executableElement;
                    } else {
                        throw new IllegalArgumentException("more than one @Clear");
                    }
                }
            }
        } else {
            String key = methodName.substring(3);
            KeyElements keyElements = keyMethodsMap.get(key);
            if (methodName.startsWith(SET)) {
                if (returnType.getKind() != TypeKind.VOID) {
                    throw new IllegalArgumentException(methodName + " must return Void");
                }
                checkSetMethod(executableElement);
                if (keyElements == null) {
                    keyElements = new KeyElements(key);
                    keyMethodsMap.put(key, keyElements);
                }
                keyElements.setSetMethod(executableElement);
            } else if (methodName.startsWith(GET)) {
                if (returnType.getKind() == TypeKind.VOID) {
                    throw new IllegalArgumentException(methodName + " must not return Void");
                }
                checkGetMethod(executableElement);
                if (keyElements == null) {
                    keyElements = new KeyElements(key);
                    keyMethodsMap.put(key, keyElements);
                }
                keyElements.setGetMethod(executableElement);
            } else {
                throw new IllegalArgumentException(methodName + "can't parse");
            }
        }
    }

    private void checkSetMethod(ExecutableElement executableElement) {
        List<? extends VariableElement> params = executableElement.getParameters();
        if (params == null || params.size() != 1) {
            throw new IllegalArgumentException("one params");
        }
        TypeMirror typeMirror = executableElement.getReturnType();
        if (typeMirror.getKind() != TypeKind.VOID) {
            throw new IllegalArgumentException(executableElement.getSimpleName() + " must returns void");
        }
    }

    private void checkGetMethod(ExecutableElement executableElement) {
        List<? extends VariableElement> params = executableElement.getParameters();
        if (params == null || params.size() != 0) {
            throw new IllegalArgumentException("no param");
        }
        TypeMirror typeMirror = executableElement.getReturnType();
        if (typeMirror.getKind() == TypeKind.VOID) {
            throw new IllegalArgumentException(executableElement.getSimpleName() + " returns void");
        }
    }

    public JavaFile brewJava() {
        return JavaFile.builder(PreferencesProcessor.packageName, createType())
                .addFileComment("Generated code from EasyPreferences. Do not modify!")
                .build();
    }

    private TypeSpec createType() {
        TypeSpec.Builder builder = TypeSpec.classBuilder(spName)
                .addModifiers(Modifier.PUBLIC)
                .addField(sharedPreferencesClassName, "sp", Modifier.PRIVATE)
                .addMethod(getConstructorMethod())
                .addSuperinterface(ClassName.get(typeElement))
                .addMethods(getMethods());
        return builder.build();
    }

    private MethodSpec getConstructorMethod() {
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(contextClassName, "context")
                .addStatement("sp=context.getSharedPreferences($S,android.content.Context.MODE_PRIVATE)", spName)
                .build();
        return constructor;
    }

    private List<MethodSpec> getMethods() {
        List<MethodSpec> methodSpecs = new ArrayList<>();
        for (Map.Entry<String, KeyElements> entry : keyMethodsMap.entrySet()) {
            String key = entry.getKey();
            KeyElements keyElements = entry.getValue();
            ExecutableElement set = keyElements.getSetMethod();
            if (set != null) {
                TypeName setType = ClassName.get(set.getParameters().get(0).asType());
                MethodSpec setMethodSpec = MethodSpec.methodBuilder(set.getSimpleName().toString())
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(setType, "value")
                        .addStatement("sp.edit().put$N($S,value).commit()", getPutType(setType), key)
                        .build();
                methodSpecs.add(setMethodSpec);
            }
            ExecutableElement get = keyElements.getGetMethod();
            if (get != null) {
                TypeName getType = ClassName.get(get.getReturnType());
                MethodSpec getMethodSpec = MethodSpec.methodBuilder(get.getSimpleName().toString())
                        .addAnnotation(Override.class)
                        .returns(getType)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return sp.get$N($S,$N)", getPutType(getType), key, getDefaultValue(getType))
                        .build();
                methodSpecs.add(getMethodSpec);
            }
        }
        if (clearElement != null) {
            MethodSpec clearMethodSpec = MethodSpec.methodBuilder(clearElement.getSimpleName().toString())
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("sp.edit().clear().commit()")
                    .build();
            methodSpecs.add(clearMethodSpec);
        }
        return methodSpecs;
    }

    private String getPutType(TypeName typeName) {
        String type = "";
        TypeName unboxed = typeName.isBoxedPrimitive() ? typeName.unbox() : typeName;
        if (unboxed == TypeName.BOOLEAN) {
            type = "Boolean";
        } else if (unboxed == TypeName.FLOAT) {
            type = "Float";
        } else if (unboxed == TypeName.INT) {
            type = "Int";
        } else if (unboxed == TypeName.LONG) {
            type = "Long";
        } else if ("java.lang.String".equals(unboxed.toString())) {
            type = "String";
        } else if ("java.util.Set<java.lang.String>".equals(unboxed.toString())) {
            type = "StringSet";
        }
        return type;
    }

    private String getDefaultValue(TypeName typeName) {
        String type = "";
        TypeName unboxed = typeName.isBoxedPrimitive() ? typeName.unbox() : typeName;
        if (unboxed == TypeName.BOOLEAN) {
            type = "false";
        } else if (unboxed == TypeName.FLOAT) {
            type = "0.0f";
        } else if (unboxed == TypeName.INT) {
            type = "0";
        } else if (unboxed == TypeName.LONG) {
            type = "0l";
        } else if ("java.lang.String".equals(unboxed.toString())) {
            type = "\"\"";
        } else if ("java.util.Set<java.lang.String>".equals(unboxed.toString())) {
            type = "null";
        }
        return type;
    }
}
