package com.management.cms.service.implement;

import com.management.cms.constant.EPermission;
import com.management.cms.model.enitity.PermissionDoc;
import com.management.cms.model.enitity.RoleDoc;
import com.management.cms.model.enitity.UserDoc;
import com.management.cms.security.UserDetailsImpl;
import com.management.cms.service.PermissionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Override
    public boolean isBusinessPremisesView() {
        return isValidUser(EPermission.BUSINESS_PREMISES_VIEW);
    }

    @Override
    public boolean isBusinessPremisesEdit() {
        return isValidUser(EPermission.BUSINESS_PREMISES_EDIT);
    }

    @Override
    public boolean isBusinessPremisesCreate() {
        return isValidUser(EPermission.BUSINESS_PREMISES_CREATE);
    }

    private boolean isValidUser(EPermission permission) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails != null) {
            if (userDetails.getUsername().equalsIgnoreCase("admin")) return true;
//            return !CollectionUtils.isEmpty(userPrincipal.getAuthorities())
//                    && userPrincipal.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals(function.getCode()));
            UserDoc user = userDetails.getUser();
            RoleDoc role = user.getRole();
            List<PermissionDoc> permissions = role.getPermissions();
            for (PermissionDoc perm : permissions) {
                if (permission.getCode().equals(perm.getCode())) {
                    return true;
                }
            }
        }
        return false;
    }
}
