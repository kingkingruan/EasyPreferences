package com.kingkingduanduan.easypreferences.compiler.method;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class AllMethod extends AbstractMethod {
    private String methodName;
    private TypeMirror returnType;

    public AllMethod(ExecutableElement executableElement) {
        super(executableElement);
    }

    @Override
    public String getMapKey() {
        return "all";
    }

    @Override
    protected boolean checkElement(ExecutableElement executableElement) {
        methodName = executableElement.getSimpleName().toString();
        returnType = executableElement.getReturnType();
        List<? extends VariableElement> params = executableElement.getParameters();
        if (params != null && params.size() > 0) {
            throw new IllegalArgumentException(methodName + " is get all method, need not params");
        }
        returnType = executableElement.getReturnType();
        if (!"java.util.Map<java.lang.String,?>".equals(returnType.toString()))
            throw new IllegalArgumentException("getAll method only return Map<String,?>");
        return true;
    }

    @Override
    protected void parseElement(ExecutableElement executableElement) {

    }

    @Override
    public MethodSpec generateMethodSpec() {
        return MethodSpec.methodBuilder(methodName)
                .returns(ClassName.get(returnType))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("return sp.getAll()")
                .build();
    }

    @Override
    public TypeMirror getKeyType() {
        return null;
    }
}
