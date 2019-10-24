package com.cdsen.powersocket.websocket;

import com.cdsen.powersocket.message.MessageResult;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;

/**
 * @author HuSen
 * create on 2019/10/11 11:45
 */
public class BaseController {

    private final SimpUserRegistry simpUserRegistry;

    public BaseController(SimpUserRegistry simpUserRegistry) {
        this.simpUserRegistry = simpUserRegistry;
    }

    @MessageExceptionHandler
    @SendToUser(destinations = "/queue/error", broadcast = false)
    public MessageResult handleException(Exception ex) {
        return MessageResult.of(999999999, ex.getMessage());
    }

    protected SimpUserRegistry simpUserRegistry() {
        return this.simpUserRegistry;
    }
}
