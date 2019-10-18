package com.cdsen.powersocket.controller;

import com.cdsen.powersocket.message.MessageResult;
import com.cdsen.powersocket.websocket.BaseController;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HuSen
 * create on 2019/10/18 16:23
 */
@RestController
public class ConsumptionController extends BaseController {

    public ConsumptionController(SimpUserRegistry simpUserRegistry) {
        super(simpUserRegistry);
    }

    @MessageMapping("/test")
    @SendToUser("/queue/router")
    public MessageResult<String> test() {
        return MessageResult.success();
    }
}
