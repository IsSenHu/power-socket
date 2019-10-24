package com.cdsen.powersocket.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.cdsen.interfaces.config.service.SelfConfigApiService;
import com.cdsen.interfaces.config.vo.SelfConfig;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author HuSen
 * create on 2019/10/24 14:47
 */
@Service
@Component
public class SelfConfigApiServiceImpl implements SelfConfigApiService {

    private static final String DESTINATION = "/topic/selfConfig";

    private final SimpMessagingTemplate simpMessagingTemplate;

    public SelfConfigApiServiceImpl(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void push(Long userId, Map<String, List<SelfConfig>> selfConfig) {
        simpMessagingTemplate.convertAndSendToUser(userId.toString(), DESTINATION, selfConfig);
    }
}
