package com.sankha.userService.exceptions;

import com.sankha.userService.dto.ErrorResponse;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse handleUserAlreadyExist(HttpServletRequest httpRequest, Exception exception) {
        return new ErrorResponse(httpRequest.getRequestURI(), exception.getMessage());
    }

//    @ExceptionHandler(InsufficientWalletBalance.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    ErrorResponse handleInsufficientWalletBalance(HttpServletRequest httpRequest, Exception exception) {
//        return new ErrorResponse(httpRequest.getRequestURI(), exception.getMessage());
//    }

    @ExceptionHandler(value = SignatureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ErrorResponse handleTokenSignatureException(HttpServletRequest httpRequest, Exception exception) {
        return new ErrorResponse(httpRequest.getRequestURI(), exception.getMessage());
    }

    @ExceptionHandler(value = UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ErrorResponse handleUsernameNotFoundException(HttpServletRequest httpRequest, Exception exception) {
        return new ErrorResponse(httpRequest.getRequestURI(), exception.getMessage());
    }

}
