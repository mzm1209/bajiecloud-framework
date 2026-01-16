package com.bajiezu.cloud.common.esign;

import lombok.Getter;

import java.io.Serial;

/**
 * description 自定义全局异常
 */
@Getter
public class DefineException extends Exception {

    @Serial
    private static final long serialVersionUID = 4359180081622082792L;
    private Exception e;

    public DefineException(String msg) {
        this.e = new Exception(msg);
    }

    public void setE(Exception e) {
        this.e = e;
    }


}
