package com.kingkingduanduan.easypreferences.compiler.method;

import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

public abstract class AbstractMethod {
    protected String key;
    protected ExecutableElement executableElement;

    public AbstractMethod(ExecutableElement executableElement) {
        this.executableElement = executableElement;
        if (checkElement(executableElement)) {
            parseElement(executableElement);
        }
    }

    public String getKey() {
        return key;
    }

    public abstract String getMapKey();

    protected abstract boolean checkElement(ExecutableElement executableElement);

    /**
     * 解析方法，获取相关的参数，返回值，注解
     *
     * @param executableElement
     */
    protected abstract void parseElement(ExecutableElement executableElement);

    /**
     * 生成最终的MethodSpec用于JavaPoet生成类
     *
     * @return
     */
    public abstract MethodSpec generateMethodSpec();

    public ExecutableElement getExecutableElement() {
        return executableElement;
    }

    public abstract TypeMirror getKeyType();
}
