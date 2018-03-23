package com.kingkingduanduan.easypreferences.compiler;

import javax.lang.model.element.ExecutableElement;

/**
 * Created by ruanjinjing on 2018/3/22.
 */

public class KeyElements {
    public String key;
    private ExecutableElement setMethod;
    private ExecutableElement getMethod;

    public void setSetMethod(ExecutableElement setMethod) {
        this.setMethod = setMethod;
    }

    public void setGetMethod(ExecutableElement getMethod) {
        this.getMethod = getMethod;
    }
}
