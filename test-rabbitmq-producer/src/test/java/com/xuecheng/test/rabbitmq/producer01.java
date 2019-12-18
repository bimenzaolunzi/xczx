package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * rabbit 入门程序
 */
public class producer01 {
    //队列
    private static final String QUEUE = "helloworld";


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
        Channel channel=null;
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
            channel.queueDeclare(QUEUE, true, false, false, null);
            //发送消息
            /**
             * 参数String exchange, String routingKey, BasicProperties props, byte[] body
             * 参数明细:
             * 1.exchange:交换机,如果不指定使用mq的默认交换机
             * 2.routingKey:路由KEY,交换机根据路由key来讲消息转发到的制定的队列,如果使用默认,routingKey就要设置为队列名称
             * 3.props:消息属性
             * 4.body:消息内容
             */
            String message = "hello,广州电信";//消息体
            channel.basicPublish("", QUEUE, null, message.getBytes());
            System.out.println("send to =" + message);
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
