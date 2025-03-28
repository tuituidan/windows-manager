package com.tuituidan.openhub.exception;

import com.tuituidan.openhub.util.StringExtUtils;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * DefaultExceptionHandler.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2025-03-28
 */
@RestControllerAdvice
@Slf4j
public class DefaultExceptionHandler {

    /**
     * 参数校验异常，往往只是用户操作问题，不需要写日志
     * 注意：手动抛异常时，需要写日志的不能使用这些异常.
     *
     * @param ex Exception
     * @return ExceptionBody
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ConversionFailedException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentNotValidException.class,
            BindException.class,
            MaxUploadSizeExceededException.class})
    public ExceptionBody runtimeExceptionHandler(Exception ex) {
        String message = ex.getMessage();
        if (ex instanceof MethodArgumentNotValidException) {
            message = ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors()
                    .stream().map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(","));
        }
        if (ex instanceof BindException) {
            message = ((BindException) ex).getAllErrors()
                    .stream().map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(","));
        }
        if (ex instanceof MaxUploadSizeExceededException) {
            message = "上传文件大小超过限制";
            Throwable throwable = Optional.ofNullable(ex.getCause())
                    .map(Throwable::getCause)
                    .orElse(null);
            if (throwable instanceof FileSizeLimitExceededException) {
                String size = FileUtils.byteCountToDisplaySize(((FileSizeLimitExceededException) throwable)
                        .getPermittedSize());
                message = StringExtUtils.format("{}，最大只允许上传{}", message, size);
            }
        }
        return new ExceptionBody(HttpStatus.BAD_REQUEST.value(), message);
    }

    /**
     * 其他未被捕获的异常.
     *
     * @param throwable 所有的未被捕获或定义的异常
     * @return ExceptionBody
     */
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public ExceptionBody exception(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
        return new ExceptionBody(HttpStatus.INTERNAL_SERVER_ERROR,
                StringUtils.defaultString(throwable.getMessage(), ExceptionUtils.getStackTrace(throwable)));
    }

}
