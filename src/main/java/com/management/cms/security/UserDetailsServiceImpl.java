package com.management.cms.security;

import com.management.cms.model.enitity.UserDoc;
import com.management.cms.repository.UserRepository;
import com.management.cms.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserDoc> optionalUser = userRepository.findByEmail(email);
        if(!optionalUser.isPresent()) throw new UsernameNotFoundException("Tên đăng nhập hoặc mật khẩu sai");
        UserDoc user = optionalUser.get();
        log.info("get user by: {}", email);
//        log.info("user: {}", Utils.getWmfGson().toJson(user));
        log.info("user: {}", user.toString());
        return UserDetailsImpl.build(user);
    }
}
