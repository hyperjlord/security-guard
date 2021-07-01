package com.yj.rabbitmq;

import com.yj.config.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.message.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageConsumer {
    @RabbitListener(queues = "warningQueue")
    public void processMsg(String user_id) {
        log.warn("用户:"+user_id+"发出了预警!");
        //调用audio-service的接口，去获取录音之类的
    }
}
