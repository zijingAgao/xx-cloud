## xx-cloud
微服务整合技术点案例

### 技术栈
- springBoot 2.6.13
- SpringCloud 2021.0.5
- CloudAlibaba 2021.0.5.0 nacos 注册中心/配置中心
- MyBatisPlus 3.3.2
- Redisson 3.25.2 缓存，分布式锁
- MySQL 8.0.33
- MongoDB 5.0.35
- Guava 30.0.0-jre LoadingCache
- Swagger 3.0.0
- RabbitMQ 死信队列
- XXL-Job 定时任务

### 整合的功能点
- Nacos服务注册与发现
- Nacos配置中心
- RabbitMQ 消息队列，订单失效
- - 1.投递延迟队列，等待15分钟再投递到队列+监听判断数据库订单状态，消费消息。注意需要安装rabbitMQ插件 rabbitmq_delayed_message_exchange
- - 2.投递到队列，设置过期时间，不设置消费者，等待过期进入死信队列，在死信队列的监听中消费消息。DLX+TTL
- XXL-Job 定时任务
- Redis缓存，分布式锁
- Ip寻址-awdb