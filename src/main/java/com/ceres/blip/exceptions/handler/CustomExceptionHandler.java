package com.ceres.blip.exceptions.handler;
import com.ceres.blip.exceptions.ExpiredJwtException;
import com.ceres.blip.dtos.OperationReturnObject;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {
    @ExceptionHandler(value = IllegalArgumentException.class)
    private OperationReturnObject illegalArgument(IllegalArgumentException e) {
        e.printStackTrace();
        return new OperationReturnObject(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
    }

    @ExceptionHandler(value = IllegalStateException.class)
    private OperationReturnObject illegalState(IllegalStateException e) {
        e.printStackTrace();
        return new OperationReturnObject(HttpStatus.BAD_REQUEST.value(), e.getMessage(),null);
    }

    @ExceptionHandler(value = Exception.class)
    private OperationReturnObject exception(Exception e) {
        e.printStackTrace();
        return new OperationReturnObject(400, e.getMessage(),null);
    }

    @ExceptionHandler(value = RuntimeException.class)
    private OperationReturnObject runtimeException(Exception e) {
        e.printStackTrace();
        return new OperationReturnObject(400, e.getMessage(),null);
    }

    @ExceptionHandler(value = {ExpiredJwtException.class, io.jsonwebtoken.ExpiredJwtException.class})
    private OperationReturnObject expiredOtp(ExpiredJwtException e) {
        e.printStackTrace();
        return new OperationReturnObject(400, e.getMessage(),null);
    }

    @ExceptionHandler(value = NoSuchElementException.class)
    private OperationReturnObject noSuchElementOtp(NoSuchElementException e) {
        e.printStackTrace();
        return new OperationReturnObject(400, e.getMessage(),null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private OperationReturnObject invalidMethodArgException(MethodArgumentNotValidException e) {
        e.printStackTrace();
        List<String> errors = e.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
        return new OperationReturnObject(400, e.getMessage(),errors);
    }

    @ExceptionHandler(TypeMismatchException.class)
    public OperationReturnObject handleTypeMismatchException(TypeMismatchException ex) {
        String detail = ex.getMessage();
        return new OperationReturnObject(400, ex.getMessage(),detail);
    }
}

