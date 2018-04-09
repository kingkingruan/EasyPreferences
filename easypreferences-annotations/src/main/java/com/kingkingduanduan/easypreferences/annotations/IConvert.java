package com.kingkingduanduan.easypreferences.annotations;

public interface IConvert<T> {

    String convertToString(T value);

    T convertFromString(String value);
}
