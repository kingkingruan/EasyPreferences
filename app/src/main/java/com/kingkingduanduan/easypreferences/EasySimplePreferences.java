package com.kingkingduanduan.easypreferences;

import android.content.Context;

/**
 * Created by ruanjinjing on 2018/3/22.
 */

public class EasySimplePreferences implements SimplePreferences {
    private Context context;

    public EasySimplePreferences(Context context) {
        this.context = context;
    }

    @Override
    public void setCheckVersion(String version) {

    }

    @Override
    public String getCheckVersion() {
        return BuildConfig.VERSION_NAME;
    }
}
