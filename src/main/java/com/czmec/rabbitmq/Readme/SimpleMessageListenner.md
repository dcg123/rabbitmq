- 简单消息监听容器  
- 这个类非常强大，我们可以对它进行很多的配置，对于消费者的配置项，这个类都可以满足
- 监听队列（多个队列）自动启动，自动声明功能  
- 设置事务特性等等
- 设置消费者数量，最小最大数量，批量消费
- 设置消费确认和自动确认模式，是否重回队列，异常铺货handler函数
- 设置消费者标签生成策略，是否独占模式，消费者属性等
- 设置具体的监听器，消息转换消息模式等，
- 在与spring整合时需要实例化
- 在与spring整合时需要实例化



主要代码如下  
``` 

         /**
          * 简单消息监听容器
          */
         @Bean
         public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory){
             SimpleMessageListenerContainer container=new SimpleMessageListenerContainer(connectionFactory);
             //设置监听的队列消息
             container.setQueues(queue001(), queue002(), queue003(), queue_image(), queue_pdf());
             //指定要创建的并发用户的数量。
             container.setConcurrentConsumers(1);
             //设置最大消费者数量
             container.setMaxConcurrentConsumers(5);
             //设置是否重回队列
             container.setDefaultRequeueRejected(false);
             container.setAcknowledgeMode(AcknowledgeMode.AUTO);
             container.setExposeListenerChannel(true);
             //设置监听队列唯一标志
             container.setConsumerTagStrategy(new ConsumerTagStrategy() {
                 @Override
                 public String createConsumerTag(String queue) {
                     return queue + "_" + UUID.randomUUID().toString();
                 }
             });
     
             /**
              * 添加监听队列消息处理
              */
             container.setMessageListener(new ChannelAwareMessageListener() {
             @Override
             public void onMessage(Message message, Channel channel) throws Exception {
             String msg = new String(message.getBody());
             System.err.println("----------消费者: " + msg);
             }
             });
     
     
             /**
              * 1 适配器方式. 默认是有自己的方法名字的：handleMessage
              // 可以自己指定一个方法的名字: consumeMessage
              // 也可以添加一个转换器: 从字节数组转换为String
              MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
              adapter.setDefaultListenerMethod("consumeMessage");
              adapter.setMessageConverter(new TextMessageConverter());
              container.setMessageListener(adapter);
              */
     
             /**
              * 2 适配器方式: 我们的队列名称 和 方法名称 也可以进行一一的匹配
              *
              MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
              adapter.setMessageConverter(new TextMessageConverter());
              Map<String, String> queueOrTagToMethodName = new HashMap<>();
              queueOrTagToMethodName.put("queue001", "method1");
              queueOrTagToMethodName.put("queue002", "method2");
              adapter.setQueueOrTagToMethodName(queueOrTagToMethodName);
              container.setMessageListener(adapter);
              */
             return container;
     
         }
``` 


[测试代码](/src/test/java/com/czmec/rabbitmq/RabbitmqApiApplicationTests) 


``` 
测试代码

	@Test
    	public void testSendMessage4Text() throws Exception {
    		//1 创建消息
    		MessageProperties messageProperties = new MessageProperties();
    		messageProperties.setContentType("text/plain");
    		Message message = new Message("mq 消息1234".getBytes(), messageProperties);
    
    		rabbitTemplate.send("topic001", "spring.abc", message);
    		rabbitTemplate.send("topic002", "rabbit.abc", message);
    	}
``` 

