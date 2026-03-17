package org.example.zhao.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.zhao.common.api.R;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public R<Void> biz(BizException e) {
        return R.fail(e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public R<Void> valid(Exception e) {
        return R.fail("参数校验失败");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<Void> body(HttpMessageNotReadableException e) {
        return R.fail("请求体格式错误");
    }

    @ExceptionHandler(Exception.class)
    public R<Void> other(Exception e) {
        log.error("Unhandled exception", e);
        return R.fail("系统异常");
    }
}

