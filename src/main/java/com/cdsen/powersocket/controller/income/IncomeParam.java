package com.cdsen.powersocket.controller.income;

import lombok.Data;

/**
 * @author HuSen
 * create on 2019/10/29 10:04
 */
@Data
public class IncomeParam {
    private IncomeCommand command;
    private String param;
}
