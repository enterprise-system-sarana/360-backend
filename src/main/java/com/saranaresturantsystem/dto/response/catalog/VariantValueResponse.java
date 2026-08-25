package com.saranaresturantsystem.dto.response.catalog;

import com.saranaresturantsystem.dto.response.common.BaseEntityResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class VariantValueResponse extends BaseEntityResponse {
    private Long id;
    private String code;
    private String name;
    private Long variantTypeId;
    private String variantTypeName;
    private String status;
}