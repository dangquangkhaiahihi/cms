package com.management.cms.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
  @NotBlank(message = "Vui lòng nhập username")
  private String username;

  @NotBlank(message = "Vui lòng nhập mật khẩu")
  private String password;
}
