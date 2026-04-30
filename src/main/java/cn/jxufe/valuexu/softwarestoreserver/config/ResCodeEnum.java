package cn.jxufe.valuexu.softwarestoreserver.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResCodeEnum {
    SUCCESS(0, "success"),
    UNKNOWN_ERR(-1, "未知错误");
    private final int code;
    private final String msg;
}
