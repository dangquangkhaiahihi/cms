package com.management.cms.model.enitity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "sa_user")
public class UserDoc {
    @Transient
    public static final String SEQUENCE_NAME = "sa_user_sequence";

    @Id
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDateTime dob;
    private String socialSecurityNum;

    private String password;
    private Integer enabled;
    private Integer resetPass;
    private Integer failCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    @DBRef(lazy = true)
    private List<AreaDoc> areas = new ArrayList<>();

    @DBRef(lazy = true)
    private RoleDoc role;
}

