package com.czmec.rabbitmq.quickstart;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Product {

    public static void main(String[] args) throws IOException, TimeoutException {
        //1 创建一个ConnectionFactory 并进行配置
        ConnectionFactory connectionFactory=new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        //2通过连接工厂创建连接
        Connection connection=connectionFactory.newConnection();

        //通过connection创建channel
        Channel channel=connection.createChannel();

        //通过channel发送数据
        for (int i=0;i<10;i++){
            String msg="Hello RabbitMq";
            /**
             * 只指定了routingKey
             * 如果生产者指定的exchange为空 会默认使用AMQP default（根据routingKey去队列中找到与routingKey相同名称的队列）
             */
            channel.basicPublish("","test01",null,msg.getBytes());
        }

        //关闭连接
        channel.close();
        connection.close();

    }


}
