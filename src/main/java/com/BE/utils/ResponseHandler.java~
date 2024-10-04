package com.BE.utils;


import com.BE.model.response.DataResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;


@Component
public class ResponseHandler<T> {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    public ResponseEntity<DataResponseDTO<T>> response(int statusCode, String message, T data) {
        return ResponseEntity.ok(new DataResponseDTO<T>(statusCode, message, data));
    }

    public void responseResolver(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        resolver.resolveException(request, response, null, exception);
    }
}
