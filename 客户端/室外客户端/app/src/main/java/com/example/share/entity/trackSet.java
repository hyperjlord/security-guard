package com.example.share.entity;

import com.amap.api.maps.model.LatLng;

import org.json.JSONArray;

import java.util.List;

/**
 * @author Rex
 */
public class trackSet {
    public JSONArray track_set_json;
    public long start_time;
    public long end_time;
    public int account_id;

    public static JSONArray trackSetToJson(List<LatLng> latLngs)
    {
        return null;
    }
}


