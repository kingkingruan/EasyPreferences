package com.kingkingduanduan.easypreferences.annotations;

public interface IConverter<T> {

    String convertToString(T value);

    T convertFromString(String value);
}
