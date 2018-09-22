package com.czmec.rabbitmq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitmqApiApplicationTests {

	@Test
	public void contextLoads() {
	}

	/**
	 * 测试RabbitMqAdmin
	 */


	@Autowired
	private RabbitAdmin rabbitAdmin;


	@Test
	public void testAdmin(){
		//设置交换机
		rabbitAdmin.declareExchange(new DirectExchange("test.direct",false,false));
		rabbitAdmin.declareExchange(new TopicExchange("test.topic",false,false));
		rabbitAdmin.declareExchange(new FanoutExchange("test.fanout",false,false));

		//声明队列
		rabbitAdmin.declareQueue(new Queue("test.direct.queue",false));
		rabbitAdmin.declareQueue(new Queue("test.topic.queue",false));
		rabbitAdmin.declareQueue(new Queue("test.fanout.queue",false));

		//声明绑定
		rabbitAdmin.declareBinding(
				BindingBuilder
						.bind(new Queue("test.topic.queue", false))		//直接创建队列
						.to(new TopicExchange("test.topic", false, false))	//直接创建交换机 建立关联关系
						.with("user.#"));	//指定路由Key

		rabbitAdmin.declareBinding(
				BindingBuilder
						.bind(new Queue("test.fanout.queue", false))
						.to(new FanoutExchange("test.fanout", false, false)));

	}

	/**
	 * 使用消息模版(rabbitTemplate)操控rabbitMq
	 */
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Test
	public void testSendMessage(){
		//创建消息
		MessageProperties messageProperties=new MessageProperties();
		messageProperties.getHeaders().put("desc","信息描述");
		messageProperties.getHeaders().put("type","自定义消息类型");
		//拼装消息体
		Message message=new Message("Hello RabbitMq".getBytes(),messageProperties);

		//转化并发送
		rabbitTemplate.convertAndSend("topic001", "spring.amqp", message, new MessagePostProcessor() {
			@Override
			public Message postProcessMessage(Message message) throws AmqpException {
				//设置消息发送前 对消息的修改和添加
				System.err.println("------添加额外的设置---------");
				message.getMessageProperties().getHeaders().put("desc", "额外修改的信息描述");
				message.getMessageProperties().getHeaders().put("attr", "额外新加的属性");
				return message;
			}
		});
	}

	//rabbitTemplate其他方式发送消息
	@Test
	public void testSendMessage2() throws Exception {
		//1 创建消息
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setContentType("text/plain");
		Message message = new Message("mq 消息1234".getBytes(), messageProperties);

		rabbitTemplate.send("topic001", "spring.abc", message);

		rabbitTemplate.convertAndSend("topic001", "spring.amqp", "hello object message send!");
		rabbitTemplate.convertAndSend("topic002", "rabbit.abc", "hello object message send!");
	}

	@Test
	public void testSendMessage4Text() throws Exception {
		//1 创建消息
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setContentType("text/plain");
		Message message = new Message("mq 消息1234".getBytes(), messageProperties);

		rabbitTemplate.send("topic001", "spring.abc", message);
		rabbitTemplate.send("topic002", "rabbit.abc", message);
	}



}
