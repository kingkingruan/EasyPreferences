package com.kingkingduanduan.easypreferences.compiler.method;

import com.kingkingduanduan.easypreferences.annotations.Apply;
import com.kingkingduanduan.easypreferences.annotations.Key;
import com.kingkingduanduan.easypreferences.annotations.Remove;
import com.kingkingduanduan.easypreferences.compiler.Utils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class RemoveMethod extends AbstractMethod {
    private Apply apply;
    private TypeMirror returnType;
    private String methodName;
    private Key keyAnno;
    private Remove remove;

    public RemoveMethod(ExecutableElement executableElement) {
        super(executableElement);
    }

    @Override
    public String getMapKey() {
        return "remove" + key;
    }

    @Override
    protected boolean checkElement(ExecutableElement executableElement) {
        List<? extends VariableElement> params = executableElement.getParameters();
        if (params != null && params.size() > 0) {
            throw new IllegalArgumentException("need zero param");
        }
        methodName = executableElement.getSimpleName().toString();
        remove = executableElement.getAnnotation(Remove.class);
        key = remove.value();
        if (Utils.isEmpty(key)) {
            keyAnno = executableElement.getAnnotation(Key.class);
            if (keyAnno == null) {
                throw new IllegalArgumentException("remove method must have key");
            }
            key = keyAnno.value();
            if (Utils.isEmpty(key)) {
                throw new IllegalArgumentException("remove method must have key");
            }
        }
        apply = executableElement.getAnnotation(Apply.class);
        returnType = executableElement.getReturnType();
        return isValidReturnType(returnType);
    }

    @Override
    protected void parseElement(ExecutableElement executableElement) {

    }

    @Override
    public MethodSpec generateMethodSpec() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
                .returns(ClassName.get(executableElement.getReturnType()))
                .addModifiers(Modifier.PUBLIC);
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
        builder.addStatement("$N sp.edit().remove($S).$N()",
                returnKey, key, action);
        return builder.build();
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
