package com.kingkingduanduan.easypreferences.compiler;

import com.kingkingduanduan.easypreferences.annotations.Default;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.MirroredTypeException;

public class Utils {

    public static boolean isEmpty(String value) {
        if (value == null || value.length() == 0) {
            return true;
        }
        return false;
    }

    public static String getPutType(TypeName typeName) {
        String type;
        TypeName unboxed = typeName.isBoxedPrimitive() ? typeName.unbox() : typeName;
        if (unboxed == TypeName.BOOLEAN) {
            type = "Boolean";
        } else if (unboxed == TypeName.FLOAT) {
            type = "Float";
        } else if (unboxed == TypeName.INT) {
            type = "Int";
        } else if (unboxed == TypeName.LONG) {
            type = "Long";
        } else if ("java.util.Set<java.lang.String>".equals(unboxed.toString())) {
            type = "StringSet";
        } else {
            type = "String";
        }
        return type;
    }

    public static boolean isBasicType(TypeName typeName) {
        TypeName unboxed = typeName.isBoxedPrimitive() ? typeName.unbox() : typeName;
        if (unboxed == TypeName.BOOLEAN
                || unboxed == TypeName.FLOAT
                || unboxed == TypeName.INT
                || unboxed == TypeName.LONG
                || "java.util.Set<java.lang.String>".equals(unboxed.toString())
                || "java.lang.String".equals(unboxed.toString())) {
            return true;
        }
        return false;
    }

    public static String getDefaultValue(ExecutableElement executableElement) {
        TypeName typeName = ClassName.get(executableElement.getReturnType());
        Default anno = executableElement.getAnnotation(Default.class);
        String[] defaultValue = null;
        try {
            if (anno != null) {
                defaultValue = anno.value();
            }
        } catch (MirroredTypeException mte) {
            mte.printStackTrace();
        }
        String type = "null";
        TypeName unboxed = typeName.isBoxedPrimitive() ? typeName.unbox() : typeName;
        if (unboxed == TypeName.BOOLEAN) {
            if (defaultValue != null && defaultValue.length > 0) {
                type = defaultValue[0];
            } else
                type = "false";
        } else if (unboxed == TypeName.FLOAT) {
            if (defaultValue != null && defaultValue.length > 0) {
                type = defaultValue[0].endsWith("f") ? defaultValue[0] : defaultValue[0] + "f";
            } else {
                type = "0.0f";
            }
        } else if (unboxed == TypeName.INT) {
            if (defaultValue != null && defaultValue.length > 0) {
                type = defaultValue[0];
            } else {
                type = "0";
            }
        } else if (unboxed == TypeName.LONG) {
            if (defaultValue != null && defaultValue.length > 0) {
                type = defaultValue[0];
            } else {
                type = "0l";
            }
        } else if ("java.lang.String".equals(unboxed.toString())) {
            if (defaultValue != null && defaultValue.length > 0) {
                type = "\"" + defaultValue[0] + "\"";
            } else {
                type = "\"\"";
            }
        } else if ("java.util.Set<java.lang.String>".equals(unboxed.toString())) {
            if (defaultValue != null && defaultValue.length > 0) {
                StringBuilder builder = new StringBuilder();
                builder.append("new java.util.HashSet<String>(java.util.Arrays.asList(new java.lang.String[]{");
                for (int i = 0; i < defaultValue.length; i++) {
                    builder.append("\"").append(defaultValue[i]).append("\"");
                    if (i != (defaultValue.length - 1)) {
                        builder.append(",");
                    }
                }
                builder.append("}))");
                type = builder.toString();
            } else {
                type = "null";
            }
        }
        return type;
    }
}
