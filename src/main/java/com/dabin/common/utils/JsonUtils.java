package com.dabin.common.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: 程序员大彬
 * @time: 2021-11-06 17:04
 */
public class JsonUtils {

    // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static Logger log = LoggerFactory.getLogger(JsonUtils.class);

    /**
     * 把对象转换为json数据
     *
     * @param obj
     * @return 2018年5月7日  下午5:27:16
     */
    public static String objectToJson(Object obj) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        try {
            String json = gson.toJson(obj);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将Object类型的map转换成String类型
     *
     * @param map
     * @return
     */
    public static Map<String, String> mapToMap(Map<String, Object> map) {
        Map<String, String> returnMap = new HashMap<>();
        for (String key : map.keySet()) {
            returnMap.put(key, String.valueOf(map.get(key)));
        }
        return returnMap;
    }

    /**
     * 任意类型转换成Map
     *
     * @return
     */
    public static Map<String, String> object2Map(Object obj) {
        Map<String, String> hashMap = new HashMap();
        try {
            Class c = obj.getClass();
            Method m[] = c.getDeclaredMethods();
            for (int i = 0; i < m.length; i++) {
                if (m[i].getName().indexOf("get") == 0) {
                    // 得到Map的key
                    String suffixKey = m[i].getName().substring(4);
                    String prefixKey = m[i].getName().substring(3, 4).toLowerCase();
                    hashMap.put(prefixKey + suffixKey, String.valueOf(m[i].invoke(obj, new Object[0])));
                }
            }
        } catch (Throwable e) {
            log.error(e.getMessage());
        }
        return hashMap;
    }

    /**
     * JSON 转 ArrayList
     */
    public static <T> ArrayList<T> jsonArrayToArrayList(String jsonArray, Type clazz) {

        Gson gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .serializeNulls()
                .create();
        ArrayList<T> list = null;
        try {

            list = gson.fromJson(jsonArray, clazz);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 把json转换为map类型的数据
     *
     * @param json
     * @return
     */
    public static Map<String, Object> jsonToMap(String json) {

        Gson gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .serializeNulls()
                .create();
        Map<String, Object> map = null;
        try {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();

            map = gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 将json结果集转化为对象
     *
     * @param jsonData
     * @param beanType
     * @param <T>
     * @return
     */
    public static <T> T jsonToPojo(String jsonData, Class<T> beanType) {
        try {
            T t = MAPPER.readValue(jsonData, beanType);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将json数据转换成pojo对象list
     *
     * @param jsonData
     * @param beanType
     * @return
     */
    public static <T> List<T> jsonToList(String jsonData, Class<T> beanType) {
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
        try {
            List<T> list = MAPPER.readValue(jsonData, javaType);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 将任意pojo转化成map
     *
     * @param t pojo对象
     * @return
     */
    public static <T> Map<String, Object> pojoToMap(T t) {
        Map<String, Object> result = new HashMap<String, Object>();
        Method[] methods = t.getClass().getMethods();
        try {
            for (Method method : methods) {
                Class<?>[] paramClass = method.getParameterTypes();
                // 如果方法带参数，则跳过
                if (paramClass.length > 0) {
                    continue;
                }
                String methodName = method.getName();
                if (methodName.startsWith("get")) {
                    Object value = method.invoke(t);
                    result.put(methodName, value);
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return result;
    }

}
