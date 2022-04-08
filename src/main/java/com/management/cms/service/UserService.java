package com.management.cms.service;

import com.management.cms.constant.ESystemEmail;
import com.management.cms.model.dto.UserDto;
import com.management.cms.model.request.UserSaveRequest;
import com.management.cms.model.request.UserSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.Set;

public interface UserService {
    void createNewUser(UserSaveRequest userSaveRequest) throws Exception;

    void editUser(UserSaveRequest userSaveRequest) throws Exception;

    Page<UserDto> searchAllUser(UserSearchRequest userSearchRequest, Pageable pageable);

    UserDto getUserDetailById(Long id) throws Exception;

    void sendMailToUser(String mailto, Set<String> mailCc, Map<String, Object> mapParams, ESystemEmail eSystemMail);

    void updateLastLoginAndFailCount(String username, Boolean status);

    void lockAndUnlockById(Long id) throws Exception;
}
