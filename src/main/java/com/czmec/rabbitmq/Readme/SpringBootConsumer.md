- RabbitMq整合springBoot  (消费者)
- 消费端核心配置（配置手工确认模式，用于手工ack处理，这样可以保证消息的可靠性送达，或者消费端消费失败的时候可以重回队列，根据业务日志等处理）
- 可以设置消费者监听个数和最大数量，用于控制消费端并发情况
- @RabbitListener是一个组合注解，里面可以配置Exchange,queue,key等一次性搞定消费端的交换机，路由，队列绑定，并配置监听功能
主要代码如下  
``` 



    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "queue-1",
                    durable="true"),
            exchange = @Exchange(value = "exchange-1",
                    durable="true",
                    type= "topic",
                    ignoreDeclarationExceptions = "true"),
            key = "springboot.*"
    )
    )
    @RabbitHandler
    public void onMessage(Message message, Channel channel) throws Exception {
        System.err.println("--------------------------------------");
        System.err.println("消费端Payload: " + message.getPayload());
        Long deliveryTag = (Long)message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        //手工ACK
        channel.basicAck(deliveryTag, false);
    }

//  根据配置文件生成绑定
//    /**
//     *
//     * 	spring.rabbitmq.listener.order.queue.name=queue-2
//     spring.rabbitmq.listener.order.queue.durable=true
//     spring.rabbitmq.listener.order.exchange.name=exchange-1
//     spring.rabbitmq.listener.order.exchange.durable=true
//     spring.rabbitmq.listener.order.exchange.type=topic
//     spring.rabbitmq.listener.order.exchange.ignoreDeclarationExceptions=true
//     spring.rabbitmq.listener.order.key=springboot.*
//     * @param order
//     * @param channel
//     * @param headers
//     * @throws Exception
//     */
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = "${spring.rabbitmq.listener.order.queue.name}",
//                    durable="${spring.rabbitmq.listener.order.queue.durable}"),
//            exchange = @Exchange(value = "${spring.rabbitmq.listener.order.exchange.name}",
//                    durable="${spring.rabbitmq.listener.order.exchange.durable}",
//                    type= "${spring.rabbitmq.listener.order.exchange.type}",
//                    ignoreDeclarationExceptions = "${spring.rabbitmq.listener.order.exchange.ignoreDeclarationExceptions}"),
//            key = "${spring.rabbitmq.listener.order.key}"
//    )
//    )
//    @RabbitHandler
//    public void onOrderMessage(@Payload com.bfxy.springboot.entity.Order order,
//                               Channel channel,
//                               @Headers Map<String, Object> headers) throws Exception {
//        System.err.println("--------------------------------------");
//        System.err.println("消费端order: " + order.getId());
//        Long deliveryTag = (Long)headers.get(AmqpHeaders.DELIVERY_TAG);
//        //手工ACK
//        channel.basicAck(deliveryTag, false);
//    }
``` 






