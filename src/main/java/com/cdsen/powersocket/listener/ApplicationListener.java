package com.cdsen.powersocket.listener;

import com.cdsen.apollo.ConfigUtils;
import com.ctrip.framework.apollo.model.ConfigChange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author HuSen
 * create on 2019/10/12 16:16
 */
@Slf4j
@Configuration
public class ApplicationListener {

    @EventListener(ApplicationStartedEvent.class)
    public void started(ApplicationStartedEvent event) {
        addConfigChangeListener();
    }

    private final AtomicBoolean addConfigChangeListened = new AtomicBoolean(false);
    private void addConfigChangeListener() {
        if (!addConfigChangeListened.getAndSet(true)) {
            ConfigUtils.addChangeListener(changeEvent -> {
                log.info("Changes for namespace {}", changeEvent.getNamespace());
                for (String changedKey : changeEvent.changedKeys()) {
                    ConfigChange change = changeEvent.getChange(changedKey);
                    log.info("Found change - key: {}, oldValue: {}, newValue: {}, changeType: {}", change.getPropertyName(), change.getOldValue(), change.getNewValue(), change.getChangeType());
                }
            });
        }
    }
}
