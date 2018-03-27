package com.kingkingduanduan.easypreferences;

import com.kingkingduanduan.easypreferences.annotations.Clear;
import com.kingkingduanduan.easypreferences.annotations.Preferences;

import java.util.Set;

/**
 * Created by ruanjinjing on 2018/3/22.
 */

@Preferences
public interface SimplePreferences {

    void setInt(int version);

    int getInt();

    void setString(String value);

    String getString();

    void setBoolean(boolean value);

    boolean getBoolean();

    void setLong(long value);

    long getLong();

    void setFloat(Float value);

    float getFloat();

    void setSetString(Set<String> stringSet);

    Set<String> getSetString();

    @Clear
    void clear();
}
