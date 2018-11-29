package org.emerald.comicapi.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.emerald.comicapi.config.GlobalVariable;
import org.emerald.comicapi.exception.EntityNotFoundException;
import org.emerald.comicapi.model.common.ResponseFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class RestErrorHandler extends ResponseEntityExceptionHandler {

    @Autowired
    GlobalVariable globalVariable;

    private ResponseFormat handleValidationError(
            BindingResult bindingResult,
            HttpStatus status,
            WebRequest request)
    {
        Map<String,Object> map = new HashMap<>();
        List<String> globalErrorList = new ArrayList<>();

        for (ObjectError err : bindingResult.getAllErrors()) {
            if (err instanceof FieldError) {
                FieldError e = (FieldError)err;
                map.put(e.getField(), e.getDefaultMessage());
            }
            else {
                globalErrorList.add(err.getDefaultMessage());
            }
        }
        if (!globalErrorList.isEmpty())
            map.put("globalErrors", globalErrorList);

        return new ResponseFormat(status.value(), "Invalid values in some fields", map, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request)
    {
        ResponseFormat body = handleValidationError(ex.getBindingResult(), status, request);
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(
            BindException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request)
    {
        ResponseFormat body = handleValidationError(ex.getBindingResult(), status, request);
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    @ExceptionHandler(value = {EntityNotFoundException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ResponseFormat handleResourceNotFound(
        EntityNotFoundException ex, WebRequest request)
    {
        String message = ex.getEntityClass().getSimpleName() + " not found";
        return new ResponseFormat(404, message, request);
    }

    @ExceptionHandler(value = {MaxUploadSizeExceededException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseFormat handleMaxUploadSize(
        MaxUploadSizeExceededException ex, WebRequest request)
    {
        String message = "The request was rejected because its size exceeds the maximum: " + globalVariable.SIZE_LIMIT;
        return new ResponseFormat(400, message, request);
    }
}