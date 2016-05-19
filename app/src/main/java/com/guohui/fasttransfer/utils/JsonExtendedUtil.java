package com.guohui.fasttransfer.utils;

import com.guohui.fasttransfer.utils.annotation.JsonListParam;
import com.guohui.fasttransfer.utils.annotation.JsonParam;
import com.guohui.fasttransfer.utils.annotation.Jsonable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;


/**
 * Created by Dikaros on 2016/5/16.
 */
public class JsonExtendedUtil {
    public JsonExtendedUtil() {
    }

    public static String generateJson(Object object) throws Exception {
        JSONStringer stringer = new JSONStringer();
        stringer.array();
        generateObjectMessage(object, stringer);
        stringer.endArray();
        return stringer.toString();
    }

    private static void generateObjectMessage(Object object, JSONStringer stringer) throws Exception {
        if (!(object instanceof Jsonable)) {
            throw new Exception("不支持的类型格式，是否忘记了实现JsonAble接口");
        } else {
            stringer.object();
            Class clazz = object.getClass();
            Field[] fields = clazz.getDeclaredFields();
            Field[] var7 = fields;
            int var6 = fields.length;

            for (int var5 = 0; var5 < var6; ++var5) {
                Field field = var7[var5];
                Method method;
                Object value;
                if (field.isAnnotationPresent(JsonParam.class)) {
                    JsonParam var14 = (JsonParam) field.getAnnotation(JsonParam.class);
                    stringer.key(var14.name());
                    method = getFieldMethod(clazz, field);
                    value = method.invoke(object, new Object[0]);
                    stringer.value(value);
                } else if (field.isAnnotationPresent(JsonListParam.class)) {
                    JsonListParam jsonListParam = (JsonListParam) field.getAnnotation(JsonListParam.class);
                    method = getFieldMethod(clazz, field);
                    value = method.invoke(object, new Object[0]);
                    if (value != null) {
                        Object listItem;
                        if (jsonListParam.classType() == JsonListParam.TYPE.ARRAY) {
                            Array.getLength(value);
                            stringer.key(jsonListParam.name());
                            stringer.array();

                            for (int var15 = 0; var15 < Array.getLength(value); ++var15) {
                                listItem = Array.get(value, var15);
                                if (listItem instanceof Jsonable) {
                                    generateObjectMessage(listItem, stringer);
                                } else {
                                    if (!isBaseType(listItem)) {
                                        throw new Exception("集合元素类型不支持，请将集合内元素实现JsonAble接口");
                                    }

                                    stringer.value(listItem);
                                }
                            }

                            stringer.endArray();
                        } else if (jsonListParam.classType() == JsonListParam.TYPE.LIST) {
                            stringer.key(jsonListParam.name());
                            stringer.array();
                            List list = (List) value;
                            Iterator var13 = list.iterator();

                            while (var13.hasNext()) {
                                listItem = var13.next();
                                if (listItem instanceof Jsonable) {
                                    generateObjectMessage(listItem, stringer);
                                } else {
                                    if (!isBaseType(listItem)) {
                                        throw new Exception("集合元素类型不支持，请将集合内元素实现JsonAble接口");
                                    }

                                    stringer.value(listItem);
                                }
                            }

                            stringer.endArray();
                        }
                    }
                }
            }

            stringer.endObject();
        }
    }

    private static boolean isBaseType(Object obj) {
        boolean result = false;
        if (obj instanceof Integer || obj instanceof Byte || obj instanceof Double || obj instanceof Boolean || obj instanceof String || obj instanceof Short || obj instanceof Long || obj instanceof Character || obj instanceof Float) {
            result = true;
        }

        return result;
    }

    private static Method getFieldMethod(Class clazz, Field field) throws Exception {
        String fieldName = field.getName();
        fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        Method method = null;

        try {
            if (field.getType() == Boolean.class) {
                method = clazz.getMethod("is" + fieldName, (Class[]) null);
            } else {
                method = clazz.getMethod("get" + fieldName, (Class[]) null);
            }

            return method;
        } catch (NoSuchMethodException var5) {
            throw new Exception("不是标准的JavaBean,注意JavaBean的格式");
        }
    }

    private static Method setFieldMethod(Class clazz, Field field) throws Exception {
        String fieldName = field.getName();
        fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        Method method = null;

        try {
            method = clazz.getMethod("set" + fieldName, new Class[]{field.getType()});
            return method;
        } catch (NoSuchMethodException var5) {
            throw new Exception("不是标准的JavaBean,注意JavaBean的格式");
        }
    }

    public static Object compileJson(Class clazz, String jsonFile) throws Exception {
        JSONArray array = new JSONArray(jsonFile);
        Object object = null;
        if (array.length() > 0) {
            JSONObject jsonObject = array.getJSONObject(0);
            object = clazz.newInstance();
            getObjectFromJsonFile(object, jsonObject.toString());
        }

        return object;
    }

