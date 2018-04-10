package com.kingkingduanduan.easypreferences.compiler.method;

import com.kingkingduanduan.easypreferences.annotations.Converter;
import com.kingkingduanduan.easypreferences.annotations.Key;
import com.kingkingduanduan.easypreferences.compiler.Utils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.lang.reflect.Type;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class GetMethod extends AbstractMethod {
    private Converter converter;
    private TypeMirror returnType;

    GetMethod(ExecutableElement executableElement) {
        super(executableElement);
    }

    @Override
    protected boolean checkElement(ExecutableElement executableElement) {
        List<? extends VariableElement> params = executableElement.getParameters();
        if (params == null || params.size() != 0) {
            throw new IllegalArgumentException("no param");
        }
        TypeMirror typeMirror = executableElement.getReturnType();
        if (typeMirror.getKind() == TypeKind.VOID) {
            throw new IllegalArgumentException(executableElement.getSimpleName() + " returns void");
        }
        converter = executableElement.getAnnotation(Converter.class);
        returnType = executableElement.getReturnType();
        if (!Utils.isBasicType(ClassName.get(returnType)) && converter == null) {
            throw new IllegalArgumentException(executableElement.getSimpleName() + "needs converter");
        }
        return true;
    }

    @Override
    protected void parseElement(ExecutableElement executableElement) {
        String methodName = executableElement.getSimpleName().toString();
        Key keyAnno = executableElement.getAnnotation(Key.class);
        if (keyAnno != null) {
            key = keyAnno.value();
            if (Utils.isEmpty(key)) {
                throw new IllegalArgumentException(methodName + " Key's value can't be empty");
            }
        } else {
            key = methodName.substring(3).toLowerCase();
        }
    }

    @Override
    public MethodSpec generateMethodSpec() {
        TypeName getType = ClassName.get(executableElement.getReturnType());
        MethodSpec.Builder builder = MethodSpec.
                methodBuilder(executableElement.getSimpleName().toString())
                .addAnnotation(Override.class)
                .returns(getType)
                .addModifiers(Modifier.PUBLIC);
        if (converter != null) {
            try {
                Type converterValue = converter.value();
                builder.addStatement("$T originValue = sp.get$N($S,$N)",
                        ClassName.get("java.lang", "String"),
                        Utils.getPutType(getType),
                        key,
                        Utils.getDefaultValue(executableElement));
                builder.addStatement("return new $N().convertFromString(originValue)",
                        converterValue.getTypeName());
            } catch (MirroredTypeException mte) {
                DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
                TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
                String qualifiedSuperClassName = classTypeElement.getQualifiedName().toString();
                builder.addStatement("$T originValue = sp.get$N($S,$N)",
                        ClassName.get("java.lang", "String"),
                        Utils.getPutType(getType),
                        key,
                        Utils.getDefaultValue(executableElement));
                builder.addStatement("return new $N().convertFromString(originValue)",
                        qualifiedSuperClassName);
            }

        } else {
            builder.addStatement("return sp.get$N($S,$N)",
                    Utils.getPutType(getType),
                    key,
                    Utils.getDefaultValue(executableElement));
        }
        return builder.build();
    }

    @Override
    public String getMapKey() {
        return "get" + key;
    }

    @Override
    public TypeMirror getKeyType() {
        return returnType;
    }
}
