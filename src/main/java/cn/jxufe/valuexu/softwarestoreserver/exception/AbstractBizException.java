package cn.jxufe.valuexu.softwarestoreserver.exception;

import cn.jxufe.valuexu.softwarestoreserver.config.ResCodeEnum;

public abstract class AbstractBizException extends Exception{
    AbstractBizException() {
        super();
    }

    AbstractBizException(ResCodeEnum resCodeEnum) {
        super(resCodeEnum.getMsg());
    }

    AbstractBizException(ResCodeEnum resCodeEnum, Throwable cause) {
        super(resCodeEnum.getMsg(), cause);
    }
}
