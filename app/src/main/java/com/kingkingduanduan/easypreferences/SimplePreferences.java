package com.kingkingduanduan.easypreferences;

import com.kingkingduanduan.easypreferences.annotations.Preferences;

/**
 * Created by ruanjinjing on 2018/3/22.
 */

@Preferences
public interface SimplePreferences {
    void setCheckVersion(String version);

    String getCheckVersion();
}
