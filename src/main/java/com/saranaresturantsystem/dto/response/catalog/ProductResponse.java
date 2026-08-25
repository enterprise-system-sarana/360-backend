package com.saranaresturantsystem.dto.response.catalog;

import com.saranaresturantsystem.dto.response.common.BaseEntityResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ProductResponse extends BaseEntityResponse {
    private Long id;
    private String code;
    private String noted;
    private String imageUrl;
    private String status;
    private Integer reorderLevel;
    private Long modelId;
    private String modelName;
    private String brandName;
    private String categoryName;
    private List<VariantValueResponse> variantValues;
}
