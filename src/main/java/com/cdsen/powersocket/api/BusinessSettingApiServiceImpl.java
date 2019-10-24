package com.cdsen.powersocket.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.cdsen.interfaces.config.service.BusinessSettingApiService;
import com.cdsen.interfaces.config.vo.BusinessSetting;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author HuSen
 * create on 2019/10/24 14:19
 */
@Service
@Component
public class BusinessSettingApiServiceImpl implements BusinessSettingApiService {

    private static final String DESTINATION = "/topic/businessSetting";

    private final SimpMessagingTemplate simpMessagingTemplate;

    public BusinessSettingApiServiceImpl(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void push(Map<String, List<BusinessSetting>> changedMap) {
        simpMessagingTemplate.convertAndSend(DESTINATION, changedMap);
    }
}
