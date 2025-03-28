package com.tuituidan.openhub.exception;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * ExceptionBody.
 *
 * @author zhujunhan
 * @version 1.0
 * @date 2025-03-28
 */
@Getter
@Setter
@AllArgsConstructor
public class ExceptionBody implements Serializable {

    private static final long serialVersionUID = 4127127070319148802L;

    /**
     * ExceptionBody
     *
     * @param status status
     * @param message message
     */
    public ExceptionBody(HttpStatus status, String message) {
        this.code = status.value();
        this.message = message;
    }

    private int code;

    private String message;

}
