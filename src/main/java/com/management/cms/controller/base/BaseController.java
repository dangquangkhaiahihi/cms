package com.management.cms.controller.base;

import com.management.cms.constant.Commons;
import com.management.cms.model.response.BaseResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        List<String> listError = new ArrayList<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(
                        (error) -> {
                            String fieldName = ((FieldError) error).getField();
                            String errorMessage = error.getDefaultMessage();
                            listError.add(errorMessage);
                            errors.put(fieldName, errorMessage);
                        });
        String stringError = "";
        if (listError.size() > 0) {
            String[] arrayError = new String[listError.size()];
            listError.toArray(arrayError);
            stringError = StringUtils.join(arrayError, ",");
        }
        BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
        baseResponse.setData(errors);
        baseResponse.setDesc(stringError);
        return ResponseEntity.badRequest().body(baseResponse);
    }
}
