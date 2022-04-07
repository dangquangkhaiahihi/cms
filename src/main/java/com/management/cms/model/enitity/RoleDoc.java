package com.management.cms.model.enitity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "sa_role")
@Data
public class RoleDoc {
    @Transient
    public static final String SEQUENCE_NAME = "sa_role_sequence";
    @Id
    private Long id;
    private String code;
    private String description;
    private Integer status;

    @DBRef
    private List<PermissionDoc> permissions = new ArrayList<>();
}
