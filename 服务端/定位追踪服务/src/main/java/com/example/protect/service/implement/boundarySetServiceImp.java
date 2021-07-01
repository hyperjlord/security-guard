package com.example.protect.service.implement;

import com.example.protect.dao.boundarySetDao;
import com.example.protect.entities.boundarySet;
import com.example.protect.service.boundarySetService;
import com.example.protect.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class boundarySetServiceImp implements boundarySetService {
    //redis工具类
    @Autowired
    private RedisUtils redisUtils;
    @Resource
    boundarySetDao boundarySetDao;

    private String BOUNDARYREDISKEY="share_boundary";
    private String EDGEREDISKEY="share_edge";

    //存一个区域
    @Override
    public boolean storageBoundary(boundarySet boundarySetEntity) {
        //用list 存
        String listKey=BOUNDARYREDISKEY+boundarySetEntity.account_id+"list";
        //先全部移除
        if (boundarySetEntity.boundary_id==0) {
                redisUtils.set(listKey, 0,500);
                boundarySetDao.deleteBoundaryById(boundarySetEntity.account_id);
        }
        redisUtils.incr(listKey,1);
        boundarySetDao.addBoundarySet(boundarySetEntity.account_id,boundarySetEntity.boundary_id,boundarySetEntity.boundary_set_json);
        return true;
    }

    //存一个边界
    @Override
    public boolean storageEdge(boundarySet boundarySetEntity) {
        //用list 存
        String listKey=EDGEREDISKEY+boundarySetEntity.account_id+"list";
        //先全部移除
        if (boundarySetEntity.boundary_id==0&&redisUtils.lGetListSize(listKey)>0) {
            for(int i=0;i<redisUtils.lGetListSize(listKey);i++) {
                redisUtils.lPop(listKey, 0);
            }
        }
        return redisUtils.lSet(listKey, boundarySetEntity.boundary_set_json,500);
    }

    //取出安全区域
    @Override
    public List<boundarySet> getBoundaryById(int account_id) {
        return boundarySetDao.getBoundaryById(account_id);
    }

    //取出安全边界
    @Override
    public List<String> getEdgeById(int account_id) {
        String listKey=EDGEREDISKEY+account_id+"list";
        List<String> resList=new ArrayList<String>();
        resList=(List<String>)(List)redisUtils.lGet(listKey,0,-1);
            return resList;
        //再从mysql中取出新的边界

    }

}
