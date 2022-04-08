package com.management.cms.controller;

import com.management.cms.constant.Commons;
import com.management.cms.model.enitity.UserDoc;
import com.management.cms.model.request.UserSaveRequest;
import com.management.cms.model.response.BaseResponse;
import com.management.cms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping()
    public ResponseEntity<?> save(@Validated @RequestBody UserSaveRequest userSaveRequest) {
        try {
            userService.createNewUser(userSaveRequest);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
            baseResponse.setData("Tạo người dùng thành công");
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc(e.getMessage());
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }
}
