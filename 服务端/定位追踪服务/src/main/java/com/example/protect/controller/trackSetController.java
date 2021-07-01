package com.example.protect.controller;
import com.example.protect.entities.AccountInfo;
import com.example.protect.entities.trackform;
import com.example.protect.service.IMessage;
import com.example.protect.service.implement.AccountServiceImpl;
import com.example.protect.service.implement.trackSetServiceImp;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;


@Api(tags = "轨迹管理API")
@RestController
@Slf4j
@DefaultProperties(defaultFallback = "global_Fallback")
public class trackSetController {

    @Autowired
    RestTemplate restTemplate;

    @Resource
    private trackSetServiceImp trackServiceImp;
    @Resource
    private AccountServiceImpl accountService;

    @Resource
    private IMessage iMessage;


    @ApiOperation("存入轨迹")
    @RequestMapping(value = "/protect/storage",method = RequestMethod.POST)
    @ResponseBody
    public boolean storage(@RequestBody @Validated trackform trackform) {
        LocalDateTime Time = LocalDateTime.now();
        System.out.println("storage到了");
        //System.out.println(trackform.trackSetJson);
        if(trackServiceImp.storageTrack(trackform)) {
            return true;
        }
        //        String msg = Time.toString()+"-"+"select-user_id="+ID+"-account";
        //        iMessage.send(msg);
        return false;
    }

    @ApiOperation("按秒取出轨迹")
    @GetMapping("/protect/getTrackBySeconds")
    @ResponseBody
    public String getTrack(@ApiParam("用户ID") @RequestParam(name = "account_id") int account_id,
                           @ApiParam("轨迹时间") @RequestParam(name = "time") long time) {

        String resJson=trackServiceImp.getTrackBySeconds(account_id,time);
        //        LocalDateTime Time = LocalDateTime.now();
        //        String msg = Time.toString()+"-"+"select-user_id="+ID+"-account";
        //        iMessage.send(msg);
        return resJson;
    }

    @ApiOperation("按秒取出轨迹")
    @GetMapping("/protect/getTrackBySecondsToken")
    @ResponseBody
    public String getTrack(@RequestHeader("token") String token,
                                      @ApiParam("轨迹时间") @RequestParam(name = "time") long time) {
        AccountInfo accountInfo = accountService.getAccountInfo(token);

       String resJson=trackServiceImp.getTrackBySeconds((int) accountInfo.id,time);
       //        LocalDateTime Time = LocalDateTime.now();
        //        String msg = Time.toString()+"-"+"select-user_id="+ID+"-account";
        //        iMessage.send(msg);
        return resJson;
    }

    @ApiOperation("客户端取出最近所有轨迹")
    @GetMapping("/protect/getAllTrackRecently")
    @ResponseBody
    public List<String> getAllTrack(@ApiParam("用户ID") @RequestParam(name = "account_id") int account_id,
                                    @ApiParam("轨迹时间") @RequestParam(name = "time") long time) {

        System.out.println("取出轨迹");
        return trackServiceImp.getTrackByMin(account_id,time);
    }

    @ApiOperation("客户端取出最近所有轨迹")
    @GetMapping("/protect/getAllTrackRecentlyToken")
    @ResponseBody
    public List<String> getAllTrack(@RequestHeader("token") String token,
                                    @ApiParam("轨迹时间") @RequestParam(name = "time") long time) {

        AccountInfo accountInfo = accountService.getAccountInfo(token);
        return trackServiceImp.getTrackByMin((int) accountInfo.id,time);
    }


    @ApiOperation("取出最近2h轨迹")
    @GetMapping("/protect/getTrackAround2h")
    @ResponseBody
    public List<String> getTrackAround2h(@ApiParam("用户ID") @RequestParam(name = "account_id") int account_id,
                                    @ApiParam("轨迹时间") @RequestParam(name = "time") long time) {

        System.out.println("取出2h轨迹");
        return trackServiceImp.getTrackByHour(account_id,time);
    }

    @ApiOperation("取出最近2h轨迹")
    @GetMapping("/protect/getTrackAround2hToken")
    @ResponseBody
    public List<String> getTrackAround2h(@RequestHeader("token") String token,
                                         @ApiParam("轨迹时间") @RequestParam(name = "time") long time) {

        System.out.println("取出2h轨迹");
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        return trackServiceImp.getTrackByHour((int) accountInfo.id,time);
    }

    @ApiOperation("取出所选一天的轨迹")
    @GetMapping("/protect/getTrackAroundDay")
    @ResponseBody
    public List<String> getTrackAroundDay(@ApiParam("用户ID") @RequestParam(name = "account_id") int account_id,
                                         @ApiParam("轨迹时间") @RequestParam(name = "time") long time) {

        System.out.println("取出最近一天轨迹");
        return trackServiceImp.getTrackByDay(account_id,time);
    }

    @ApiOperation("取出所选一天的轨迹")
    @GetMapping("/protect/getTrackAroundDayToken")
    @ResponseBody
    public List<String> getTrackAroundDay(@RequestHeader("token") String token,
                                          @ApiParam("轨迹时间") @RequestParam(name = "time") long time) {

        System.out.println("取出最近一天轨迹");
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        return trackServiceImp.getTrackByDay((int) accountInfo.id,time);
    }

    @GetMapping("/protect/getIdByToken")
    @ResponseBody
    public long getIdBytoken(@RequestHeader("token") String token) {

        System.out.println("取出id");
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        return accountInfo.id;
    }

    @GetMapping("/protect/getUserInfoByToken")
    @ResponseBody
    public AccountInfo getUserInfo(@RequestHeader("token") String token) {

        System.out.println("取出userInfo数据");
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        return accountInfo;
    }
}
