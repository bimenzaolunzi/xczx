package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;

public class consumer04_topics_sms {
    //队列名称
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    private static final String EXCHANGE_TOPICS_INFORM = "exchange_topics_inform";
    private static final String ROUTINGKEY_SMS="inform.#.sms.#";

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
        try {
            connection = connectionFactory.newConnection();
            //创建会话通道
            Channel channel = connection.createChannel();
            //声明队列 如果队列在mq中没有则要创建
            /**
             * 参数明细
             * 1.queue: 队列名称
             * 2.durable: 是否持久化,如果持久化,mq重启后队列还在
             * 3.exclusive:是否独占连接,队列只允许在该连接中访问,如果connection连接关闭队列则自动删除,如果将此参数设置为true可用于临时队列的创建
             * 4.autoDelete:自动删除,队列不再使用时是否自动删除此队列,如果将此参数和exclusive参数设置为true就可以设置临时队列(队列不用了就可以自动删除)
             * 5.arguments:参数,可以设置一个队列的扩展参数,比如可以设置存活时间
             */
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
            channel.exchangeDeclare(EXCHANGE_TOPICS_INFORM, BuiltinExchangeType.TOPIC);
            //交换机和队列进行绑定
            /**
             * 参数:String queue, String exchange, String routingKey
             * 1.queue:队列名称
             * 2.exchange:交换机名称
             * 3.routingKey路由KEY,作用是交换机根据路由key的值将消息转发到指定的队列中,在发布订阅模式中默认为空字符串
             */
            channel.queueBind(QUEUE_INFORM_SMS,EXCHANGE_TOPICS_INFORM,ROUTINGKEY_SMS);
            //实现消费方法
            DefaultConsumer defaultConsumer = new DefaultConsumer(channel){
                //当接收到消息后此方法将被调用

                /**
                 *
                 * @param consumerTag 消费者标签,用来标示消费者,可设置可不设置,在监听队列时设置
                 * @param envelope 信封 通过envelope
                 * @param properties 属性,消息的属性,生产者可以传递一些特别的属性,消费者通过这里来取出来
                 * @param body 消息内容,通过new String() 构造方法,同时需要指定编码字符集
                 * @throws IOException
                 */
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    //拿到交换机
                    String exchange = envelope.getExchange();
                    //消息DI,mq在channel中用来表示消息的id
                    long deliveryTag = envelope.getDeliveryTag();
                    System.out.println("deliveryTag = " + deliveryTag);
                    System.out.println("body+sms = " +new String(body,"UTF-8"));
                    channel.basicAck(deliveryTag,envelope.isRedeliver());//手动确认消息 成功
                }
            };

            //监听队列
            /**
             * String queue, boolean autoAck, Consumer callback
             * 参数明细
             * 1.queue:队列名称
             * 2.autoAck:自动回复,当消费者接收到消息后要告诉mq消息已收到,如果将此参数设置为true表示会自动回复mq,如果设置费false要通过编程实现回复
             * 3.callback:消费方法,当消费者接收到消息要执行的方法
             */
            channel.basicConsume(QUEUE_INFORM_SMS,false,defaultConsumer);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //消费者不能关闭流,因为他需要一直监听队列!!
        }
    }
}
