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
import com.management.cms.service.AreaService;
import com.management.cms.service.GeneratorSeqService;
import com.management.cms.service.UserService;
import com.management.cms.utils.Utils;
import com.management.cms.utils.WebUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PagedListHolder;
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
    AreaService areaService;

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

        UserDoc userDoc = new UserDoc();
        if(userSaveRequest.getRole().equals("USER")){
            //check area code c?? h???p l??? ko
            if(userSaveRequest.getAreaCode() == null){
                throw new Exception("Kh??ng ???????c ????? tr???ng m?? khu v???c");
            }
            if(!areaRepository.existsByCode(userSaveRequest.getAreaCode())){
                throw new Exception("M?? khu v???c kh??ng h???p l??? : " + userSaveRequest.getAreaCode());
            }
            AreaDoc areaDoc = areaRepository.findByCode(userSaveRequest.getAreaCode());
            userDoc.getAreas().add(areaDoc);
            userDoc.setRole(roleRepository.findByCode("USER").get());
        }

        if(userSaveRequest.getRole().equals("ADMIN")){
            List<AreaDoc> areaDocs = areaService.getAllActiveAreas();
            for(AreaDoc areaDoc : areaDocs){
                userDoc.getAreas().add(areaDoc);
            }
            userDoc.setRole(roleRepository.findByCode("ADMIN").get());
        }

        BeanUtils.copyProperties(userSaveRequest, userDoc);

        LocalDateTime dob = utils.convertStringToLocalDateTime01(userSaveRequest.getDob());
        userDoc.setDob(dob);

        userDoc.setPassword(passwordEncoder.encode(Commons.DEFAULT_PASSWORD));
        userDoc.setEnabled(Commons.STATUS_ACTIVE);
        userDoc.setResetPass(Commons.HAVE_NOT_RESET_PASS);
        userDoc.setFailCount(0);

        //current user ????? set CreateBy v?? set UpdateBy sau
        UserDoc currentUser = WebUtils.getCurrentUser().getUser();
        userDoc.setCreatedBy(currentUser.getEmail());
        userDoc.setUpdatedBy(currentUser.getEmail());
        userDoc.setCreatedAt(LocalDateTime.now());
        userDoc.setUpdatedAt(LocalDateTime.now());

        //SET ID CHO USER TR?????C KHI L??U
        userDoc.setId(generatorSeqService.getNextSequenceId(userDoc.SEQUENCE_NAME));
        userRepository.save(userDoc);

