package com.cdsen.powersocket.controller.income;

import com.cdsen.apollo.AppProperties;
import com.cdsen.apollo.ConfigUtils;
import com.cdsen.powersocket.message.MessageResult;
import com.cdsen.powersocket.websocket.BaseController;
import com.cdsen.powersocket.websocket.Robot;
import com.cdsen.rabbit.model.InComeCreateDTO;
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
 * create on 2019/10/29 10:05
 */
@Slf4j
@RestController
public class IncomeController extends BaseController {

    private final Robot robot;
    private final RabbitTemplate rabbitTemplate;

    public IncomeController(SimpUserRegistry simpUserRegistry, Robot robot, RabbitTemplate rabbitTemplate) {
        super(simpUserRegistry);
        this.robot = robot;
        this.rabbitTemplate = rabbitTemplate;
    }

    @MessageMapping("/income")
    @SendToUser("/queue/income")
    public MessageResult<IncomeCommand> create(@RequestBody IncomeParam param, Principal principal) {
        IncomeCommand command = param.getCommand();
        Pair<IncomeCommand, InComeCreateDTO> pair = robot.autoIncome(command, param.getParam(), principal.getName());
        if (command.equals(IncomeCommand.CANCEL)) {
            log.info("{} 取消这次新增收入的操作", principal.getName());
        } else if (command.equals(IncomeCommand.FINISH)) {
            String exchange = ConfigUtils.getProperty(RABBITMQ_NAMESPACE, AppProperties.Rabbitmq.DIRECT_EXCHANGE_POWER, "");
            String key = ConfigUtils.getProperty(RABBITMQ_NAMESPACE, AppProperties.Rabbitmq.DIRECT_KEY_CREATE_INCOME, "");
            String id = UUID.randomUUID().toString();
            rabbitTemplate.convertAndSend(exchange, key, new RabbitMessage<>(Long.parseLong(principal.getName()), id, pair.getSecond()));
        }
        return MessageResult.of(pair.getFirst());
    }
}
