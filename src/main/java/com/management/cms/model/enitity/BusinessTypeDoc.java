package com.management.cms.model.enitity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "business_type")
@Data
public class BusinessTypeDoc {
    @Transient
    public static final String SEQUENCE_NAME = "business_type_sequence";
    @Id
    private Long id;
    private String code;
    private String name;
    private Integer status;
}
