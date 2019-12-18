package com.xuecheng.test.rabbitmq.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import com.xuecheng.test.rabbitmq.config.RabbitmqConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component //这里既不是service也不是dao,所以用这个注解代表这是一个bean
public class ReceiveHandler {

    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_EMAIL})
    public void send_email(String mage, Message message, Channel channel) throws IOException {
        System.out.println("mage = " + mage);
     //   byte[] body = message.getBody();
       // String s = new String(body, "utf-8");
       // channel.basicAck(envelope.getDeliveryTag(),true);
        System.out.println("s = " +mage);
    }


}
