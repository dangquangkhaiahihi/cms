package com.management.cms.model.enitity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "sequences_generate")
public class SequenceId {

    @Id
    private String id;
    private Long seq;

}

