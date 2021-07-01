package com.example.protect.controller;

import com.example.protect.entities.boundarySet;
import com.example.protect.service.IMessage;
import com.example.protect.service.implement.boundarySetServiceImp;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "安全边界管理API")
@RestController
@Slf4j
@DefaultProperties(defaultFallback = "global_Fallback")
public class bandarySetController {
    @Autowired
    RestTemplate restTemplate;

    @Resource
    private boundarySetServiceImp boundarySetServiceImpEntity;

    @Resource
    private IMessage iMessage;

    @ApiOperation("存入安全范围点阵")
    @RequestMapping(value = "/protect/boundaryStorage",method = RequestMethod.POST)
    @ResponseBody
    public boolean storage( @RequestBody boundarySet boundarySetEntity) {

        return boundarySetServiceImpEntity.storageBoundary(boundarySetEntity);
        //        String msg = Time.toString()+"-"+"select-user_id="+ID+"-account";
        //        iMessage.send(msg);
    }


    @ApiOperation("存入安全边界点阵")
    @RequestMapping(value = "/protect/edgeStorage",method = RequestMethod.POST)
    @ResponseBody
    public boolean storageEdge( @RequestBody boundarySet boundarySetEntity) {

        return boundarySetServiceImpEntity.storageEdge(boundarySetEntity);
        //        String msg = Time.toString()+"-"+"select-user_id="+ID+"-account";
        //        iMessage.send(msg);
    }

    @ApiOperation("取出最近一次的安全范围点集")
    @GetMapping("/protect/getBoundaryById")
    @ResponseBody
    public List<boundarySet> getBoundaryById(@ApiParam("用户ID") @RequestParam(name = "account_id") int account_id) {

        return boundarySetServiceImpEntity.getBoundaryById(account_id);
    }

    @ApiOperation("取出边界值")
    @GetMapping("/protect/getEdgeById")
    @ResponseBody
    public List<String> getEdgeById(@ApiParam("用户ID") @RequestParam(name = "account_id") int account_id) {

        return boundarySetServiceImpEntity.getEdgeById(account_id);
    }
    @ApiOperation("测试")
    @GetMapping("/protect/test")
    @ResponseBody
    public String getEdgeId(@ApiParam("用户ID") @RequestParam(name = "account_id") int account_id) {

        return account_id+"";
    }
}
