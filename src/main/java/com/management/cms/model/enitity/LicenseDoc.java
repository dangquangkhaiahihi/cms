package com.management.cms.model.enitity;

import com.management.cms.constant.ELicenseType;
import com.management.cms.constant.EProvider;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "license")
@Data
public class LicenseDoc {
    @Transient
    public static final String SEQUENCE_NAME = "license_sequence";
    @Id
    private Long id;
    private String regno;
    private LocalDateTime createdDate;
    private LocalDateTime expirationDate;
    private EProvider provider;
    private ELicenseType licenseType;

    private String image;
}
