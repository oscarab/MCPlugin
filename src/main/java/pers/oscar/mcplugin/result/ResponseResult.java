package pers.oscar.mcplugin.result;

import lombok.Data;

@Data
public class ResponseResult {
    private int status;
    private String message;
    private Object content;

    public static ResponseResult success(Object data) {
        ResponseResult result = new ResponseResult();
        result.setStatus(ResponseCode.OK.getCode());
        result.setMessage(ResponseCode.OK.getMessage());
        result.setContent(data);
        return result;
    }

    public static ResponseResult fail(ResponseCode code) {
        return fail(code, code.getMessage());
    }

    public static ResponseResult fail(ResponseCode code, String message) {
        ResponseResult result = new ResponseResult();
        result.setStatus(code.getCode());
        result.setMessage(message);
        return result;
    }
}
