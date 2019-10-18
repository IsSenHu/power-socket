package com.cdsen.powersocket.message;

import lombok.Getter;
import lombok.Setter;

/**
 * @author HuSen
 * create on 2019/10/15 11:34
 */
@Getter
@Setter
public class MessageResult<T> {

    private int code;
    private String error;
    private T data;

    public static <T> MessageResult<T> of(T d) {
        MessageResult<T> result = new MessageResult<>();
        result.setCode(0);
        result.setData(d);
        return result;
    }

    public static <T> MessageResult<T> of(int code, String error) {
        MessageResult<T> result = new MessageResult<>();
        result.setCode(code);
        result.setError(error);
        return result;
    }

    public static <T> MessageResult<T> success() {
        MessageResult<T> result = new MessageResult<>();
        result.setCode(0);
        return result;
    }

    public static <T> MessageResult<T> of(MessageError error) {
        MessageResult<T> result = new MessageResult<>();
        result.setCode(error.getCode());
        result.setError(error.getError());
        return result;
    }
}
