package com.management.cms.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChangePassRequest {
    @NotBlank(message = "Chưa nhập mật khẩu cũ")
    private String passwordOld;
    @NotBlank(message = "Chưa nhập mật khẩu mới")
    private String passwordNew;
    @NotBlank(message = "Vui lòng nhập lại mật khẩu mới")
    private String rePassword;
}
