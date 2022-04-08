package com.management.cms.exception;

import com.management.cms.constant.Commons;
import com.management.cms.model.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

  @ExceptionHandler(value = {DataInvalidException.class})
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ResponseBody
  public BaseResponse handleExceptionBadRequest(Exception e, HttpServletRequest request) {
    BaseResponse response = BaseResponse.parse(Commons.SVC_ERROR_99).setMessage(e.getMessage());
    log.error("[{}]Handler Exception Bad request : {}", request.getRequestURI(), e.getMessage());
    return response;
  }

  @ExceptionHandler(value = {DataNotFoundException.class})
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  @ResponseBody
  public BaseResponse handleExceptionNotFound(Exception e, HttpServletRequest request) {
    BaseResponse response = BaseResponse.parse(Commons.SVC_ERROR_99).setMessage(e.getMessage());
    log.error("[{}]Handler Exception Notfound data : {}", request.getRequestURI(), e.getMessage());
    return response;
  }

  @ExceptionHandler(value = {TokenInvalidException.class})
  @ResponseStatus(value = HttpStatus.FORBIDDEN)
  @ResponseBody
  public BaseResponse handleExceptionTokenInvalid(Exception e, HttpServletRequest request) {
    BaseResponse response = BaseResponse.parse(Commons.SVC_ERROR_99).setMessage(e.getMessage());
    log.error("[{}]Handler Exception Token invalid : {}", request.getRequestURI(), e.getMessage());
    return response;
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  public BaseResponse handleExceptionInternalServer(Exception e, HttpServletRequest request) {
    BaseResponse response = BaseResponse.parse(Commons.SVC_ERROR_99).setMessage(e.getMessage());
    log.error("[{}]Handler Exception Internal server : {}", request.getRequestURI(), e.getMessage());
    return response;
  }
}
