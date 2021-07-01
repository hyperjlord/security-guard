package com.example.share.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class boundary {
    public double latitude;
    public double longitude;
    public double value;
    public int untraveled_weeks;
    public boolean isEdge;

    public boundary(double latitude,double longitude,double value,int untraveled_weeks,boolean isEdge)
    {
        this.latitude=latitude;
        this.longitude=longitude;
        this.value=value;
        this.untraveled_weeks=untraveled_weeks;
        this.isEdge=isEdge;
    }
    public static JSONObject toJsonObjectEntity(boundary boundaryEntity) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("latitude", boundaryEntity.latitude);
        jsonObject.put("longitude", boundaryEntity.longitude);
        jsonObject.put("value", boundaryEntity.value);
        jsonObject.put("untraveled_weeks", boundaryEntity.untraveled_weeks);
        jsonObject.put("isEdge", boundaryEntity.isEdge);
        return jsonObject;
    }

}