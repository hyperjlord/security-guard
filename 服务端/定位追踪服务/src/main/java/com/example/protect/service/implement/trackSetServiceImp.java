package com.example.protect.service.implement;


import com.example.protect.dao.trackSetDao;
import com.example.protect.entities.trackSet;
import com.example.protect.entities.trackform;
import com.example.protect.service.trackSetService;
import com.example.protect.util.RedisUtils;
import com.example.protect.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;



@Service
public class trackSetServiceImp implements trackSetService {

    @Autowired
    private RedisUtils redisUtils;

    //时间工具类
    @Autowired
    private TimeUtils timeUtils;

    private String REDISKEY="share_track";
    private Integer TRACKSIZE=300;
    private int TIME_SIZE=300;
    private int MIN_SIZE=50000;
    private int CLEAR_SIZE=5000;
    private int HOURTIME=7200;
    private int DAYTIME=86400;

    @Resource
    trackSetDao trackSetDaoEntity;

    /**
     * 使用lpop和rpush
     * lGetListSize测长度
     * lGet 参数0-1
     */
    @Override
    public boolean storageTrack(trackform trackFormEntity) {
        //解决办法 内部存一个List
        String listKey=REDISKEY+trackFormEntity.account_id+"list";
        if (redisUtils.lGetListSize(listKey) >= TRACKSIZE) {
            redisUtils.lPop(listKey,0);
        }

        redisUtils.lSet(listKey,trackFormEntity.start_time,CLEAR_SIZE);
        String redisKey=REDISKEY+trackFormEntity.account_id+"_"+trackFormEntity.start_time;

        if(!redisUtils.set(redisKey, trackFormEntity.trackSetJson, CLEAR_SIZE))
        {
            return false;
        }
        if(!trackSetDaoEntity.addTrackSet(trackFormEntity.account_id, trackFormEntity.start_time,trackFormEntity.end_time,trackFormEntity.trackSetJson))
        {
            return false;
        }
        return true;
    }

    /**
     * 等具体落实时回来细细的测试
     * @param account_id
     * @param timeValue
     * @return
     */
    @Override
    public String getTrackBySeconds(int account_id, long timeValue) {
        List<Object> timeList=new ArrayList<Object>();
        String listKey=REDISKEY+account_id+"list";
        timeList=redisUtils.lGet(listKey,0,-1);
        System.out.println("寻找时间"+timeValue);
//        for(int i=0;i<timeList.size();i++)
//        {
//            System.out.println(timeList.get(i));
//        }
        //从小到大的排序
        //二分查找获取最合适的json字符串
        int right=timeList.size()-1;
        int mid=right/2;
        int left=0;
        long orderKey=0;
        while(left<=right)
        {
            orderKey=(Integer) timeList.get(left);
            if((Integer) timeList.get(mid)<timeValue)
            {
               left=mid+1;
            }
            if((Integer) timeList.get(mid)>timeValue)
            {
                right=mid-1;
            }
            if((Integer) timeList.get(mid)==timeValue)
            {
                orderKey=timeValue;
                break;
            }
            mid=(right+left)/2;
        }
                                        
        System.out.println("找到时间"+orderKey);
        String res=(String)redisUtils.get(REDISKEY+account_id+"_"+orderKey);
        //直接获取当前的最大值
        if(res==null)
        {
            res=trackSetDaoEntity.getTrackSetBySeconds(account_id);
        }
        return res;
    }

    @Override
    public List<String> getTrackByMin(int account_id, long time) {
        List<Object> timeList=new ArrayList<Object>();
        List<String>resList =new ArrayList<>();
        String listKey=REDISKEY+account_id+"list";
        //运行时注意MIN_SIZE大小
        if (redisUtils.lGetListSize(listKey) >= MIN_SIZE)
        {

            timeList=redisUtils.lGet(listKey,0,-1);
            for(Object i:timeList)
            {
                String redisKey=REDISKEY+account_id+"_"+i.toString();
                if(redisUtils.get(redisKey)!=null) {
                resList.add(redisUtils.get(redisKey).toString());
                }
            }
        }
        else
        {
            resList=trackSetDaoEntity.getTrackSetByMin(account_id,(time-TIME_SIZE),time);
        }
        return  resList;
    }

    @Override
    public List<String> getTrackByHour(int account_id, long time) {
            List<String>resList =new ArrayList<>();
            //运行时注意HOURTIME_SIZE大小
            resList=trackSetDaoEntity.getTrackSetByMin(account_id,(time-HOURTIME),time);

            System.out.println(resList.size());
            return  resList;

    }

    @Override
    public List<String> getTrackByDay(int account_id, long time) {
        List<String>resList =new ArrayList<>();
        //运行时注意HOURTIME_SIZE大小
        resList=trackSetDaoEntity.getTrackSetByMin(account_id,(time-DAYTIME),time);
        System.out.println(resList.size());
        return  resList;
    }



}
