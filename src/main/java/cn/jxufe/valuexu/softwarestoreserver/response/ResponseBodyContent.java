package cn.jxufe.valuexu.softwarestoreserver.response;

public class ResponseBodyContent<T> {
    private int code;
    private String msg;
    private T result;

    public ResponseBodyContent() {
        this.code = 1;
        this.msg = "success";
    }

    public ResponseBodyContent(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseBodyContent(int code, String msg, T result) {
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
