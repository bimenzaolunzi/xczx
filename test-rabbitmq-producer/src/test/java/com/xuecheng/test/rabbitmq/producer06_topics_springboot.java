package com.xuecheng.test.rabbitmq;

import com.xuecheng.test.rabbitmq.config.RabbitmqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * rabbit 入门程序
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class producer06_topics_springboot {


    //使用rabbitTemplate
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    /**
     * 参数:
     * 1.参数名称
     * 2.routingKEY
     * 3.消息体
     *
     */
    public void testSendEmail(){
        for (int i = 0; i < 50; i++) {
            String message="send email message to user"+i;
            rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_TOPICS_INFORM,"inform.email",message);
            System.out.println("message = " + message);
        }

    }

}
