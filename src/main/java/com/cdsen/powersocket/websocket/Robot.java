package com.cdsen.powersocket.websocket;

import com.cdsen.powersocket.controller.consumption.ConsumptionCommand;
import com.cdsen.rabbit.model.ConsumptionCreateDTO;
import com.cdsen.rabbit.model.ConsumptionItemCreateDTO;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author HuSen
 * create on 2019/10/28 10:00
 */
@Component
public class Robot {

    private static final ConcurrentMap<String, ConsumptionCreateDTO> CREATE_DTO_CONCURRENT_MAP = new ConcurrentHashMap<>();

    public Pair<ConsumptionCommand, ConsumptionCreateDTO> autoConsumption(ConsumptionCommand command, String param, String userId) {
        ConsumptionCreateDTO dto = CREATE_DTO_CONCURRENT_MAP.getOrDefault(userId, new ConsumptionCreateDTO());
        CREATE_DTO_CONCURRENT_MAP.putIfAbsent(userId, dto);
        ConsumptionCommand next = null;
        switch (command) {
            case SET_TIME: {
                dto.setTime(param);
                next = ConsumptionCommand.SET_CURRENCY;
                break;
            }
            case SET_CURRENCY: {
                dto.setCurrency(param);
                next = ConsumptionCommand.SET_MONEY;
                break;
            }
            case SET_MONEY: {
                List<ConsumptionItemCreateDTO> items = dto.getItems();
                if (items == null) {
                    items = new ArrayList<>();
                    dto.setItems(items);
                }
                ConsumptionItemCreateDTO item = new ConsumptionItemCreateDTO();
                item.setMoney(new BigDecimal(param));
                items.add(item);
                next = ConsumptionCommand.SET_DESC;
                break;
            }
            case SET_DESC: {
                List<ConsumptionItemCreateDTO> items = dto.getItems();
                ConsumptionItemCreateDTO now = items.get(items.size() - 1);
                now.setDescription(param);
                next = ConsumptionCommand.SET_TYPE;
                break;
            }
            case SET_TYPE:
                List<ConsumptionItemCreateDTO> items = dto.getItems();
                ConsumptionItemCreateDTO now = items.get(items.size() - 1);
                now.setType(Integer.parseInt(param));
                next = ConsumptionCommand.SET_MONEY;
                break;
            case CANCEL: {
                CREATE_DTO_CONCURRENT_MAP.remove(userId);
                next = ConsumptionCommand.CANCEL;
                break;
            }
            case FINISH: {
                CREATE_DTO_CONCURRENT_MAP.remove(userId);
                next = ConsumptionCommand.FINISH;
                break;
            }
            default:
        }
        return Pair.of(next, dto);
    }
}
