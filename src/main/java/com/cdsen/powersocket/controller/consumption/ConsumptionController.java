package com.cdsen.powersocket.controller.consumption;

import com.cdsen.apollo.AppProperties;
import com.cdsen.apollo.ConfigUtils;
import com.cdsen.powersocket.message.MessageResult;
import com.cdsen.powersocket.websocket.BaseController;
import com.cdsen.powersocket.websocket.Robot;
import com.cdsen.rabbit.model.ConsumptionCreateDTO;
import com.cdsen.rabbit.model.RabbitMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.util.Pair;
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
@Slf4j
@RestController
public class ConsumptionController extends BaseController {

    private final RabbitTemplate rabbitTemplate;
    private final Robot robot;

    public ConsumptionController(SimpUserRegistry simpUserRegistry, RabbitTemplate rabbitTemplate, Robot robot) {
        super(simpUserRegistry);
        this.rabbitTemplate = rabbitTemplate;
        this.robot = robot;
    }

    @MessageMapping("/consumption")
    @SendToUser("/queue/router")
    public MessageResult<ConsumptionCommand> consumption(@RequestBody ConsumptionParam param, Principal principal) {
        ConsumptionCommand command = param.getCommand();
        Pair<ConsumptionCommand, ConsumptionCreateDTO> pair = robot.autoConsumption(command, param.getParam(), principal.getName());
        if (command.equals(ConsumptionCommand.CANCEL)) {
            log.info("{} 取消这次操作", principal.getName());
        } else if (command.equals(ConsumptionCommand.FINISH)) {
            String exchange = ConfigUtils.getProperty(RABBITMQ_NAMESPACE, AppProperties.Rabbitmq.DIRECT_EXCHANGE_POWER, "");
            String key = ConfigUtils.getProperty(RABBITMQ_NAMESPACE, AppProperties.Rabbitmq.DIRECT_KEY_CREATE_CONSUMPTION, "");
            String id = UUID.randomUUID().toString();
            rabbitTemplate.convertAndSend(exchange, key, new RabbitMessage<>(Long.parseLong(principal.getName()), id, pair.getSecond()));
        }
        return MessageResult.of(pair.getFirst());
    }
}
