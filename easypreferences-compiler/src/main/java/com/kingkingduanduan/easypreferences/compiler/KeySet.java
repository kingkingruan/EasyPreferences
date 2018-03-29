package com.kingkingduanduan.easypreferences.compiler;

import com.kingkingduanduan.easypreferences.annotations.Clear;
import com.kingkingduanduan.easypreferences.annotations.Default;
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
import javax.lang.model.type.MirroredTypeException;
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
        Clear clear = executableElement.getAnnotation(Clear.class);
        if (clear != null) {
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
                methodSpecs.add(generateSetMethodSpec(key, set));
            }
            ExecutableElement get = keyElements.getGetMethod();
            if (get != null) {
                methodSpecs.add(generateGetMethodSpec(key, get));
            }
        }
        if (clearElement != null) {
            methodSpecs.add(generateClearMethodSpec());
        }
        return methodSpecs;
    }

    private MethodSpec generateSetMethodSpec(String key, ExecutableElement executableElement) {
        TypeName setType = ClassName.get(executableElement.getParameters().get(0).asType());
        return MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(setType, "value")
                .addStatement("sp.edit().put$N($S,value).commit()", getPutType(setType), key)
                .build();
    }

    private MethodSpec generateGetMethodSpec(String key, ExecutableElement executableElement) {
        TypeName getType = ClassName.get(executableElement.getReturnType());
        return MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
                .addAnnotation(Override.class)
                .returns(getType)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return sp.get$N($S,$N)", getPutType(getType), key, getDefaultValue(executableElement))
                .build();
    }

    private MethodSpec generateClearMethodSpec() {
        return MethodSpec.methodBuilder(clearElement.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("sp.edit().clear().commit()")
                .build();
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

    private String getDefaultValue(ExecutableElement executableElement) {
        TypeName typeName = ClassName.get(executableElement.getReturnType());
        Default anno = executableElement.getAnnotation(Default.class);
        String[] defaultValue = null;
        try {
            if (anno != null) {
                defaultValue = anno.value();
            }
        } catch (MirroredTypeException mte) {
            mte.printStackTrace();
        }
        String type = "";
        TypeName unboxed = typeName.isBoxedPrimitive() ? typeName.unbox() : typeName;
        if (unboxed == TypeName.BOOLEAN) {
            if (defaultValue != null && defaultValue.length > 0) {
                type = defaultValue[0];
            } else
                type = "false";
        } else if (unboxed == TypeName.FLOAT) {
            if (defaultValue != null && defaultValue.length > 0) {
                type = defaultValue[0].endsWith("f") ? defaultValue[0] : defaultValue[0] + "f";
            } else {
                type = "0.0f";
            }
        } else if (unboxed == TypeName.INT) {
            if (defaultValue != null && defaultValue.length > 0) {
                type = defaultValue[0];
            } else {
                type = "0";
            }
        } else if (unboxed == TypeName.LONG) {
            if (defaultValue != null && defaultValue.length > 0) {
                type = defaultValue[0];
            } else {
                type = "0l";
            }
        } else if ("java.lang.String".equals(unboxed.toString())) {
            if (defaultValue != null && defaultValue.length > 0) {
                type = "\"" + defaultValue[0] + "\"";
            } else {
                type = "\"\"";
            }
        } else if ("java.util.Set<java.lang.String>".equals(unboxed.toString())) {
            if (defaultValue != null && defaultValue.length > 0) {
                StringBuilder builder = new StringBuilder();
                builder.append("new java.util.HashSet<String>(java.util.Arrays.asList(new java.lang.String[]{");
                for (int i = 0; i < defaultValue.length; i++) {
                    builder.append("\"").append(defaultValue[i]).append("\"");
                    if (i != (defaultValue.length - 1)) {
                        builder.append(",");
                    }
                }
                builder.append("}))");
                type = builder.toString();
            } else {
                type = "null";
            }
        }
        return type;
    }
}
