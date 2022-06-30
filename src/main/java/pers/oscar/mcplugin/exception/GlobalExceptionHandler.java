package pers.oscar.mcplugin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pers.oscar.mcplugin.result.ResponseCode;
import pers.oscar.mcplugin.result.ResponseResult;

/**
 * 全局异常处理器
 *
 * <p>处理可能出现的各种业务异常
 *
 * @author Oscar_Wen
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 未找到资源的相关错误处理
     * @param e 错误
     * @return 返回信息
     */
    @ExceptionHandler(value = ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult handleResourceNotFound(ResourceNotFoundException e) {
        return ResponseResult.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult handleArgument(MethodArgumentNotValidException e) {
        String error = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseResult.fail(ResponseCode.POST_FAIL, error);
    }

    /**
     * 发表时的错误
     * @param e 异常
     * @return 返回信息
     */
    @ExceptionHandler(value = PostException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult handlePostFail(PostException e) {
        return ResponseResult.fail(ResponseCode.POST_FAIL, e.getMessage());
    }

    @ExceptionHandler(value = AccessDenyException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult handlePostFail(AccessDenyException e) {
        return ResponseResult.fail(ResponseCode.ACCESS_DENIED, e.getMessage());
    }

    /**
     * 处理登录注册时的错误
     * @param e 错误
     * @return 返回信息
     */
    @ExceptionHandler(value = AuthException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult handleAuthFail(AuthException e) {
        return ResponseResult.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult handleNullPointer(NullPointerException e) {
        return ResponseResult.fail(ResponseCode.ERROR, e.getMessage());
    }
}
