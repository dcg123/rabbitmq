- RabbitMq整合springBoot  (生产者)
- publisher-confirms 实现一个监听器用于监听broke端给我们返回的确认请求：RabbitTemplate.ReturnCallback
- publisher-returns  保证消息对broke端是可达的，如果出现路由键不可达的情况，则使用监听器对不可达的消息进行后续处理，保证消息的路由成功

主要代码如下  
``` 

   配置文件application.properties
   spring.rabbitmq.addresses=localhost:5672
   spring.rabbitmq.username=guest
   spring.rabbitmq.password=guest 
   spring.rabbitmq.virtual-host=/
   spring.rabbitmq.connection-timeout=15000
   
   spring.rabbitmq.publisher-confirms=true
   spring.rabbitmq.publisher-returns=true
   spring.rabbitmq.template.mandatory=true


    //回调函数: confirm确认
    	final ConfirmCallback confirmCallback = new ConfirmCallback() {
    		@Override
    		public void confirm(CorrelationData correlationData, boolean ack, String cause) {
    			System.err.println("correlationData: " + correlationData);
    			System.err.println("ack: " + ack);
    			if(!ack){
    				System.err.println("异常处理....");
    			}
    		}
    	};
    	
    	//回调函数: return返回
        	final ReturnCallback returnCallback = new ReturnCallback() {
        		@Override
        		public void returnedMessage(org.springframework.amqp.core.Message message, int replyCode, String replyText,
        				String exchange, String routingKey) {
        			System.err.println("return exchange: " + exchange + ", routingKey: " 
        				+ routingKey + ", replyCode: " + replyCode + ", replyText: " + replyText);
        		}
        	};
        	
        	//发送消息方法调用: 构建Message消息
        	public void send(Object message, Map<String, Object> properties) throws Exception {
        		MessageHeaders mhs = new MessageHeaders(properties);
        		Message msg = MessageBuilder.createMessage(message, mhs);
        		rabbitTemplate.setConfirmCallback(confirmCallback);
        		rabbitTemplate.setReturnCallback(returnCallback);
        		//id + 时间戳 全局唯一 
        		CorrelationData correlationData = new CorrelationData("1234567890");
        		rabbitTemplate.convertAndSend("exchange-1", "springboot.hello", msg, correlationData);
        	}
        	
        	//发送消息方法调用: 构建自定义对象消息
        	public void sendOrder(Order order) throws Exception {
        		rabbitTemplate.setConfirmCallback(confirmCallback);
        		rabbitTemplate.setReturnCallback(returnCallback);
        		//id + 时间戳 全局唯一 
        		CorrelationData correlationData = new CorrelationData("0987654321");
        		rabbitTemplate.convertAndSend("exchange-2", "springboot.def", order, correlationData);
        	}
``` 


[测试代码](/src/test/java/com/czmec/rabbitmq/RabbitmqApiApplicationTests) 


``` 
测试代码

	@Test
    	public void testSender1() throws Exception {
    		Map<String, Object> properties = new HashMap<>();
    		properties.put("number", "12345");
    		properties.put("send_time", simpleDateFormat.format(new Date()));
    		rabbitSender.send("Hello RabbitMQ For Spring Boot!", properties);
    	}
``` 

