package com.alibaba.mos.util;

/**
 * 自定义异常处理
 */
public class ExcelReadException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ExcelReadException() {
        super();
    }

    public ExcelReadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ExcelReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExcelReadException(String message) {
        super(message);
    }

    public ExcelReadException(Throwable cause) {
        super(cause);
    }
}
