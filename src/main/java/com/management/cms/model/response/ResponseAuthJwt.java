package com.management.cms.model.response;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class ResponseAuthJwt {
  private Long userId;
  private String userName;
  private String userFullName;
  private Set<String> roleKeys;
  private List<String> scopes;
  private Integer roleId;
}
