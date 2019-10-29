package com.cdsen.powersocket.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

/**
 * @author HuSen
 * create on 2019/10/21 11:49
 */
@Slf4j
@Component
public class RabbitSender implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    private final RabbitTemplate rabbitTemplate;

    public RabbitSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("消息:{} 发送成功", correlationData);
        } else {
            log.warn("消息:{} 发送失败:{}", correlationData, cause);
        }
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.warn("消息发送失败, message:{}, replyCode:{}, replyText:{}, exchange:{}, routingKey:{}", message, replyCode, replyText, exchange, routingKey);
    }

    /**
     * convertAndSend 异步,消息是否发送成功用ConfirmCallback和ReturnCallback回调函数类确认。
     * 发送MQ消息
     */
    public void sendMessage(String exchangeName, String routingKey, Object message) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message, new CorrelationData(UUID.randomUUID().toString()));
    }

    /**
     * sendMessageAndReturn 当发送消息过后,该方法会一直阻塞在哪里等待返回结果,直到请求超时,配置spring.rabbitmq.template.reply-timeout来配置超时时间。
     * 发送MQ消息并返回结果
     */
    public Object sendMessageAndReturn(String exchangeName, String routingKey, Object message) {
        return rabbitTemplate.convertSendAndReceive(exchangeName, routingKey, message, new CorrelationData(UUID.randomUUID().toString()));
    }

}
