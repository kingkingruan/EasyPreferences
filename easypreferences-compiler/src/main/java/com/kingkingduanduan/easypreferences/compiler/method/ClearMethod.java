package com.kingkingduanduan.easypreferences.compiler.method;

import com.kingkingduanduan.easypreferences.compiler.Utils;
import com.squareup.javapoet.MethodSpec;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class ClearMethod extends AbstractMethod {
    private String methodName;
    private TypeMirror returnType;

    public ClearMethod(ExecutableElement executableElement) {
        super(executableElement);
    }

    @Override
    protected boolean checkElement(ExecutableElement executableElement) {
        methodName = executableElement.getSimpleName().toString();
        returnType = executableElement.getReturnType();
        List<? extends VariableElement> params = executableElement.getParameters();
        if (params != null && params.size() > 0) {
            throw new IllegalArgumentException(methodName + " is clear method, need not params");
        }
        if (returnType.getKind() != TypeKind.VOID) {
            throw new IllegalArgumentException(methodName + " : clear method must return void");
        }
        return true;
    }

    @Override
    public void parseElement(ExecutableElement executableElement) {
        key = executableElement.getSimpleName().toString().toLowerCase();
    }

    @Override
    public MethodSpec generateMethodSpec() {
        if (Utils.isEmpty(methodName)) {
            throw new IllegalArgumentException("parse fail,can't get method name");
        }
        return MethodSpec.methodBuilder(methodName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("sp.edit().clear().commit()")
                .build();
    }

    @Override
    public String getMapKey() {
        return "clear";
    }
}
