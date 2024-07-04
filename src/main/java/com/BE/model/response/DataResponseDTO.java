package com.BE.model.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataResponseDTO<T> {
    private int statusCode;
    private String message;
    private T data;
}
