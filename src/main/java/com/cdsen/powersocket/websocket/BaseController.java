package com.cdsen.powersocket.websocket;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;

/**
 * @author HuSen
 * create on 2019/10/15 17:52
 */
public class BaseController {

    @MessageExceptionHandler
    @SendToUser(destinations = "/queue/errors", broadcast = false)
    public MessageResult handleException(Exception ex) {
        return MessageResult.of(999999999, ex.getMessage());
    }
}
