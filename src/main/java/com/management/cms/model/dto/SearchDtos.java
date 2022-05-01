package com.management.cms.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class SearchDtos {
    List<?> content;
    Integer totalElements;
    Integer totalPages;
}
