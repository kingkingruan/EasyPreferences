package com.kingkingduanduan.easypreferences;

import android.content.Context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ruanjinjing on 2018/3/22.
 */

public class EasyPreferences {
    private static Map<String, Object> instanceBuffer = new HashMap();

    public static <T> T getSharedPreferences(Context context, Class<T> t) {
        T instance = null;
        String canonicalName = t.getCanonicalName();
        Object bufferValue = instanceBuffer.get(canonicalName);
        if (bufferValue != null) {
            return (T) bufferValue;
        }
        try {
            String simpleName = t.getSimpleName();
            String canName = "com.kingkingduanduan.preferences.Easy" + simpleName;
            Class imple = t.getClassLoader().loadClass(canName);
            Constructor<T> constructor = imple.getConstructor(Context.class);
            instance = constructor.newInstance(context);
            instanceBuffer.put(canonicalName, instance);
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
