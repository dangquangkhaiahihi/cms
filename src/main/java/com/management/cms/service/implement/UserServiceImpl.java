package com.management.cms.service.implement;

import com.management.cms.constant.Commons;
import com.management.cms.constant.ESystemEmail;
import com.management.cms.exception.OldPasswordIsWrongException;
import com.management.cms.exception.RePasswordWrongException;
import com.management.cms.exception.UsedInLastThreeTimePasswordException;
import com.management.cms.model.dto.UserDto;
import com.management.cms.model.enitity.*;
import com.management.cms.model.request.ChangePassRequest;
import com.management.cms.model.request.UserSaveRequest;
import com.management.cms.model.request.UserSearchRequest;
import com.management.cms.repository.*;
import com.management.cms.service.GeneratorSeqService;
import com.management.cms.service.UserService;
import com.management.cms.utils.Utils;
import com.management.cms.utils.WebUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private MongoTemplate mongoTemplate;

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

    @Autowired
    PasswordResetRepository passwordResetRepository;

    @Autowired
    AccessTokenMgoRepository accessTokenMgoRepository;

    Utils utils = new Utils();

    @Override
    public void createNewUser(UserSaveRequest userSaveRequest) throws Exception{
        //validate input
        try{
            userSaveRequest.validateInput();
        }catch (Exception e){
            throw e;
        }
        //check area code có hợp lệ ko
        if(userSaveRequest.getAreaCodes().isEmpty()){
            throw new Exception("Không được để trống mã khu vực");
        }
        if(userSaveRequest.getAreaCodes().size() > 1){
            throw new Exception("Không thể chỉ định người dùng quản lý nhiều hơn 1 khu vực");
        }
        for(String areaCode : userSaveRequest.getAreaCodes()){
            if(!areaRepository.existsByCode(areaCode)){
                throw new Exception("Mã khu vực không hợp lệ : " + areaCode);
            }
        }

        AreaDoc areaDoc = areaRepository.findByCode(userSaveRequest.getAreaCodes().get(0));

        //tạo đối tượng
        UserDoc userDoc = new UserDoc();
        BeanUtils.copyProperties(userSaveRequest, userDoc);

        LocalDateTime dob = utils.convertStringToLocalDateTime01(userSaveRequest.getDob());
        userDoc.setDob(dob);

        userDoc.setPassword(passwordEncoder.encode(Commons.DEFAULT_PASSWORD));
        userDoc.setEnabled(Commons.STATUS_ACTIVE);
        userDoc.setResetPass(Commons.HAVE_NOT_RESET_PASS);
        userDoc.setFailCount(0);

        //current user để set CreateBy và set UpdateBy sau
        UserDoc currentUser = WebUtils.getCurrentUser().getUser();
        userDoc.setCreatedBy(currentUser.getEmail());
        userDoc.setUpdatedBy(currentUser.getEmail());
        userDoc.setCreatedAt(LocalDateTime.now());
        userDoc.setUpdatedAt(LocalDateTime.now());

        userDoc.getAreas().add(areaDoc);

        userDoc.setRole(roleRepository.findByCode("USER").get());
        //SET ID CHO PARTNER TRƯỚC KHI LƯU
        userDoc.setId(generatorSeqService.getNextSequenceId(userDoc.SEQUENCE_NAME));
        userRepository.save(userDoc);

//        //Gửi mail tạo tài khoản cho user (chưa hoạt động)
//        Map<String, Object> map = new HashMap<>();
//        map.put("fullName", userDoc.getFirstName().concat(" ").concat(userDoc.getLastName()));
//        map.put("username", userDoc.getEmail());
//        map.put("password", Commons.DEFAULT_PASSWORD);
//        sendMailToUser(userDoc.getEmail(), null, map, ESystemEmail.MAIL_CREATE_ACCOUNT);
    }

    @Override
    public void editUser(UserSaveRequest userSaveRequest, Long id) throws Exception{
        //không được thay đổi id
        if(id != userSaveRequest.getId()) throw new Exception("Không được thay đổi id");

        //check id truyền vào có trong DB ko
        Optional<UserDoc> optional = userRepository.findById(id);
        if (!optional.isPresent()){
            throw new Exception("Id không hợp lệ.");
        }

        //validate input
        try{
            userSaveRequest.validateInput();
        }catch (Exception e){
            throw e;
        }
        //check area code có hợp lệ ko
        if(userSaveRequest.getAreaCodes().isEmpty()){
            throw new Exception("Không được để trống mã khu vực");
        }
        if(userSaveRequest.getAreaCodes().size() > 1){
            throw new Exception("Không thể chỉ định người dùng quản lý nhiều hơn 1 khu vực");
        }
        for(String areaCode : userSaveRequest.getAreaCodes()){
            if(!areaRepository.existsByCode(areaCode)){
                throw new Exception("Mã khu vực không hợp lệ : " + areaCode);
            }
        }

        //tạo đối tượng
        UserDoc userDoc = optional.get();
        BeanUtils.copyProperties(userSaveRequest, userDoc);

        LocalDateTime dob = utils.convertStringToLocalDateTime01(userSaveRequest.getDob());
        userDoc.setDob(dob);

        //current user để set CreateBy và set UpdateBy sau
        UserDoc currentUser = WebUtils.getCurrentUser().getUser();
        userDoc.setCreatedBy(currentUser.getEmail());
        userDoc.setUpdatedBy(currentUser.getEmail());
        userDoc.setCreatedAt(LocalDateTime.now());
        userDoc.setUpdatedAt(LocalDateTime.now());

        //thay đổi khu vực nếu có
        if(!userDoc.getAreas().get(0).getCode().equals(userSaveRequest.getAreaCodes().get(0))){
            AreaDoc areaDoc = areaRepository.findByCode(userSaveRequest.getAreaCodes().get(0));
            userDoc.getAreas().remove(0);
            userDoc.getAreas().add(areaDoc);
        }

        userRepository.save(userDoc);
    }

    @Override
    public Page<UserDto> searchAllUser(UserSearchRequest userSearchRequest, Pageable pageable) throws Exception{
        Query query = new Query();

        if (!StringUtils.isEmpty(userSearchRequest.getEmail().toLowerCase().trim())) {
            query.addCriteria(Criteria.where("email").regex(".*"+userSearchRequest.getEmail().toLowerCase().trim()+".*", "i"));
        }

        if (!StringUtils.isEmpty(userSearchRequest.getPhoneNumber().toLowerCase().trim())) {
            query.addCriteria(Criteria.where("phoneNumber").regex(".*"+userSearchRequest.getPhoneNumber().toLowerCase().trim()+".*", "i"));
        }

        if (!StringUtils.isEmpty(userSearchRequest.getSocialSecurityNum().toLowerCase().trim())) {
            query.addCriteria(Criteria.where("socialSecurityNum").regex(".*"+userSearchRequest.getSocialSecurityNum().toLowerCase().trim()+".*", "i"));
        }

        if (!StringUtils.isEmpty(userSearchRequest.getRole().toLowerCase().trim())) {
            Optional<RoleDoc> optionalRole = roleRepository.findByCode(userSearchRequest.getRole());
            if(!optionalRole.isPresent()) throw new Exception("Mã role không hợp lệ");
            RoleDoc roleDoc = optionalRole.get();
            query.addCriteria(Criteria.where("role").is(roleDoc));
        }

        if (!StringUtils.isEmpty(userSearchRequest.getArea().toLowerCase().trim())) {
            AreaDoc areaDoc = areaRepository.findByCode(userSearchRequest.getArea());
            if(areaDoc == null) throw new Exception("Mã khu vực không hợp lệ");
            query.addCriteria(Criteria.where("areas").is(areaDoc));
        }

        if (userSearchRequest.getEnabled() != 2) {
            query.addCriteria(Criteria.where("enabled").is(userSearchRequest.getEnabled()));
        }

        List<UserDoc> queryResults = mongoTemplate.find(query, UserDoc.class);

        List<UserDto> returnResults = new ArrayList<>();

        Long total = Long.valueOf(queryResults.size());
        for(UserDoc userDoc : queryResults){
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userDoc, userDto);
            userDto.setFullName(userDoc.getFirstName().concat(" ").concat(userDoc.getLastName()));
            if(userDoc.getDob() != null) userDto.setDob(utils.convertDateToString(userDoc.getDob()));
            for(AreaDoc areaDoc : userDoc.getAreas()){
                userDto.getAreas().add(areaDoc.getCode());
            }
            userDto.setRole(userDoc.getRole().getCode());
            userDto.setStatus(userDoc.getEnabled());
            returnResults.add(userDto);
        }
        return new PageImpl<>(returnResults, pageable, total);
    }

    @Override
    public UserDto getUserDetailById(Long id) throws Exception{
        Optional<UserDoc> optional = userRepository.findById(id);
        if (!optional.isPresent()) {
            throw new Exception("Id không hợp lệ.");
        }
        UserDoc userDoc = optional.get();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDoc, userDto);
        userDto.setFullName(userDoc.getFirstName().concat(" ").concat(userDoc.getLastName()));
        if(userDoc.getDob() != null) userDto.setDob(utils.convertDateToString(userDoc.getDob()));
        for(AreaDoc areaDoc : userDoc.getAreas()){
            userDto.getAreas().add(areaDoc.getCode());
        }
        userDto.setRole(userDoc.getRole().getCode());
        userDto.setStatus(userDoc.getEnabled());

        return userDto;
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
    public String lockAndUnlockById(Long id) throws Exception {
        String message = "";
        Optional<UserDoc> optional = userRepository.findById(id);
        if (!optional.isPresent()) {
            throw new Exception("Id không hợp lệ.");
        }
        UserDoc userDoc = optional.get();
        if(userDoc.getEnabled() == Commons.STATUS_ACTIVE){
            userDoc.setEnabled(Commons.STATUS_INACTIVE);
            message = "Khóa người dùng thành công";
        }else{
            userDoc.setEnabled(Commons.STATUS_ACTIVE);
            message = "Mở khóa người dùng thành công";
        }
        userRepository.save(userDoc);
        return message;
    }

    @Override
    public void resetPassword(Long id) throws Exception{
        UserDoc userDoc = userRepository.findById(id).get();
        if (userDoc == null) throw new Exception("Id không hợp lệ.");

        UserDoc currentUser = WebUtils.getCurrentUser().getUser();

        PasswordResetDoc passwordResetDoc = new PasswordResetDoc();
        passwordResetDoc.setUserDocEmail(userDoc.getEmail());
        passwordResetDoc.setPasswordOld(userDoc.getPassword());
        passwordResetDoc.setCreatedByEmail(currentUser.getEmail());
        passwordResetDoc.setCreatedDate(LocalDateTime.now());
        passwordResetDoc.setId(generatorSeqService.getNextSequenceId(passwordResetDoc.SEQUENCE_NAME));

        passwordResetRepository.save(passwordResetDoc);

        String passwordGender = WebUtils.genderRandomByRegex(Commons.REGEX_GEN_PASSWORD);
        userDoc.setPassword(passwordEncoder.encode(passwordGender));
        userDoc.setResetPass(Commons.HAVE_NOT_RESET_PASS);
        userDoc.setUpdatedBy(currentUser.getEmail());
        userDoc.setUpdatedAt(LocalDateTime.now());
        userDoc.setEnabled(Commons.STATUS_ACTIVE);
        userDoc.setFailCount(0);
        userRepository.save(userDoc);
        logger.info("PASSWORD NEW===: {}", passwordGender);

        //Sau khi reset pass thì xóa access token của người dùng đi -> Nếu người này đã đăng nhâp,
        //sau đó admin reset pass, thì người này phải đăng nhập lại để có token mới


//        //Gửi mail reset pass tài khoản cho user (chưa hoạt động)
//        Map<String, Object> map = new HashMap<>();
//        map.put("fullName", userDoc.getFirstName().concat(" ").concat(userDoc.getLastName()));
//        map.put("username", userDoc.getEmail());
//        map.put("password", Commons.DEFAULT_PASSWORD);
//        sendMailToUser(userDoc.getEmail(), null, map, ESystemEmail.MAIL_RESET_PASS);
    }

    @Override
    public String changePassword(ChangePassRequest changePassRequest) throws Exception{
        try{
            UserDoc currentUser = WebUtils.getCurrentUser().getUser();
            if (!passwordEncoder.matches(changePassRequest.getPasswordOld().trim(), currentUser.getPassword())) {
                throw new OldPasswordIsWrongException("Mật khẩu cũ không chính xác");
            }
            if(!changePassRequest.getPasswordNew().equals(changePassRequest.getRePassword())){
                throw new RePasswordWrongException("Nhập lại mật khẩu không chính xác");
            }
            List<PasswordResetDoc> passwordResets = findTop3ByUserEmail(currentUser.getEmail());
            for (PasswordResetDoc pass : passwordResets) {
                if (passwordEncoder.matches(changePassRequest.getPasswordNew(), pass.getPasswordOld())) {
                    throw new UsedInLastThreeTimePasswordException("Bạn đã sử dụng mật khẩu này. Vui lòng sử dụng mật khẩu khác");
                }
            }
            currentUser.setPassword(passwordEncoder.encode(changePassRequest.getPasswordNew()));
//            currentUser.setEnabled(Commons.STATUS_ACTIVE);
            currentUser.setResetPass(Commons.DID_RESET_PASS);
            currentUser.setUpdatedBy(currentUser.getEmail());
            currentUser.setUpdatedAt(LocalDateTime.now());
            userRepository.save(currentUser);

            PasswordResetDoc passwordResetDoc = new PasswordResetDoc();
            passwordResetDoc.setUserDocEmail(currentUser.getEmail());
            passwordResetDoc.setPasswordOld(currentUser.getPassword());
            passwordResetDoc.setCreatedByEmail(currentUser.getEmail());
            passwordResetDoc.setCreatedDate(LocalDateTime.now());
            passwordResetDoc.setId(generatorSeqService.getNextSequenceId(passwordResetDoc.SEQUENCE_NAME));

            passwordResetRepository.save(passwordResetDoc);

            return "Thay đổi mật khẩu thành công";
        }
        catch (OldPasswordIsWrongException e){
            throw e;
        }
        catch (RePasswordWrongException e){
            throw e;
        }
        catch (UsedInLastThreeTimePasswordException e){
            throw e;
        }
        catch (Exception e){
            throw new Exception("Người dùng chưa đăng nhập thành công");
        }
    }

    @Override
    public void logout(String token) {
        AccessTokenMgo accessTokenMgo = accessTokenMgoRepository.findByToken(token);
        if (accessTokenMgo != null){
            accessTokenMgoRepository.delete(accessTokenMgo);
        }
    }

    private List<PasswordResetDoc> findTop3ByUserEmail(String email) {
        Query query = new Query();

        query.addCriteria(Criteria.where("userDocEmail").regex(".*"+email.toLowerCase().trim()+".*", "i"));
        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        query.limit(3);
        List<PasswordResetDoc> queryResults = mongoTemplate.find(query, PasswordResetDoc.class);

        return queryResults;
    }


}
