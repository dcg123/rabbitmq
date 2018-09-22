- 在springAMQP整合时进行发送消息的关键类  
- 该类提供了丰富的发送消息的方法，包括可靠性投递消息方法，回调监听消息接口ConfirmCallback,返回确认接口。
- 在与spring整合时需要实例化

主要代码如下  
``` 

     @Bean
        public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
            RabbitTemplate rabbitTemplate=new RabbitTemplate(connectionFactory);
            return rabbitTemplate;
        }
``` 


[测试代码](/src/test/java/com/czmec/rabbitmq/RabbitmqApiApplicationTests) 


``` 
测试代码

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
``` 

