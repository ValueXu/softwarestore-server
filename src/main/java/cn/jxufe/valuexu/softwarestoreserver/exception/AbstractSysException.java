package cn.jxufe.valuexu.softwarestoreserver.exception;

import cn.jxufe.valuexu.softwarestoreserver.config.ResCodeEnum;

public abstract class AbstractSysException extends Exception {
    AbstractSysException() {
        super();
    }

    AbstractSysException(ResCodeEnum resCodeEnum) {
        super(resCodeEnum.getMsg());
    }

    AbstractSysException(ResCodeEnum resCodeEnum, Throwable cause) {
        this(resCodeEnum.getMsg(), cause);
    }

    AbstractSysException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
