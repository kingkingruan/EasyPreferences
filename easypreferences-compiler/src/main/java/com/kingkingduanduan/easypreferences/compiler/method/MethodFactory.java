package com.kingkingduanduan.easypreferences.compiler.method;

import com.kingkingduanduan.easypreferences.annotations.Clear;

import javax.lang.model.element.ExecutableElement;

public class MethodFactory {
    private static final String SET = "set";
    private static final String GET = "get";

    public static AbstractMethod parseMethod(ExecutableElement executableElement) {
        AbstractMethod method = null;
        String methodName = executableElement.getSimpleName().toString();
        Clear clear = executableElement.getAnnotation(Clear.class);
        if (clear != null) {
            method = new ClearMethod(executableElement);
        } else {
            if (methodName.startsWith(SET)) {
                method = new SetMethod(executableElement);
            } else if (methodName.startsWith(GET)) {
                method = new GetMethod(executableElement);
            } else {
                throw new IllegalArgumentException(methodName + "can't parse");
            }
        }
        return method;
    }
}
