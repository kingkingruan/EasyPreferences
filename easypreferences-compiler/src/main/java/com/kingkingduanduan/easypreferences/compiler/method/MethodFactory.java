package com.kingkingduanduan.easypreferences.compiler.method;

import com.kingkingduanduan.easypreferences.annotations.Clear;
import com.kingkingduanduan.easypreferences.annotations.Remove;

import javax.lang.model.element.ExecutableElement;

public class MethodFactory {
    private static final String SET = "set";
    private static final String GET = "get";

    public static AbstractMethod parseMethod(ExecutableElement executableElement) {
        AbstractMethod method;
        String methodName = executableElement.getSimpleName().toString();
        Clear clear = executableElement.getAnnotation(Clear.class);
        Remove remove = executableElement.getAnnotation(Remove.class);
        if (clear != null) {
            method = new ClearMethod(executableElement);
        } else if (remove != null) {
            method = new RemoveMethod(executableElement);
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
