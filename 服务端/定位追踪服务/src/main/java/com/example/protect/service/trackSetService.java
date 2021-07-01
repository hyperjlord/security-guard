package com.example.protect.service;


import com.example.protect.entities.trackSet;
import com.example.protect.entities.trackform;

import java.util.List;

public interface trackSetService {
    public boolean storageTrack(trackform trackform);
    public String getTrackBySeconds(int account_id, long time);
    public List<String> getTrackByMin(int account_id,long time);
    public List<String> getTrackByHour(int account_id,long time);
    public List<String> getTrackByDay(int account_id,long time);


}
