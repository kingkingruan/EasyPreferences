package com.kingkingduanduan.easypreferences.compiler;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created by ruanjinjing on 2018/3/22.
 */

public class KeyElements {
    public String key;
    private ExecutableElement setMethod;
    private ExecutableElement getMethod;

    public KeyElements(String key) {
        this.key = key;
    }

    public void setSetMethod(ExecutableElement setMethod) {
        if (this.setMethod != null) {
            throw new IllegalArgumentException("");
        }
        //set函数只能一个参数
        List<? extends VariableElement> params = setMethod.getParameters();
        if (params == null || params.size() != 1) {
            throw new IllegalArgumentException("");
        }
        //set函数的参数与get函数返回值判断，两个返回类型必须一致
        if (getMethod != null) {
            TypeMirror getReturnType = getMethod.getReturnType();
            VariableElement variableElement = params.get(0);
            TypeMirror paramType = variableElement.asType();
            if (paramType != getReturnType) {
                throw new IllegalArgumentException("");
            }
        }
        this.setMethod = setMethod;
    }

    public void setGetMethod(ExecutableElement getMethod) {
        if (this.getMethod != null) {
            throw new IllegalArgumentException("");
        }
        this.getMethod = getMethod;
    }
}
