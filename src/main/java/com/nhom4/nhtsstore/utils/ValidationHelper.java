package com.nhom4.nhtsstore.utils;

import com.nhom4.nhtsstore.common.FieldValidationError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ValidationHelper {

    private final Validator validator;

    public ValidationHelper(Validator validator) {
        this.validator = validator;
    }

    public <T> List<FieldValidationError> validateAndCollectErrors(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        return violations.stream()
                .map(v -> new FieldValidationError(getPropertyName(v.getPropertyPath()), v.getMessage()))
                .collect(Collectors.toList());
    }

    private String getPropertyName(Path path) {
        // Trả về tên field cuối cùng trong path (trong trường hợp nested)
        String fullPath = path.toString();
        int lastDot = fullPath.lastIndexOf('.');
        return lastDot != -1 ? fullPath.substring(lastDot + 1) : fullPath;
    }
}
