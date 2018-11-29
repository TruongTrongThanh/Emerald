package org.emerald.comicapi.model.common;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.WebRequest;

import lombok.Getter;

import javax.servlet.http.HttpServletRequest;

@Getter
@JsonInclude(Include.NON_EMPTY)
public class ResponseFormat {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private String error;
    private Object data;
    private String path;

    public ResponseFormat(int statusCode, @Nullable String message, @Nullable Object data, Object path) {
        timestamp = LocalDateTime.now();
        this.status = statusCode;
        setMessage(message);
        setData(data);
        setPath(path);
    }
    public ResponseFormat(int statusCode, Object path) {
        this(statusCode, null, null, path);
    }
    public ResponseFormat(int statusCode, String message, Object path) {
        this(statusCode, message, null, path);
    }
    public ResponseFormat(int statusCode, Object data, Object path) {
        this(statusCode, null, data, path);
    }

    private void setMessage(@Nullable String value) {
        HttpStatus httpStatus = HttpStatus.resolve(this.status);
        this.message = value == null ? httpStatus.getReasonPhrase() : value;
        if (httpStatus.isError())
            this.error = httpStatus.getReasonPhrase();
    }
    private void setData(@Nullable Object data) {
        if (data instanceof Optional) {
            Optional<?> opt = (Optional<?>)data;
            this.data = opt.orElse(null);
        }
        else if (data instanceof List) {
            List<?> list = (List<?>)data;
            this.data = list.isEmpty() ? null : list;
        }
        else
            this.data = data;
    }
    private void setPath(Object path) {
        if (path instanceof String) {
            this.path = path.toString();
        }
        else if (path instanceof WebRequest) {
            WebRequest request = (WebRequest)path;
            this.path = request.getDescription(false).replaceFirst("uri=", "");
        }
        else if (path instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest)path;
            this.path = request.getRequestURI();
        }
    }
}