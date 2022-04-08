package com.management.cms.model.enitity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "area")
@Data
public class AreaDoc {
    @Transient
    public static final String SEQUENCE_NAME = "area_sequence";
    @Id
    private Long id;
    private String code;
    private String name;
    private Integer status;

}
