package com.cdsen.powersocket.controller;

import com.cdsen.apollo.AppProperties;
import com.cdsen.apollo.ConfigUtils;
import com.cdsen.powersocket.message.MessageResult;
import com.cdsen.powersocket.websocket.BaseController;
import com.cdsen.rabbit.model.ConsumptionCreateDTO;
import com.cdsen.rabbit.model.RabbitMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
