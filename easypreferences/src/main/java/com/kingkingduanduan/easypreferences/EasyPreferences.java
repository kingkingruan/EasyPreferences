package com.kingkingduanduan.easypreferences;

import android.content.Context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by ruanjinjing on 2018/3/22.
 */

public class EasyPreferences {

    public static <T> T getSharedPreferences(Context context, Class<T> t) {
        T instance = null;
        String simpleName = t.getSimpleName();
        try {
            String canName = "com.kingkingduanduan.easypreferences.Easy" + simpleName;
            Class imple = t.getClassLoader().loadClass(canName);
            Constructor<T> constructor = imple.getConstructor(Context.class);
            instance = constructor.newInstance(context);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return instance;
        }
    }
}
