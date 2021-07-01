package com.example.share.util;

import com.amap.api.maps.model.LatLng;
import com.example.share.entity.track;
import com.example.share.thread.netThread.getAllTrackRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PointUtils {
    public static JSONArray trackPoints=new JSONArray();

    /**
     * 将points直接添加到我们的jsonArray中
     * @param points
     * @param isInBound
     * @param time
     * @throws JSONException
     */
    public static void trackAddPoint(LatLng points,boolean isInBound,long time) throws JSONException {
        track trackEntity=new track();
        trackEntity.latitude=points.latitude;
        trackEntity.longitude=points.longitude;
        trackEntity.time=time;
        //暂定为fasle;
        trackEntity.isInBound=isInBound;
        trackEntity.isDangerous=false;
        trackPoints.put(track.toJsonObjectEntity(trackEntity));
    }

    /**
     * 将一个LatLnglist全部添加到我们的trackPoint中
     * @param points
     * @param isInBound
     * @param time
     * @throws JSONException
     */
    public static void trackAddPoints(List<LatLng> points,boolean isInBound,long time) throws JSONException {
        track trackEntity=new track();
        for(LatLng point:points) {
            trackEntity.latitude = point.latitude;
            trackEntity.longitude = point.longitude;
            trackEntity.time=time;
            //暂定为fasle;
            trackEntity.isInBound = isInBound;
            trackEntity.isDangerous = false;
            trackPoints.put(track.toJsonObjectEntity(trackEntity));
        }
    }

    /**
     * 将trackArray直接填装为JSONObject进行传输
     * @param account_id
     * @return
     * @throws JSONException
     */
    public static JSONObject trackSetToJson(long account_id) throws JSONException {
        long start_time= (long) trackPoints.getJSONObject(0).get("time");
        long end_time=(long)trackPoints.getJSONObject(trackPoints.length()-1).get("time");
        JSONObject res=new JSONObject();
        res.put("account_id",account_id);
        res.put("start_time",start_time);
        res.put("end_time",end_time);
        JSONArray jsonArray=new JSONArray();
        jsonArray=trackPoints;
        res.put("trackSetJson",jsonArray.toString());
        //垃圾回收一下
        trackPoints=null;
        trackPoints=new JSONArray();
        return res;
    }

    /**
     *
     * @return 返回array的取出值
     */
    public static List<LatLng> getListByRes() throws JSONException {
        if(getAllTrackRequest.allTrackArray!=null)
        {
            List<LatLng> points=new ArrayList<>();
            for(int i=0;i<getAllTrackRequest.allTrackArray.length();i++) {
                JSONArray getArray = new JSONArray(getAllTrackRequest.allTrackArray.get(i).toString());
                for(int j=0;j<getArray.length();j++)
                {
                    JSONObject getObject=new JSONObject(getArray.get(j).toString());
                    LatLng latLng=new LatLng(getObject.getDouble("latitude"),getObject.getDouble("longitude"));
                    points.add(latLng);
                }
            }
            return points;
        }
        return null;
    }

    public static String getStartTime() throws JSONException {
        if(getAllTrackRequest.allTrackArray!=null&&getAllTrackRequest.allTrackArray.length()>0) {
            JSONArray getArray = new JSONArray(getAllTrackRequest.allTrackArray.get(0).toString());
            JSONObject getObject=new JSONObject(getArray.get(0).toString());
            long timeStamp=getObject.getLong("time")*1000;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            Date date = new Date(timeStamp);

            return simpleDateFormat.format(date);
        }

        return null;
    }

}
