package com.management.cms.security;

import com.management.cms.model.enitity.UserDoc;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserDetailsImpl implements UserDetails {
    private String username;
    private String email;
    private String roleCode;
    private Long id;
    private UserDoc user;
    private final List<GrantedAuthority> authorities;

    public UserDetailsImpl(UserDoc user, List<GrantedAuthority> authorities) {
        this.username = user.getEmail();
        this.email = user.getEmail();
        if (user.getRole() != null) {
            this.roleCode = user.getRole().getCode();
        }
        this.id = user.getId();
        this.user = user;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(UserDoc user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        return new UserDetailsImpl(user, authorities);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.user.getEnabled() == 1 ? true : false;
    }
}
