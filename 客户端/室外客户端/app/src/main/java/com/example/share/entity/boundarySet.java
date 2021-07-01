package com.example.share.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rex
 */
public class boundarySet {
    public int account_id;
    public int boundary_id;
    public JSONArray boundary_set_json;
    public static List<boundarySet> boundarySetList=new ArrayList<boundarySet>();

    public JSONArray finishBoundary_set_json() throws JSONException {
        boundary_set_json=new JSONArray();
        boundary boundaryEntity=new boundary(31.288751,121.206068,1,0,true);
        JSONObject jsonObject=boundary.toJsonObjectEntity(boundaryEntity);
        boundary_set_json.put(jsonObject);
        boundaryEntity=new boundary(31.291588, 121.209652,1,0,true);
        jsonObject=boundary.toJsonObjectEntity(boundaryEntity);
        boundary_set_json.put(jsonObject);
        boundaryEntity=new boundary(31.291394,  121.215197,1,0,true);
        jsonObject=boundary.toJsonObjectEntity(boundaryEntity);
        boundary_set_json.put(jsonObject);
        boundaryEntity=new boundary(31.291201,  121.218554,1,0,true);
        jsonObject=boundary.toJsonObjectEntity(boundaryEntity);
        boundary_set_json.put(jsonObject);
        boundaryEntity=new boundary(31.290524,  121.222666,1,0,true);
        jsonObject=boundary.toJsonObjectEntity(boundaryEntity);
        boundary_set_json.put(jsonObject);
        boundaryEntity=new boundary(31.287784,  121.223005,1,0,true);
        jsonObject=boundary.toJsonObjectEntity(boundaryEntity);
        boundary_set_json.put(jsonObject);
        boundaryEntity=new boundary( 31.288203 ,  121.219648,1,0,true);
        jsonObject=boundary.toJsonObjectEntity(boundaryEntity);
        boundary_set_json.put(jsonObject);
        boundaryEntity=new boundary(31.284786 ,  121.219271,1,0,true);
        jsonObject=boundary.toJsonObjectEntity(boundaryEntity);
        boundary_set_json.put(jsonObject);
        boundaryEntity=new boundary(31.282787 ,  121.221874,1,0,true);
        jsonObject=boundary.toJsonObjectEntity(boundaryEntity);
        boundary_set_json.put(jsonObject);

        boundaryEntity=new boundary(31.279218 ,  121.21897,1,0,true);
        jsonObject=boundary.toJsonObjectEntity(boundaryEntity);
        boundary_set_json.put(jsonObject);
        boundaryEntity=new boundary(31.279301 ,  121.211056,1,0,true);
        jsonObject=boundary.toJsonObjectEntity(boundaryEntity);
        boundary_set_json.put(jsonObject);
        boundaryEntity=new boundary(31.281848 ,  121.211691,1,0,true);
        jsonObject=boundary.toJsonObjectEntity(boundaryEntity);
        boundary_set_json.put(jsonObject);
        boundaryEntity=new boundary(31.285146 ,  121.207392,1,0,true);
        jsonObject=boundary.toJsonObjectEntity(boundaryEntity);
        boundary_set_json.put(jsonObject);
        boundaryEntity=new boundary(31.286023 ,  121.206366,1,0,true);
        jsonObject=boundary.toJsonObjectEntity(boundaryEntity);
        boundary_set_json.put(jsonObject);
        boundaryEntity=new boundary(31.286858 ,  121.207636,1,0,true);
        jsonObject=boundary.toJsonObjectEntity(boundaryEntity);
        boundary_set_json.put(jsonObject);
        boundaryEntity=new boundary(31.288528 ,  121.206122,1,0,true);
        jsonObject=boundary.toJsonObjectEntity(boundaryEntity);
        boundary_set_json.put(jsonObject);
        boundaryEntity=new boundary(31.288702 ,  121.206165,1,0,true);
        jsonObject=boundary.toJsonObjectEntity(boundaryEntity);
        boundary_set_json.put(jsonObject);

        return boundary_set_json;
    }

}
