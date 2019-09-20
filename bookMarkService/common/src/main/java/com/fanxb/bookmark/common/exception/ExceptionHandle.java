package com.fanxb.bookmark.common.exception;

import com.fanxb.bookmark.common.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/3/19 18:12
 */
@RestControllerAdvice
public class ExceptionHandle {

    private static Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        CustomException ce;
        if (e instanceof MethodArgumentNotValidException) {
            ce = new FormDataException(((MethodArgumentNotValidException) e).getBindingResult().getFieldErrors().get(0).getDefaultMessage());
        } else if (e instanceof CustomException) {
            logger.error("捕获到自定义错误:{}", e.getMessage(), e);
            ce = (CustomException) e;
        } else {
            logger.error("捕获到意外错误:{}", e.getMessage(), e);
            ce = new CustomException("服务器异常");
        }
        return new Result(ce.getCode(), ce.getMessage(), null);
    }
}
