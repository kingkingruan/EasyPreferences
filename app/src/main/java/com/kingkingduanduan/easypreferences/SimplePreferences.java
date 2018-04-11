package com.kingkingduanduan.easypreferences;

import android.support.annotation.CallSuper;

import com.kingkingduanduan.easypreferences.annotations.All;
import com.kingkingduanduan.easypreferences.annotations.Apply;
import com.kingkingduanduan.easypreferences.annotations.Clear;
import com.kingkingduanduan.easypreferences.annotations.Converter;
import com.kingkingduanduan.easypreferences.annotations.Default;
import com.kingkingduanduan.easypreferences.annotations.Key;
import com.kingkingduanduan.easypreferences.annotations.Preferences;
import com.kingkingduanduan.easypreferences.annotations.Remove;

import java.util.Map;
import java.util.Set;

/**
 * Created by ruanjinjing on 2018/3/22.
 */

@Preferences("simple")
public interface SimplePreferences {

    void setInt(int version);

    int getInt();

    void setString(String value);

    String getString();

    void setBoolean(Boolean value);

    Boolean getBoolean();

    void setLong(long value);

    Long getLong();

    void setFloat(Float value);

    float getFloat();

    @Apply
    void setSetString(Set<String> stringSet);

    Set<String> getSetString();

    @Clear
    void clear();

    @Default("hello")
    String getWelcome();

    @Default("0")
    int getDafaultInt();

    @Default("0.0")
    float getDefaultFloat();

    @Default("10")
    long getDefaultLong();

    @Default({"hh", "gg"})
    Set<String> getDefaultSetString();

    @Key("defaultUser")
    @Converter(UserConverter.class)
    void setUser(User user);

    @Converter(UserConverter.class)
    User getUser();

    @Remove("string")
    void removeString();

    @Remove()
    @Key("long")
    boolean removeLong();

    @All
    Map<String,?> getAll();
}
