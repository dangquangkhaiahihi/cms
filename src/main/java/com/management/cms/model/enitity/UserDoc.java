package com.management.cms.model.enitity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "sa_user")
public class UserDoc {
    @Transient
    public static final String SEQUENCE_NAME = "sa_user_sequence";

    @Id
    private Long id;
    private String email;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Integer enabled;

    @DBRef
    private RoleDoc role;
}

