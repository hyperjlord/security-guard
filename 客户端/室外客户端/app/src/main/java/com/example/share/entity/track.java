package com.example.share.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class track {
    public double latitude;
    public double longitude;
    public long time;
    public boolean isInBound;
    public boolean isDangerous;

    public static String toJsonObjectString(track trackEntity) throws JSONException {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("latitude",trackEntity.latitude);
        jsonObject.put("longitude",trackEntity.longitude);
        jsonObject.put("time",trackEntity.time);
        jsonObject.put("isInBound",trackEntity.isInBound);
        jsonObject.put("isDangerous",trackEntity.isDangerous);

        return jsonObject.toString();
    }


    public static JSONObject toJsonObjectEntity(track trackEntity) throws JSONException {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("latitude",trackEntity.latitude);
        jsonObject.put("longitude",trackEntity.longitude);
        jsonObject.put("time",trackEntity.time);
        jsonObject.put("isInBound",trackEntity.isInBound);
        jsonObject.put("isDangerous",trackEntity.isDangerous);

        return jsonObject;
    }
}
