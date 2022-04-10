package com.management.cms.model.enitity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "reset_password")
public class PasswordResetDoc {
    @Transient
    public static final String SEQUENCE_NAME = "reset_password_sequence";
    @Id
    private Long id;
    private String userDocEmail;
    private String passwordOld;
    private String createdByEmail;
    private LocalDateTime createdDate;
}

