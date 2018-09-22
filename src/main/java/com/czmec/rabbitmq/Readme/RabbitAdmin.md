- RabbitAdmin类可以很好的整合的操作RabbitMq,在spring中直接注入即可
主要代码如下  
``` 

    @Bean
    public ConnectionFactory connectionFactory(){
        /**
         * 配置RabbitMq
         */
        CachingConnectionFactory cachingConnectionFactory=new CachingConnectionFactory();
        cachingConnectionFactory.setHost("localhost");
        cachingConnectionFactory.setPort(5672);
        cachingConnectionFactory.setUsername("guest");
        cachingConnectionFactory.setPassword("guest");
        cachingConnectionFactory.setVirtualHost("/");
        return cachingConnectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        RabbitAdmin rabbitAdmin=new RabbitAdmin(connectionFactory);
        //设置自动加载
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }
``` 
注意：autoStartup必须设置为true,否则spring容器不会加载RabbitAdmin类  
RabbitAdmin底层实现就是从spring容器中获取exchange,queue,routingKey以及Bing的声明(可以看下rabbitAdmin中实现InitializingBean的方法)  
然后使用RabbitTemplate的execute的方法执行对应的声明，修改删除等一系列操作

[测试代码](/src/test/java/com/czmec/rabbitmq/RabbitmqApiApplicationTests) 


- SpringAMQP-RabbitMQ声明式配置
主要代码如下  
``` 
 @Bean
    public TopicExchange exchange001() {
        return new TopicExchange("topic001", true, false);
    }

    @Bean
    public Queue queue001() {
        return new Queue("queue001", true); //队列持久
    }

    @Bean
    public Binding binding001() {
        return BindingBuilder.bind(queue001()).to(exchange001()).with("spring.*");
    }

    @Bean
    public TopicExchange exchange002() {
        return new TopicExchange("topic002", true, false);
    }
   (省虐。。。)
``` 
如上所示，容器会在运行时去加载声明的队列，交换机，以及绑定。
``` 
测试代码
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
``` 

