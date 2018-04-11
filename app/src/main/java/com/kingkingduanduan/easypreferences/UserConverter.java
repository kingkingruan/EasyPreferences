package com.kingkingduanduan.easypreferences;

import com.google.gson.Gson;
import com.kingkingduanduan.easypreferences.annotations.IConvert;

public class UserConverter implements IConvert<User> {
    @Override
    public String convertToString(User value) {
        return new Gson().toJson(value);
    }

    @Override
    public User convertFromString(String value) {
        return new Gson().fromJson(value, User.class);
    }
}
