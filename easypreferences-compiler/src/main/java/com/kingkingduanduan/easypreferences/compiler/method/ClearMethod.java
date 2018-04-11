package com.kingkingduanduan.easypreferences.compiler.method;

import com.kingkingduanduan.easypreferences.annotations.Apply;
import com.kingkingduanduan.easypreferences.compiler.Utils;
import com.squareup.javapoet.ClassName;
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
    private Apply apply;

    public ClearMethod(ExecutableElement executableElement) {
        super(executableElement);
    }

    @Override
    protected boolean checkElement(ExecutableElement executableElement) {
        methodName = executableElement.getSimpleName().toString();
        returnType = executableElement.getReturnType();
        apply = executableElement.getAnnotation(Apply.class);
        List<? extends VariableElement> params = executableElement.getParameters();
        if (params != null && params.size() > 0) {
            throw new IllegalArgumentException(methodName + " is clear method, need not params");
        }
        return isValidReturnType(returnType);
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
        String returnKey;
        if (returnType.getKind() == TypeKind.VOID) {
            returnKey = "";
        } else {
            returnKey = "return";
        }
        String action;
        if (apply == null) {
            action = "commit";
        } else {
            action = "apply";
        }
        return MethodSpec.methodBuilder(methodName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(returnType))
                .addStatement("$N sp.edit().clear().$N()", returnKey, action)
                .build();
    }

    @Override
    public String getMapKey() {
        return "clear";
    }

    @Override
    public TypeMirror getKeyType() {
        return null;
    }

    private boolean isValidReturnType(TypeMirror returnType) {
        TypeKind typeKind = returnType.getKind();
        if (apply != null && typeKind != TypeKind.VOID) {
            throw new IllegalArgumentException(methodName + " apply method can't return value");
        } else if (typeKind == TypeKind.VOID || typeKind == TypeKind.BOOLEAN) {
            return true;
        } else {
            throw new IllegalArgumentException(methodName + " only support return void or boolean");
        }
    }

}
