package com.management.cms.controller;

import com.management.cms.security.UserDetailsImpl;
import com.management.cms.utils.Utils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class FooController {
    @PreAuthorize("@permissionServiceImpl.businessPremisesView")
    @GetMapping("/view")
    public String viewTest(){
        return "VIEW";
    }

    @PreAuthorize("@permissionServiceImpl.businessPremisesCreate")
    @GetMapping("/create")
    public String createTest(){
        return "CREATE";
    }

    @PreAuthorize("@permissionServiceImpl.businessPremisesEdit")
    @GetMapping("/edit")
    public String editTest(){
        return "EDIT";
    }

    @GetMapping("/test")
    public UserDetailsImpl test(){
        return Utils.getUserDetail();
    }

}
