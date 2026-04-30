package cn.jxufe.valuexu.softwarestoreserver.dto;

import cn.jxufe.valuexu.softwarestoreserver.config.ResCodeEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WrappedResDto<T> {
    private int code;
    private String msg;
    private T result;

    public WrappedResDto() {
        this.code = ResCodeEnum.SUCCESS.getCode();
        this.msg = ResCodeEnum.SUCCESS.getMsg();
    }

    public WrappedResDto(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public WrappedResDto(int code, String msg, T result) {
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    public WrappedResDto(ResCodeEnum resCodeEnum) {
        this(resCodeEnum.getCode(), resCodeEnum.getMsg());
    }

    public WrappedResDto(ResCodeEnum resCodeEnum, T result) {
        this(resCodeEnum.getCode(), resCodeEnum.getMsg(), result);
    }

    public static <A> WrappedResDto<A> success() {
        return new WrappedResDto<>();
    }

    public static <A> WrappedResDto<A> success(A content) {
        WrappedResDto<A> wrappedResDto = new WrappedResDto<>();
        wrappedResDto.setResult(content);
        return wrappedResDto;
    }

    public static <A> WrappedResDto<A> failed(ResCodeEnum resCodeEnum) {
        return new WrappedResDto<>(resCodeEnum);
    }

    public static <A> WrappedResDto<A> failed(ResCodeEnum resCodeEnum, A content) {
        return new WrappedResDto<>(resCodeEnum, content);
    }
}
