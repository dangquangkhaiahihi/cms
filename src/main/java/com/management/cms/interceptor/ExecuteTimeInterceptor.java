package com.management.cms.interceptor;

import com.management.cms.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class ExecuteTimeInterceptor implements HandlerInterceptor {

  // before the actual handler will be executed
  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    log.info(
        "[{}] Process API with parameter : {}",
        request.getRequestURI(),
        Utils.getWmfGson().toJson(request.getParameterMap()));
    long startTime = System.currentTimeMillis();
    request.setAttribute("startTime", startTime);
    return true;
  }

  // after the handler is executed
  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    long startTime = (Long) request.getAttribute("startTime");
    long endTime = System.currentTimeMillis();
    long executeTime = endTime - startTime;

    //        if(log.isDebugEnabled()){
    //            log.debug("[{}] {} executeTime : {} ms",request.getRequestURI(),
    // handler,executeTime);
    //        }

    log.info("[{}] ExecuteTime : {} ms", request.getRequestURI(), executeTime);
  }
}
