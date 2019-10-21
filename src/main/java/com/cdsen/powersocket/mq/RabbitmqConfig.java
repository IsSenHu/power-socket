package com.cdsen.powersocket.mq;

import com.cdsen.apollo.AppProperties;
import com.cdsen.apollo.ConfigUtils;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import static com.cdsen.apollo.AppProperties.Rabbitmq.RABBITMQ_NAMESPACE;

/**
 * @author HuSen
 * create on 2019/10/21 10:22
 */
@Configuration
public class RabbitmqConfig {

    /**
     * 1.队列名字
     * 2.durable="true" 是否持久化 rabbitmq重启的时候不需要创建新的队列
     * 3.auto-delete    表示消息队列没有在使用时将被自动删除 默认是false
     * 4.exclusive      表示该消息队列是否只在当前connection生效,默认是false
     *
     * @return queue
     */
    @Bean
    public Queue directQueue() {
        String name = ConfigUtils.getProperty(RABBITMQ_NAMESPACE, AppProperties.Rabbitmq.DIRECT_QUEUE_POWER, "");
        Assert.hasText(name, "directQueue name is empty!");
        return new Queue(name, true, false, false);
    }

    @Bean
    public Queue directQueueCreateConsumption() {
        String name = ConfigUtils.getProperty(RABBITMQ_NAMESPACE, AppProperties.Rabbitmq.DIRECT_QUEUE_CREATE_CONSUMPTION, "");
        Assert.hasText(name, "directQueue name is empty!");
        return new Queue(name, true, false, false);
    }

    @Bean
    public DirectExchange directExchange() {
        String name = ConfigUtils.getProperty(RABBITMQ_NAMESPACE, AppProperties.Rabbitmq.DIRECT_EXCHANGE_POWER, "");
        Assert.hasText(name, "directExchange name for power is empty!");
        return new DirectExchange(name, true, false);
    }

    /**
     * Direct模式相当于一对一模式，一个消息被发送者发送后，会被转发到一个绑定的消息队列中，然后被一个监听该队列的接收者接收!
     * 也就是说根据routeKey来确定消息发送到哪个队列里
     * 当指定的routekey不存在时消息会丢失
     * 一条消息一次只能被一个消费者监听到
     * 不同消费者监听同一个队列将会轮流获取消息
     *
     * @return binding
     */
    @Bean
    public Binding bindingDirect() {
        String key = ConfigUtils.getProperty(RABBITMQ_NAMESPACE, AppProperties.Rabbitmq.DIRECT_KEY_POWER, "");
        Assert.hasText(key, "direct key for power is empty!");
        return BindingBuilder.bind(directQueue()).to(directExchange()).with(key);
    }

    @Bean
    public Binding bindingDirectCreateConsumption() {
        String key = ConfigUtils.getProperty(RABBITMQ_NAMESPACE, AppProperties.Rabbitmq.DIRECT_KEY_CREATE_CONSUMPTION, "");
        Assert.hasText(key, "direct key for power is empty!");
        return BindingBuilder.bind(directQueueCreateConsumption()).to(directExchange()).with(key);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
