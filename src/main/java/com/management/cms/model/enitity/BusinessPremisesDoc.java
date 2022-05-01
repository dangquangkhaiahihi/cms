package com.management.cms.model.enitity;

import com.management.cms.constant.Commons;
import com.management.cms.constant.ELicenseType;
import com.management.cms.constant.EPosition;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "business_premises")
@Data
public class BusinessPremisesDoc {
    @Transient
    public static final String SEQUENCE_NAME = "business_premises_sequence";
    @Id
    private Long id;
    private String name;
    private String addressDetail;
    private String addressGeneral;
    private String image;
    @DBRef
    private BusinessTypeDoc businessType;
    @DBRef
    private AreaDoc areaDoc;

    @DBRef(lazy = true)
    private List<PersonDoc> people = new ArrayList<>();// not in CRUD table
    private String ownerInfo; // = ownerName + ownerPhone
    private String managerInfo;// = managerName + managerPhone

    @DBRef(lazy = true)
    private List<LicenseDoc> licenses = new ArrayList<>(); // not in CRUD table
    private String foodSafetyCertificateProvidedBy;
    private LocalDateTime foodSafetyCertificateStartDate;
    private LocalDateTime foodSafetyCertificateEndDate;
    @Transient
    private Integer licenseStatus = Commons.STATUS_HAVE_NOT_ADD_BUSINESS_LICENSE;
    @Transient
    private Integer certificateStatus = Commons.STATUS_HAVE_NOT_ADD_FOOD_SAFETY_CERTIFICATE;
    private String businessLicenseRegno;

    private LocalDateTime lastInspectDate;
    private Integer warningStatus;
    private String warningContent;

    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private String createBy;
    private String updateBy;

    public void updatePeopleInfor(List<PersonDoc> peopleDoc) {//gọi lúc lưu chủ/ quản lý
        if (peopleDoc.isEmpty()) return;
        for (PersonDoc personDoc : peopleDoc) {
            if (personDoc.getPosition().getCode().equals(EPosition.OWNER.getCode())) {
                this.ownerInfo = personDoc.getFirstName().concat(" ").concat(personDoc.getLastName()).
                        concat("\n").concat(personDoc.getPhoneNumber());
            }
            if (personDoc.getPosition().getCode().equals(EPosition.MANAGER.getCode())) {
                this.managerInfo = personDoc.getFirstName().concat(" ").concat(personDoc.getLastName()).
                        concat("\n").concat(personDoc.getPhoneNumber());
            }
        }
    }

    public void updateLicenseInfo(List<LicenseDoc> licenses) {//gọi lúc lưu license
        if (licenses.isEmpty()) return;
        for (LicenseDoc license : licenses) {
            if (license.getLicenseType().getCode().equals(ELicenseType.BUSINESS_LICENSE.getCode())) {
                this.businessLicenseRegno = license.getRegno();
            }
            if (license.getLicenseType().getCode().equals(ELicenseType.FOOD_SAFETY_CERTIFICATE.getCode())) {
                this.foodSafetyCertificateProvidedBy = license.getProvider().getCode();
                this.foodSafetyCertificateStartDate = license.getCreatedDate();
                this.foodSafetyCertificateEndDate = license.getExpirationDate();
            }
        }
    }

    public void updateLicenseAndCertificateStatus() {//gọi ở hàm get
        if (this.licenses.isEmpty()) {
            return;
        }

        if (this.foodSafetyCertificateEndDate == null) return;

        this.licenses.stream().forEach(item -> {
            if (item.getLicenseType().equals(ELicenseType.BUSINESS_LICENSE)) {
                if (item.getExpirationDate().isAfter(LocalDateTime.now()))
                    this.licenseStatus = Commons.STATUS_ACTIVE;
                else this.licenseStatus = Commons.STATUS_EXPIRED;
            }
        });

        if (this.foodSafetyCertificateEndDate.isAfter(LocalDateTime.now())) {
            this.certificateStatus = Commons.STATUS_ACTIVE;
        } else {
            this.certificateStatus = Commons.STATUS_EXPIRED;
        }
    }
}
