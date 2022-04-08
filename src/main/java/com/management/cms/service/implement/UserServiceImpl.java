package com.management.cms.service.implement;

import com.management.cms.constant.ESystemEmail;
import com.management.cms.model.dto.UserDto;
import com.management.cms.model.enitity.UserDoc;
import com.management.cms.model.request.UserSaveRequest;
import com.management.cms.model.request.UserSearchRequest;
import com.management.cms.repository.UserRepository;
import com.management.cms.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    UserRepository userRepository;

    @Override
    public void createNewUser(UserSaveRequest userSaveRequest) {

    }

    @Override
    public void editUser(UserSaveRequest userSaveRequest) {

    }

    @Override
    public Page<UserDto> searchAllUser(UserSearchRequest userSearchRequest, Pageable pageable) {
        return null;
    }

    @Override
    public UserDto getUserDetailById(Long id) {
        return null;
    }

    @Override
    public void sendMailToUser(String mailto, Set<String> mailCc, Map<String, Object> mapParams, ESystemEmail.ESystemMail eSystemMail) {

    }

    @Override
    public void updateLastLoginAndFailCount(String userName, Boolean status) {
        logger.info("Start update count login");
        Optional<UserDoc> optional = userRepository.findByEmail(userName);
        if(!optional.isPresent()) {
            logger.info("user is null");
            return;
        }
        UserDoc userDoc = optional.get();
        if(status){
            userDoc.setFailCount(0);
        }else{
            userDoc.setFailCount(userDoc.getFailCount()+1);
        }
        userRepository.save(userDoc);
    }
}
