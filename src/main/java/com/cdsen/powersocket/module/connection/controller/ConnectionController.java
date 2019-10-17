package com.cdsen.powersocket.module.connection.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cdsen.apollo.AppProperties;
import com.cdsen.apollo.ConfigUtils;
import com.cdsen.powersocket.websocket.BaseController;
import com.cdsen.powersocket.websocket.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

/**
 * @author HuSen
 * create on 2019/10/11 11:45
 */
@Slf4j
@RestController
public class ConnectionController extends BaseController {

    private final SimpUserRegistry simpUserRegistry;

    public ConnectionController(SimpMessagingTemplate messagingTemplate, SimpUserRegistry simpUserRegistry) {
        this.simpUserRegistry = simpUserRegistry;
    }

    @MessageMapping("/connecting")
    @SendTo("/topic/connected")
    public Message<MessageResult<String>> greeting(Message message, Principal principal) {
        return new GenericMessage<>(MessageResult.of(principal.getName()));
    }

    @MessageMapping("/test")
    @SendToUser("/queue/position-updates")
    public Message<MessageResult<String>> test(Principal principal) {
        int userCount = simpUserRegistry.getUserCount();
        log.info("当前在线的人数:{}", userCount);
        return new GenericMessage<>(MessageResult.of("测试一下"));
    }

    private String getToken(MessageHeaders headers) {
        try {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(headers));
            JSONObject nativeHeaders = jsonObject.getJSONObject("nativeHeaders");
            String header = ConfigUtils.getProperty(AppProperties.Security.HEADER, "authorization");
            JSONArray jsonArray = nativeHeaders.getJSONArray(header);
            List<String> token = jsonArray.toJavaList(String.class);
            return token.isEmpty() ? null : token.get(0);
        } catch (Exception e) {
            return null;
        }
    }
}
