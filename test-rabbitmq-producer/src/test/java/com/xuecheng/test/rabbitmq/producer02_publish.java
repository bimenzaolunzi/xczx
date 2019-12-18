package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * rabbit 入门程序
 */
public class producer02_publish {
    //队列名称
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    private static final String EXCHANGE_FANOUT_INFORM = "exchange_fanout_inform";


    public static void main(String[] args) {
        //通过连接工厂创建新的连接和mq建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");//地址
        connectionFactory.setPort(5672);//端口
        connectionFactory.setUsername("guest");//用户名
        connectionFactory.setPassword("guest");//密码
        connectionFactory.setVirtualHost("/");//设置主机 一个mq服务可以设置多个虚拟机,每个虚拟机就相当于一台独立的mq
        //创建会话通道,生产者和MQ服务所有通信都在channel通道中完成
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection();
            //创建会话通道
            channel = connection.createChannel();
            //声明队列 如果队列在mq中没有则要创建
            /**
             * 参数明细
             * 1.queue: 队列名称
             * 2.durable: 是否持久化,如果持久化,mq重启后队列还在
             * 3.exclusive:是否独占连接,队列只允许在该连接中访问,如果connection连接关闭队列则自动删除,如果将此参数设置为true可用于临时队列的创建
             * 4.autoDelete:自动删除,队列不再使用时是否自动删除此队列,如果将此参数和exclusive参数设置为true就可以设置临时队列(队列不用了就可以自动删除)
             * 5.arguments:参数,可以设置一个队列的扩展参数,比如可以设置存活时间
             */
            channel.queueDeclare(QUEUE_INFORM_EMAIL, true, false, false, null);
            channel.queueDeclare(QUEUE_INFORM_SMS, true, false, false, null);
            //声明一个交换机
            /**
             * 参数string exchange ,String type
             * 1.交换机名称
             * 2.交换机的类型
             * fanout:对应rabbitmq的工作模式是publish/subscribe
             * direct:对应routing工作模式
             * topic:对应topics工作弄湿
             * headers:对应headers工作模式
             *
             */
            channel.exchangeDeclare(EXCHANGE_FANOUT_INFORM, BuiltinExchangeType.FANOUT);
            //交换机和队列进行绑定
            /**
             * 参数:String queue, String exchange, String routingKey
             * 1.queue:队列名称
             * 2.exchange:交换机名称
             * 3.routingKey路由KEY,作用是交换机根据路由key的值将消息转发到指定的队列中,在发布订阅模式中默认为空字符串
             */
            channel.queueBind(QUEUE_INFORM_EMAIL,EXCHANGE_FANOUT_INFORM,"");
            channel.queueBind(QUEUE_INFORM_SMS,EXCHANGE_FANOUT_INFORM,"");
            //发送消息
            /**
             * 参数String exchange, String routingKey, BasicProperties props, byte[] body
             * 参数明细:
             * 1.exchange:交换机,如果不指定使用mq的默认交换机
             * 2.routingKey:路由KEY,交换机根据路由key来讲消息转发到的制定的队列,如果使用默认,routingKey就要设置为队列名称
             * 3.props:消息属性
             * 4.body:消息内容
             */
            for (int i = 0; i < 10000; i++) {
                String message = "hello,广州电信"+i;//消息体
                channel.basicPublish(EXCHANGE_FANOUT_INFORM, "", null, message.getBytes());
                System.out.println("send to =" + message);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //在这里关流
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            connectionFactory.clone();
        }


    }
}
