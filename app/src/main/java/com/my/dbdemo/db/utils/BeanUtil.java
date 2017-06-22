package com.my.dbdemo.db.utils;

import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by YJH on 2017/3/28 15:32.
 */

public final class BeanUtil {

    private static String TAG = "BeanUtil";

    /**
     * 返回 Field 对象的一个数组，
     * 这些对象反映此 Class 对象所表示的类或接口所声明的所有字段。
     * 与 getFields(clazz, "");等价
     *
     * @param clazz Class 对象。
     * @return Field 对象数组
     */
    public static Field[] getFields(Class<?> clazz) {
        return getFields(clazz, "");
    }

    /**
     * 返回 Field 对象的一个数组，这些对象反映此 Class 对象所表示的类或接口所声明的所有字段。
     *
     * @param clazz   Class 对象。
     * @param exclude 排除字段
     * @return Field对象数组
     */
    public static Field[] getFields(Class<?> clazz, String exclude) {
        List<Field> fs = new ArrayList<>();
        Class searchType = clazz.getSuperclass();
        if (!Object.class.equals(searchType) && searchType != null) {
            fs.addAll(Arrays.asList(getFields(searchType, exclude)));
        }

        Field[] all = clazz.getDeclaredFields();
        for (Field field : all) {
            String fName = field.getName();
            if (!TextUtils.isEmpty(exclude) && exclude.contains(fName)) {
                continue;
            }
            fs.add(field);
        }
        return fs.toArray(new Field[fs.size()]);
    }

    /**
     * 反射获取类中字段对应的值
     *
     * @param target    Object
     * @param fieldName String
     * @return Object
     */
    public static Object getFieldValue(Object target, String fieldName) {
        if (null == target || TextUtils.isEmpty(fieldName)) {
            throw new NullPointerException("target或者fieldName不能为空");
        }
        Class<?> klass = target.getClass();
        try {
            Field f = getField(klass, fieldName);
            f.setAccessible(true);
            return f.get(target);
        } catch (Exception e) {
            Log.d(TAG, "获取字段值异常", e);
            return null;
        }
    }

    /**
     * 返回字段的声明类型。
     *
     * @param clazz Class 对象
     * @param name  所表示字段的字符串
     * @return Class对象
     */
    public static Field getField(Class<?> clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            if ("java.lang.Object".equals(clazz.getSuperclass().getName())) {
                Log.e(TAG, "not find field:" + name, e);
                throw new RuntimeException("没有发现这样的字段");
            }
            return getField(clazz.getSuperclass(), name);
        }
    }

    /**
     * 创建clazz类的对象实例,params是构造函数的参数
     *
     * @param clazz  class对象
     * @param params 构造参数
     * @return 该clazz的实例
     */
    public static <T> T instanceClass(Class<T> clazz, Object... params) {
        Class<?>[] cls = new Class<?>[params.length];
        for (int i = 0; i < cls.length; ++i) {
            cls[i] = params[i].getClass();
        }
        Constructor<T> cons = null;
        try {
            cons = clazz.getConstructor(cls);
            return cons.newInstance(params);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setFieldValue(Object target, String fieldName, Object value) {
        if (null == target || TextUtils.isEmpty(fieldName)) {
            throw new NullPointerException("target和fieldName必须都不为空");
        }
        Class<?> klass = target.getClass();
        try {
            Field f = getField(klass, fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "没有发现" + fieldName + "字段", e);
        }
    }
}
