package com.management.cms.controller;

import com.google.gson.Gson;
import com.management.cms.constant.Commons;
import com.management.cms.controller.base.BaseController;
import com.management.cms.exception.AccountLockException;
import com.management.cms.model.enitity.AccessTokenMgo;
import com.management.cms.model.enitity.UserDoc;
import com.management.cms.model.request.LoginRequest;
import com.management.cms.model.response.BaseResponse;
import com.management.cms.model.response.JwtResponse;
import com.management.cms.model.response.ResponseAuthJwt;
import com.management.cms.repository.RoleRepository;
import com.management.cms.repository.UserRepository;
import com.management.cms.security.UserDetailsImpl;
import com.management.cms.service.AccessTokenService;
import com.management.cms.service.UserService;
import com.management.cms.utils.JwtUtils;
import com.management.cms.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/auth")
public class LoginController extends BaseController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    private Gson gson = Utils.getWmfGson();
    @Autowired
    private AccessTokenService accessTokenService;


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Validated @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        log.info("Start loginRequest: {}", gson.toJson(loginRequest));
        try {
            Optional<UserDoc> optionalUser = userRepository.findByEmail(loginRequest.getUsername());
            if(!optionalUser.isPresent()) throw new UsernameNotFoundException("");

            UserDoc user = optionalUser.get();
            if(user.getFailCount() > 4 || user.getEnabled().equals(Commons.STATUS_INACTIVE)){
                throw new AccountLockException("Tài khoản của bạn đã bị khóa. Hãy liên hệ với admin để mở khóa.");
            }

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            UserDoc userDoc = userDetails.getUser();
            Set<String> roles = new HashSet<>();
            if (userDetails.getAuthorities() != null) {
                roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
            }

            ResponseAuthJwt responseAuthJwt = new ResponseAuthJwt();
            responseAuthJwt.setUserId(userDoc.getId());
            responseAuthJwt.setUserName(userDetails.getUsername());
            responseAuthJwt.setRoleKeys(roles);

            String token = jwtUtils.generateToken(responseAuthJwt);
            String jwt = "Bearer " + token;
//                String jwt = jwtUtils.generateToken(responseAuthJwt);
            LocalDateTime currentDate = LocalDateTime.now();

            AccessTokenMgo accessTokenMgo= new AccessTokenMgo();
            accessTokenMgo.setToken(token);
            accessTokenMgo.setClientIp(Utils.getRequestIP(request));
            accessTokenMgo.setUserId(userDoc.getId());
            accessTokenMgo.setExpireDate(currentDate.plusDays(1));
            accessTokenMgo.setStatus(Commons.STATUS_ACTIVE);
            accessTokenService.save(accessTokenMgo);

            JwtResponse jwtResponse = new JwtResponse(jwt, userDoc.getId(), userDoc.getEmail(), userDoc.getEmail(),userDoc.getFirstName().concat(" ").concat(userDoc.getLastName()), roles);
            log.info("Save login success");
            userService.updateLastLoginAndFailCount(loginRequest.getUsername(), true);

            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_SUCCESS_00);
            baseResponse.setData(jwtResponse);
            return ResponseEntity.ok(baseResponse);

        }
        catch(UsernameNotFoundException usernameNotFoundException){
            log.info("Wrong username");
            log.info("Error authen", usernameNotFoundException);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc("Sai username");
            return ResponseEntity.badRequest().body(baseResponse);
        }
        catch(AccountLockException accountLockException){
            log.info("Account lock");
            log.info("Error authen", accountLockException);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc("Tài khoản của bạn đã bị khóa do nhập sai quá nhiều lần. Hãy liên hệ với admin để mở khóa.");
            return ResponseEntity.badRequest().body(baseResponse);
        }
        catch (Exception e) {
            log.info("Save login Failure");
            userService.updateLastLoginAndFailCount(loginRequest.getUsername(), false);
            log.info("Error authen", e);
            BaseResponse baseResponse = BaseResponse.parse(Commons.SVC_ERROR_99);
            baseResponse.setDesc("Sai password");
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }
}
