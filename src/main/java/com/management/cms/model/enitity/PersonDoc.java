package com.management.cms.model.enitity;

import com.management.cms.constant.EPosition;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "person")
@Data
public class PersonDoc {
    @Transient
    public static final String SEQUENCE_NAME = "person_sequence";
    @Id
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDateTime dob;
    private String socialSecurityNum;
    private EPosition position;

    private String image;
}
