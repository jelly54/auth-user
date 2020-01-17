package com.jelly.authuser.exception;

import com.jelly.authuser.util.CommonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 全局异常拦截
 *
 * @author guodongzhang
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 拦截进入controller 参数绑定异常
     *
     * @param request request请求
     * @param e       参数绑定异常
     * @return spring http response entity
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<CommonResponse> bindExceptionHandler(HttpServletRequest request, BindException e) {
        return new ResponseEntity<>(filterErrorMsg(request, e.getBindingResult().getFieldErrors(), e), HttpStatus.BAD_REQUEST);
    }

    /**
     * 拦截 方法参数异常异常
     *
     * @param request request请求
     * @param e       业务异常
     * @return spring http response entity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse> methodArgumentNotValidExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException e) {
        return new ResponseEntity<>(filterErrorMsg(request, e.getBindingResult().getFieldErrors(), e), HttpStatus.BAD_REQUEST);
    }

    private CommonResponse filterErrorMsg(HttpServletRequest request, List<FieldError> fieldErrors, Exception e) {
        LOG.warn("Capture a data check exception:{}.{}", request.getRequestURI(), e);
        List<String> errorMsgList = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            errorMsgList.add(fieldError.getDefaultMessage());
        }
        return CommonResponse.error(ErrorEnum.INVALID_PARAMS, errorMsgList.toArray()).build();
    }

    /**
     * 拦截业务异常
     *
     * @param request request请求
     * @param e       业务异常
     * @return spring http response entity
     */
    @ExceptionHandler(ServerException.class)
    public ResponseEntity<CommonResponse> businessExceptionHandler(HttpServletRequest request, ServerException e) {
        LOG.warn("Capture a business exception:{}.{}", request.getRequestURI(), e);
        return new ResponseEntity<>(CommonResponse.error(e.getErrno(), e.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

    /**
     * 拦截业务异常
     *
     * @param request request请求
     * @param e       业务异常
     * @return spring http response entity
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<CommonResponse> exceptionHandler(HttpServletRequest request, Exception e) {
        LOG.warn("Capture a global exception {}.{}", request.getRequestURI(), e);
        return new ResponseEntity<>(CommonResponse.error(ErrorEnum.INTERNAL_EXCEPT).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