//        //G???i mail t???o t??i kho???n cho user (ch??a ho???t ?????ng)
//        Map<String, Object> map = new HashMap<>();
//        map.put("fullName", userDoc.getFirstName().concat(" ").concat(userDoc.getLastName()));
//        map.put("username", userDoc.getEmail());
//        map.put("password", Commons.DEFAULT_PASSWORD);
//        sendMailToUser(userDoc.getEmail(), null, map, ESystemEmail.MAIL_CREATE_ACCOUNT);
    }

    @Override
    public void editUser(UserSaveRequest userSaveRequest, Long id) throws Exception{
        //kh??ng ???????c thay ?????i id
        if(id != userSaveRequest.getId()) throw new Exception("Kh??ng ???????c thay ?????i id");

        //check id truy???n v??o c?? trong DB ko
        Optional<UserDoc> optional = userRepository.findById(id);
        if (!optional.isPresent()){
            throw new Exception("Id kh??ng h???p l???.");
        }

        //validate input
        try{
            userSaveRequest.validateInput();
        }catch (Exception e){
            throw e;
        }

        //check area code c?? h???p l??? ko
        if(userSaveRequest.getAreaCode().isEmpty()){
            throw new Exception("Kh??ng ???????c ????? tr???ng m?? khu v???c");
        }
        if(!areaRepository.existsByCode(userSaveRequest.getAreaCode())){
            throw new Exception("M?? khu v???c kh??ng h???p l??? : " + userSaveRequest.getAreaCode());
        }

        //t??m ?????i t?????ng
        UserDoc userDoc = optional.get();
        String userRole = userDoc.getRole().getCode();
        if(!userRole.equals(userSaveRequest.getRole())){
            throw new Exception("Kh??ng th??? thay ?????i quy???n c???a ng?????i d??ng");
        }

        if(userRole.equals("USER")){
            //thay ?????i khu v???c n???u c?? thay ?????i g??
            if(!userDoc.getAreas().get(0).getCode().equals(userSaveRequest.getAreaCode())){
                AreaDoc areaDoc = areaRepository.findByCode(userSaveRequest.getAreaCode());
                userDoc.getAreas().remove(0);
                userDoc.getAreas().add(areaDoc);
            }
        }

        BeanUtils.copyProperties(userSaveRequest, userDoc);

        LocalDateTime dob = utils.convertStringToLocalDateTime01(userSaveRequest.getDob());
        userDoc.setDob(dob);

        //current user ????? set CreateBy v?? set UpdateBy sau
        UserDoc currentUser = WebUtils.getCurrentUser().getUser();
        userDoc.setCreatedBy(currentUser.getEmail());
        userDoc.setUpdatedBy(currentUser.getEmail());
        userDoc.setCreatedAt(LocalDateTime.now());
        userDoc.setUpdatedAt(LocalDateTime.now());

        userRepository.save(userDoc);
    }

    @Override
    public PagedListHolder<UserDto> searchAllUser(UserSearchRequest userSearchRequest, Integer page, Integer size, String sortby){
        Query query = new Query();

        if (!StringUtils.isEmpty(userSearchRequest.getKeyword().toLowerCase().trim())) {
            Criteria orCriterias = new Criteria();
            List<Criteria> orExpressions  = new ArrayList<>();
            orExpressions.add(Criteria.where("firstName").regex(".*" + userSearchRequest.getKeyword().toLowerCase().trim() + ".*", "i"));
            orExpressions.add(Criteria.where("lastName").regex(".*" + userSearchRequest.getKeyword().toLowerCase().trim() + ".*", "i"));
            orExpressions.add(Criteria.where("email").regex(".*" + userSearchRequest.getKeyword().toLowerCase().trim() + ".*", "i"));
            orExpressions.add(Criteria.where("phoneNumber").regex(".*" + userSearchRequest.getKeyword().toLowerCase().trim() + ".*", "i"));
            orExpressions.add(Criteria.where("socialSecurityNum").regex(".*" + userSearchRequest.getKeyword().toLowerCase().trim() + ".*", "i"));
            query.addCriteria(orCriterias.orOperator(orExpressions.toArray(new Criteria[orExpressions.size()])));
        }

        if (!StringUtils.isEmpty(userSearchRequest.getRole().toLowerCase().trim())) {
            Optional<RoleDoc> optionalRole = roleRepository.findByCode(userSearchRequest.getRole());
//            if(!optionalRole.isPresent()) throw new Exception("M?? role kh??ng h???p l???");
            RoleDoc roleDoc = optionalRole.get();
            query.addCriteria(Criteria.where("role").is(roleDoc));
        }

        if (!StringUtils.isEmpty(userSearchRequest.getArea().toLowerCase().trim())) {
            AreaDoc areaDoc = areaRepository.findByCode(userSearchRequest.getArea());
//            if(areaDoc == null) throw new Exception("M?? khu v???c kh??ng h???p l???");
            query.addCriteria(Criteria.where("areas").is(areaDoc));
        }

        if (userSearchRequest.getEnabled() != 2) {
            query.addCriteria(Criteria.where("enabled").is(userSearchRequest.getEnabled()));
        }

        List<UserDoc> queryResults = mongoTemplate.find(query, UserDoc.class);

        List<UserDto> returnResults = new ArrayList<>();

        UserDoc currentUser = WebUtils.getCurrentUser().getUser();
        queryResults.remove(currentUser);

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

        PagedListHolder pagable = new PagedListHolder(returnResults);

        MutableSortDefinition mutableSortDefinition = new MutableSortDefinition();
        mutableSortDefinition.setAscending(false);
        mutableSortDefinition.setIgnoreCase(true);
        mutableSortDefinition.setProperty(sortby);

        pagable.setSort(mutableSortDefinition);
        pagable.resort();

        pagable.setPageSize(size);
        pagable.setPage(page);
        return pagable;
    }

    @Override
    public UserDto getUserDetailById(Long id) throws Exception{
        Optional<UserDoc> optional = userRepository.findById(id);
        if (!optional.isPresent()) {
            throw new Exception("Id kh??ng h???p l???.");
        }
        UserDoc userDoc = optional.get();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDoc, userDto);
        userDto.setFullName(userDoc.getFirstName().concat(" ").concat(userDoc.getLastName()));
        if(userDoc.getDob() != null) userDto.setDob(utils.convertDateToString(userDoc.getDob()));
        for(AreaDoc areaDoc : userDoc.getAreas()){
            userDto.getAreas().add(areaDoc.getCode());
            userDto.setAreaCode(areaDoc.getCode());
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
            throw new Exception("Id kh??ng h???p l???.");
        }
        UserDoc userDoc = optional.get();
        if(userDoc.getEnabled() == Commons.STATUS_ACTIVE){
            userDoc.setEnabled(Commons.STATUS_INACTIVE);
            message = "Kh??a ng?????i d??ng th??nh c??ng";
        }else{
            userDoc.setEnabled(Commons.STATUS_ACTIVE);
            message = "M??? kh??a ng?????i d??ng th??nh c??ng";
        }
        userRepository.save(userDoc);
        return message;
    }

    @Override
    public void resetPassword(Long id) throws Exception{
        UserDoc userDoc = userRepository.findById(id).get();
        if (userDoc == null) throw new Exception("Id kh??ng h???p l???.");

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

        //Sau khi reset pass th?? x??a access token c???a ng?????i d??ng ??i -> N???u ng?????i n??y ???? ????ng nh??p,
        //sau ???? admin reset pass, th?? ng?????i n??y ph???i ????ng nh???p l???i ????? c?? token m???i


//        //G???i mail reset pass t??i kho???n cho user (ch??a ho???t ?????ng)
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
                throw new OldPasswordIsWrongException("M???t kh???u c?? kh??ng ch??nh x??c");
            }
            if(!changePassRequest.getPasswordNew().equals(changePassRequest.getRePassword())){
                throw new RePasswordWrongException("Nh???p l???i m???t kh???u kh??ng ch??nh x??c");
            }
            List<PasswordResetDoc> passwordResets = findTop3ByUserEmail(currentUser.getEmail());
            for (PasswordResetDoc pass : passwordResets) {
                if (passwordEncoder.matches(changePassRequest.getPasswordNew(), pass.getPasswordOld())) {
                    throw new UsedInLastThreeTimePasswordException("B???n ???? s??? d???ng m???t kh???u n??y. Vui l??ng s??? d???ng m???t kh???u kh??c");
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

            return "Thay ?????i m???t kh???u th??nh c??ng";
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
            throw new Exception("Ng?????i d??ng ch??a ????ng nh???p th??nh c??ng");
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
