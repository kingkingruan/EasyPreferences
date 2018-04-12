package com.kingkingduanduan.easypreferences.compiler.method;

import com.kingkingduanduan.easypreferences.annotations.Apply;
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

public class SetMethod extends AbstractMethod {
    private String methodName;
    private TypeMirror returnType;
    private Converter converter;
    private TypeName setType;
    private Apply apply;

    SetMethod(ExecutableElement executableElement) {
        super(executableElement);
    }

    @Override
    protected boolean checkElement(ExecutableElement executableElement) {
        methodName = executableElement.getSimpleName().toString();
        List<? extends VariableElement> params = executableElement.getParameters();
        if (params == null || params.size() != 1) {
            throw Utils.getIllegalArguentException("%s should haa one param", methodName);
        }
        apply = executableElement.getAnnotation(Apply.class);
        returnType = executableElement.getReturnType();
        return isValidReturnType(returnType);
    }

    @Override
    protected void parseElement(ExecutableElement executableElement) {
        Key keyAnno = executableElement.getAnnotation(Key.class);
        if (keyAnno != null) {
            key = keyAnno.value();
            if (Utils.isEmpty(key)) {
                throw Utils.getIllegalArguentException("%s @Key's value can not be empty", methodName);
            }
        } else {
            key = methodName.substring(3).toLowerCase();
        }
        setType = ClassName.get(executableElement.getParameters().get(0).asType());
        converter = executableElement.getAnnotation(Converter.class);
        if (!Utils.isBasicType(setType) && converter == null) {
            throw Utils.getIllegalArguentException("%s saves custom type and should has @Converter", methodName);
        }
    }

    @Override
    public MethodSpec generateMethodSpec() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(methodName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(returnType))
                .addParameter(setType, "value");
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
        if (converter != null) {
            try {
                Type converterValue = converter.value();
                builder.addStatement("$T convertValue=new $N().convertToString(value)",
                        ClassName.get("java.lang", "String"),
                        converterValue.getTypeName());
                builder.addStatement("$N sp.edit().put$N($S,convertValue).$N()",
                        returnKey, Utils.getPutType(setType), key, action);
            } catch (MirroredTypeException mte) {
                DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
                TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
                String qualifiedSuperClassName = classTypeElement.getQualifiedName().toString();
                builder.addStatement("$T convertValue=new $N().convertToString(value)",
                        ClassName.get("java.lang", "String"),
                        qualifiedSuperClassName);
                builder.addStatement("$N sp.edit().put$N($S,convertValue).$N()",
                        returnKey, Utils.getPutType(setType), key, action);
            }

        } else {
            builder.addStatement("$N sp.edit().put$N($S,value).$N()",
                    returnKey, Utils.getPutType(setType), key, action);
        }
        return builder.build();
    }

    @Override
    public String getMapKey() {
        return "set" + key;
    }

    /**
     * set method only support return void or boolean
     *
     * @param returnType
     * @return
     */
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

    @Override
    public TypeMirror getKeyType() {
        List<? extends VariableElement> params = executableElement.getParameters();
        return params.get(0).asType();
    }
}
