package com.management.cms.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    //        registry.addInterceptor(newSecurityInterceptor()).addPathPatterns("/account/login");
    registry.addInterceptor(new TokenInterceptor());
    registry.addInterceptor(new ExecuteTimeInterceptor());
  }
}
