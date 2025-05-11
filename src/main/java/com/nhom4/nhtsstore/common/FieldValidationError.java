package com.nhom4.nhtsstore.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldValidationError {
    private String field;
    private String message;
}
