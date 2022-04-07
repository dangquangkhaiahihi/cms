package com.management.cms.service;

public interface PermissionService {
    boolean isBusinessPremisesView();
    boolean isBusinessPremisesEdit();
    boolean isBusinessPremisesCreate();
}
