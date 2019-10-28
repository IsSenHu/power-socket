package com.cdsen.powersocket.controller.consumption;

import lombok.Data;

/**
 * @author HuSen
 * create on 2019/10/28 11:28
 */
@Data
class ConsumptionParam {
    private ConsumptionCommand command;
    private String param;
}
