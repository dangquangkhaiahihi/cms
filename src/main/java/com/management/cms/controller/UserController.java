package com.management.cms.controller;

import com.management.cms.constant.Commons;
import com.management.cms.model.dto.UserDto;
import com.management.cms.model.request.*;
import com.management.cms.model.response.BaseResponse;
import com.management.cms.service.UserService;
import com.management.cms.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;

    Utils utils = new Utils();

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

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody UserSaveRequest userSaveRequest,
                                    @PathVariable Long id) {

        try {
            userService.editUser(userSaveRequest,id);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
            baseResponse.setData("Chỉnh sửa người dùng thành công");
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc(e.getMessage());
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }

    @GetMapping()
    public ResponseEntity<?> search(@RequestParam(value = "email", required = false, defaultValue = "") String email,
                                    @RequestParam(value = "phone", required = false, defaultValue = "") String phone,
                                    @RequestParam(value = "ssn", required = false, defaultValue = "") String ssn,
                                    @RequestParam(value = "status", required = false, defaultValue = "2") Integer status,
                                    // status = 0 -> false, status = 1 -> true, status = 2 -> all
                                    @RequestParam(value = "area", required = false, defaultValue = "") String area,
                                    @RequestParam(value = "role", required = false, defaultValue = "") String role,
                                    @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                    @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) {
        UserSearchRequest userSearchRequest = new UserSearchRequest();
        userSearchRequest.setEmail(email);
        userSearchRequest.setPhoneNumber(phone);
        userSearchRequest.setSocialSecurityNum(ssn);
        userSearchRequest.setArea(area);
        userSearchRequest.setEnabled(status);
        userSearchRequest.setRole(role);

        Sort sort = null;
        sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        try {
            Page<UserDto> users = userService.searchAllUser(userSearchRequest, pageable);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
            baseResponse.setData(users);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc(e.getMessage());
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDetail(@PathVariable Long id) {
        try {
            UserDto userDto = userService.getUserDetailById(id);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
            baseResponse.setData(userDto);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc(e.getMessage());
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }

    @PutMapping("/lockAndUnlock/{id}")
    public ResponseEntity<?> lockAndUnlock(@PathVariable Long id) {
        try {
            String message = userService.lockAndUnlockById(id);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
            baseResponse.setData(message);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc(e.getMessage());
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }

    @PutMapping("/reset_password/{id}")
    public ResponseEntity<?> resetPass(@PathVariable Long id) {
        try {
            userService.resetPassword(id);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
            baseResponse.setData("Resset pass successfully");
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc(e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/change_password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePassRequest changePassRequest) {
        String result;
        try {
            result = userService.changePassword(changePassRequest);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
            baseResponse.setData(result);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc(e.getMessage());
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }

    @GetMapping(value = "/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) throws Exception {
        try {
            String token = utils.parseJwt(request);
            System.out.println("cacacacac " + token);
            userService.logout(token);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc(e.getMessage());
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }
}
