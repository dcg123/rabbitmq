package com.czmec.rabbitmq.quickstart;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer {


    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        //1 创建一个ConnectionFactory 并进行配置
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        //2通过连接工厂创建连接
        Connection connection = connectionFactory.newConnection();

        //通过connection创建channel
        Channel channel = connection.createChannel();

        //声明（创建）一个队列
        String queueName = "test01";
        /**
         * 参数说明
         * queue 队列名称
         *durable 表示是否持久化 如果设置为持久化 服务器重启会重新加载队列
         *exclusive 设置为true 表示只能当前连接能够消费此队列（顺序队列）
         *autoDelete 设置为true 表示服务器不使用时会自动删除
         * arguments 参数配置
         */
        channel.queueDeclare(queueName, false, false, false, null);

        //创建消费者
        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);

        //设置channel所消费的队列
        channel.basicConsume(queueName, true, queueingConsumer);

        //接收消息
        while (true){
            QueueingConsumer.Delivery delivery=queueingConsumer.nextDelivery();
            String msg=new String(delivery.getBody());
            System.out.println("消费端:"+msg);
        }
    }
}
