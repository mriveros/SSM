package com.stp.ssm.Util;

import org.json.JSONObject;

import java.lang.reflect.Field;

public class ObjectToJson {

    public static String getJsonFromObject(Object object) {
        JSONObject jsonObject = new JSONObject();
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                jsonObject.put(field.getName(), field.get(object));
            } catch (Exception e) {
                e.printStackTrace();
            }
            field.setAccessible(false);
        }
        return jsonObject.toString();
    }
}
