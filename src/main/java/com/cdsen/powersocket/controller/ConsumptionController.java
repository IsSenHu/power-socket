package com.cdsen.powersocket.controller;

import com.cdsen.apollo.AppProperties;
import com.cdsen.apollo.ConfigUtils;
import com.cdsen.powersocket.message.MessageResult;
import com.cdsen.powersocket.websocket.BaseController;
import com.cdsen.rabbit.model.ConsumptionCreateDTO;
import com.cdsen.rabbit.model.RabbitMessage;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

import static com.cdsen.apollo.AppProperties.Rabbitmq.RABBITMQ_NAMESPACE;

/**
 * @author HuSen
 * create on 2019/10/18 16:23
 */
@RestController
public class ConsumptionController extends BaseController {

    private final RabbitTemplate rabbitTemplate;

    public ConsumptionController(SimpUserRegistry simpUserRegistry, RabbitTemplate rabbitTemplate) {
        super(simpUserRegistry);
        this.rabbitTemplate = rabbitTemplate;
    }

    @MessageMapping("/consumption")
    @SendToUser("/queue/router")
    public MessageResult<String> consumption(@RequestBody ConsumptionCreateDTO dto, Principal principal) {
        String exchange = ConfigUtils.getProperty(RABBITMQ_NAMESPACE, AppProperties.Rabbitmq.DIRECT_EXCHANGE_POWER, "");
        String key = ConfigUtils.getProperty(RABBITMQ_NAMESPACE, AppProperties.Rabbitmq.DIRECT_KEY_CREATE_CONSUMPTION, "");
        String id = UUID.randomUUID().toString();
        rabbitTemplate.convertAndSend(exchange, key, new RabbitMessage<>(Long.parseLong(principal.getName()), id, dto));
        return MessageResult.success();
    }

    @RabbitListener(queues = "directQueuePower", group = "1")
    @RabbitHandler
    public void directMessage(String sendMessage, Channel channel, Message message) throws IOException {
        try {
            System.out.println(message);
            // prefetchCount限制每个消费者在收到下一个确认回执前一次可以最大接受多少条消息,通过basic.qos方法设置prefetch_count=1,这样RabbitMQ就会使得每个Consumer在同一个时间点最多处理一个Message
            channel.basicQos(1);
        } catch (IOException e) {
            e.printStackTrace();
            // 拒绝当前消息，并把消息返回原队列
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}