    private static void getObjectFromJsonFile(Object jsonable, String jsonFile) throws Exception {
        if (!(jsonable instanceof Jsonable)) {
            throw new Exception("不支持的类型格式，是否忘记了实现JsonAble接口");
        } else {
            Class clazz = jsonable.getClass();
            JSONObject obj = new JSONObject(jsonFile);
            Field[] fields = clazz.getDeclaredFields();
            Field[] var8 = fields;
            int var7 = fields.length;

            for (int var6 = 0; var6 < var7; ++var6) {
                Field field = var8[var6];
                String keyName;
                Method method;
                if (field.isAnnotationPresent(JsonParam.class)) {
                    JsonParam var20 = (JsonParam) field.getAnnotation(JsonParam.class);
                    keyName = var20.name();
//                    Object var21 = obj.get(keyName);
                    method = setFieldMethod(clazz, field);
                    Class[] parameterClass = method.getParameterTypes();
//                    if (var21 instanceof JSONObject){
//                        method.invoke(jsonable, new Object[]{var21.toString()});
//                    }else {
//                        method.invoke(jsonable,new Object[]{var21});
//                    }

                    if (parameterClass[0]==Long.class){
                        method.invoke(jsonable,new Object[]{obj.getLong(keyName)});
                    }else if (parameterClass[0]==Integer.class){
                        method.invoke(jsonable,new Object[]{obj.getInt(keyName)});

                    }else if(parameterClass[0]==String.class){
                        method.invoke(jsonable,new Object[]{obj.getString(keyName)});

                    }else {
                        method.invoke(jsonable,new Object[]{obj.get(keyName)});

                    }
                } else if (field.isAnnotationPresent(JsonListParam.class)) {
                    JsonListParam jsonListParam = (JsonListParam) field.getAnnotation(JsonListParam.class);
                    keyName = jsonListParam.name();
                    JSONArray jArray = null;

                    try {
                        jArray = obj.getJSONArray(keyName);
                    } catch (Exception var19) {
                        continue;
                    }

                    if (!field.getType().equals(List.class) && !field.getType().isArray()) {
                        throw new Exception("类型错误，类型不是List的实例");
                    }

                    method = setFieldMethod(clazz, field);
                    JsonListParam.TYPE classType = jsonListParam.classType();
                    int i;
                    if (classType == JsonListParam.TYPE.LIST) {
                        ArrayList var22 = new ArrayList();

                        for (i = 0; i < jArray.length(); ++i) {
                            JSONObject var23 = jArray.getJSONObject(i);
                            Class var24 = jsonListParam.contentClassName();
                            Object var25 = var24.newInstance();
                            if (var25 instanceof Jsonable) {
                                getObjectFromJsonFile(var25, var23.toString());
                                var22.add(var25);
                            } else if (isBaseType(var25)) {
                                var22.add(jArray.get(i));
                            }
                        }

                        method.invoke(jsonable, new Object[]{var22});
                    } else if (classType == JsonListParam.TYPE.ARRAY) {
                        Object items = Array.newInstance(jsonListParam.contentClassName(), jArray.length());

                        for (i = 0; i < jArray.length(); ++i) {
                            Class itemClass = jsonListParam.contentClassName();
                            Object itemObject = itemClass.newInstance();
                            if (itemObject instanceof Jsonable) {
                                JSONObject item = jArray.getJSONObject(i);
                                getObjectFromJsonFile(itemObject, item.toString());
                                Array.set(items, i, itemObject);
                            } else if (isBaseType(itemObject)) {
                                Array.set(items, i, jArray.get(i));
                            }
                        }

                        System.out.println("method--->" + method.getName() + ":" + method.getParameterTypes()[0]);
                        method.invoke(jsonable, new Object[]{items});
                    }
                }
            }

        }
    }

    public static long getLongParam(String jsonFile, String param) {
        long result = 0L;

        try {
            JSONArray array = new JSONArray(jsonFile);
            if (array.length() > 0) {
                JSONObject obj = array.getJSONObject(0);
                result = obj.getLong(param);
            }
        } catch (JSONException var7) {
            var7.printStackTrace();
        }

        return result;
    }

    public static boolean getBooleanParam(String jsonFile, String param) {
        boolean result = false;

        try {
            JSONArray array = new JSONArray(jsonFile);
            if (array.length() > 0) {
                JSONObject obj = array.getJSONObject(0);
                result = obj.getBoolean(param);
            }
        } catch (JSONException var6) {
            var6.printStackTrace();
        }

        return result;
    }

    public static String getParam(String jsonFile, String param) {
        String result = null;

        try {
            JSONArray array = new JSONArray(jsonFile);
            if (array.length() > 0) {
                JSONObject obj = array.getJSONObject(0);
                result = obj.getString(param);
            }
        } catch (JSONException var6) {
            var6.printStackTrace();
        }

        return result;
    }

    public static int getIntParam(String jsonFile, String param) {
        int result = -1;

        try {
            JSONArray array = new JSONArray(jsonFile);
            if (array.length() > 0) {
                JSONObject obj = array.getJSONObject(0);
                result = obj.getInt(param);
            }
        } catch (JSONException var6) {
            var6.printStackTrace();
        }

        return result;
    }

    public static String constructMapJson(HashMap map) {
        Set set = map.keySet();
        JSONStringer jsonStringer = new JSONStringer();

        try {
            jsonStringer.array();
            jsonStringer.object();
            Iterator e = set.iterator();

            while (e.hasNext()) {
                String key = (String) e.next();
                jsonStringer.key(key);
                jsonStringer.value(map.get(key));
            }

            jsonStringer.endObject();
            jsonStringer.endArray();
        } catch (JSONException var5) {
            var5.printStackTrace();
        }

        return jsonStringer.toString();
    }
}
