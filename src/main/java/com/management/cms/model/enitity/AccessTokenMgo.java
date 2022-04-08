package com.management.cms.model.enitity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "access_token")
public class AccessTokenMgo {
    @Transient
    public static final String SEQUENCE_NAME = "access_token_sequence";
    @Id
    private Long id;
    private Long userId;
    private String token;
    private LocalDateTime expireDate;
    private LocalDateTime createdAt;
    private Integer status;
    private String clientIp;

}
