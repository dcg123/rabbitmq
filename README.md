## rabbitmq
> 为什么要学习rabbitmq

- 能够与SpringAMQP完美整合,api丰富
- 集群模式丰富,表达式匹配,HA模式,镜像队列模式
- 保证数据不丢失的前提下可做到高可靠性，可用性

## exchange三种常用模式
> direct(直连)模式

<img src = "http://www.rabbitmq.com/img/tutorials/direct-exchange.png" alt="direct模式">

> topic(模糊匹配)模式

<img src = "http://www.rabbitmq.com/img/tutorials/python-five.png" alt="topic模式">

> fanout模式

<img src = "https://images2017.cnblogs.com/blog/1147891/201711/1147891-20171122151041040-579186474.png" alt="fanout模式">


## confirm确认消息
[代码](/src/main/java/com/czmec/rabbitmq/api/confirm) 

## return消息机制
[代码](/src/main/java/com/czmec/rabbitmq/api/returnlistener) 

## 消费端自定义监听
[代码](/src/main/java/com/czmec/rabbitmq/api/consumer) 

## 消费端限流
[代码](/src/main/java/com/czmec/rabbitmq/api/limit) 
RabbitMq提供了一种qos(服务质量保证)功能，即在非自动确认的消息条件下，如果一定数目的消息（通过channel或者consume设置
的qos的值）未被确认前，不进行消费新的消息
void BasicQos(int prefetchSize,int prefetchCount,boolean global)

## 消费端的ack与重回队列
[代码](/src/main/java/com/czmec/rabbitmq/api/ack) 
### 消费端的手工ack和nack
#### 消费端进行消费的时候，由于业务异常我们可以进行日志记录，然后进行补偿
#### 由于服务器严重宕机问题，那就需要我们手动ack保证消费端消费成功

## 消费端的重回队列
#### 消费端重回队列是为了对处理不成功的消息，吧消息重新投递给broker
#### 一般在实际应用中，会关闭重回队列，设置为false

## TTl队列/消息
#### 生存时间
#### RabbitMq支持设置过期时间，在消息发送时可以指定
#### RabbitMq支持队列过期时间，从消息入队开始计算，只要超过了消息队列的超时时间配置，那么消息就会自动清楚





