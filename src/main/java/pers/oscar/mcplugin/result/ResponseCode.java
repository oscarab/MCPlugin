package pers.oscar.mcplugin.result;

public enum ResponseCode {
    OK(200, "成功"),
    ERROR(500, "内部错误"),
    NOT_FOUND(601, "找不到所请求的资源"),
    ACCESS_DENIED(602, "无权访问"),
    LOGIN_FAIL(603, "用户名或密码错误"),
    REGISTER_FAIL(604, "用户名已经存在"),
    POST_FAIL(605, "上传信息有误");

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
