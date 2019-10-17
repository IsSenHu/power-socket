package com.cdsen.powersocket.module.connection.message;

import com.cdsen.powersocket.websocket.MessageError;
import lombok.Getter;

/**
 * @author HuSen
 * create on 2019/10/15 11:47
 */
@Getter
public enum ConnectionError implements MessageError {
    //
    CAN_NOT_ACCESS(10001, "can not access!"),
    INFO_NOT_MATCH(10002, "info not match!");

    private int code;
    private String error;

    ConnectionError(int code, String error) {
        this.code = code;
        this.error = error;
    }
}
