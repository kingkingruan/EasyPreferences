package com.kingkingduanduan.easypreferences.compiler;

import com.kingkingduanduan.easypreferences.compiler.method.AbstractMethod;
import com.kingkingduanduan.easypreferences.compiler.method.MethodFactory;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

/**
 * Created by ruanjinjing on 2018/3/22.
 */

public class KeySet {

    private final ClassName contextClassName;
    private final ClassName sharedPreferencesClassName;

    private TypeElement typeElement;
    private final String spName;

    Map<String, AbstractMethod> methodMap = new HashMap<>();

    public KeySet(TypeElement typeElement) {
        this.typeElement = typeElement;
        spName = "Easy" + typeElement.getSimpleName().toString();
        contextClassName = ClassName.get("android.content", "Context");
        sharedPreferencesClassName = ClassName.get("android.content", "SharedPreferences");
    }

    public void put(ExecutableElement executableElement) {
        AbstractMethod method = MethodFactory.parseMethod(executableElement);
        if (method != null) {
            AbstractMethod mapValue = methodMap.get(method.getMapKey());
            if (mapValue == null) {
                if (checkKeyTypeMathch(method)) {
                    methodMap.put(method.getMapKey(), method);
                }
            } else {
                throw new IllegalArgumentException(String.format("%s %s repeat defined",
                        mapValue.getExecutableElement().getSimpleName().toString(),
                        method.getExecutableElement().getSimpleName().toString()));
            }
        } else {
            throw new IllegalArgumentException(executableElement.getSimpleName().toString() + " parse fail");
        }
    }

    private boolean checkKeyTypeMathch(AbstractMethod method) {
        if ("clear".equals(method.getMapKey())) {
            return true;
        }
        String key = method.getKey();
        String anotherMapkey;
        if (method.getMapKey().startsWith("set")) {
            anotherMapkey = "get" + key;
        } else {
            anotherMapkey = "set" + key;
        }
        AbstractMethod anotherMethod = methodMap.get(anotherMapkey);
        if (anotherMethod != null) {
            if (Utils.isSameType(anotherMethod.getKeyType(),method.getKeyType())) {
                return true;
            } else {
                throw new IllegalArgumentException(String.format("type of %s is not the same", key));
            }
        }
        return true;
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
        for (Map.Entry<String, AbstractMethod> entry : methodMap.entrySet()) {
            AbstractMethod method = entry.getValue();
            methodSpecs.add(method.generateMethodSpec());
        }
        return methodSpecs;
    }

}
