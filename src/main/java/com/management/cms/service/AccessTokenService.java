package com.management.cms.service;


import com.management.cms.model.enitity.AccessTokenMgo;

public interface AccessTokenService {
  void save(AccessTokenMgo accessTokenMgo) throws Exception;
}
