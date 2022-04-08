package com.management.cms.service.implement;

import com.management.cms.constant.Commons;
import com.management.cms.constant.ESystemEmail;
import com.management.cms.model.dto.UserDto;
import com.management.cms.model.enitity.UserDoc;
import com.management.cms.model.request.UserSaveRequest;
import com.management.cms.model.request.UserSearchRequest;
import com.management.cms.repository.AreaRepository;
import com.management.cms.repository.RoleRepository;
import com.management.cms.repository.UserRepository;
import com.management.cms.service.GeneratorSeqService;
import com.management.cms.service.UserService;
import com.management.cms.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    AreaRepository areaRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GeneratorSeqService generatorSeqService;

    @Override
    public void createNewUser(UserSaveRequest userSaveRequest) throws Exception{
        //validate input
        userSaveRequest.validateInput();
        //check area code có hợp lệ ko
        for(String areaCode : userSaveRequest.getAreaCodes()){
            if(areaRepository.existsByCode(areaCode)){
                throw new Exception("Mã khu vực không hợp lệ : " + areaCode);
            }
        }

        //tạo đối tượng
        UserDoc userDoc = new UserDoc();
        BeanUtils.copyProperties(userSaveRequest, userDoc);

        userDoc.setPassword(passwordEncoder.encode(Commons.DEFAULT_PASSWORD));
        userDoc.setEnabled(Commons.STATUS_ACTIVE);
        userDoc.setResetPass(Commons.HAVE_NOT_RESET_PASS);
        userDoc.setFailCount(0);

//        //Sau này bổ sung current user để set CreateBy và set UpdateBy
//        UserDoc currentUser = WebUtils.getCurrentUser();
//        userDoc.setCreatedBy(currentUser.getEmail());
//        userDoc.setUpdatedBy(currentUser.getEmail());
        userDoc.setCreatedAt(LocalDateTime.now());
        userDoc.setUpdatedAt(LocalDateTime.now());

        userDoc.setRole(roleRepository.findByCode("USER").get());
        //SET ID CHO PARTNER TRƯỚC KHI LƯU
        userDoc.setId(generatorSeqService.getNextSequenceId(userDoc.SEQUENCE_NAME));
        userRepository.save(userDoc);

        //Gửi mail tạo tài khoản cho user
        Map<String, Object> map = new HashMap<>();
        map.put("fullName", userDoc.getFirstName().concat(" ").concat(userDoc.getLastName()));
        map.put("username", userDoc.getEmail());
        map.put("password", Commons.DEFAULT_PASSWORD);
        sendMailToUser(userDoc.getEmail(), null, map, ESystemEmail.MAIL_CREATE_ACCOUNT);
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
    public void sendMailToUser(String mailto, Set<String> mailCc, Map<String, Object> mapParams, ESystemEmail eSystemMail) {

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

    @Override
    public void lockAndUnlockById(Long id) throws Exception {

    }
}
