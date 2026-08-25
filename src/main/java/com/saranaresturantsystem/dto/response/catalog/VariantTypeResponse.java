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
public class VariantTypeResponse extends BaseEntityResponse {
    private Long id;
    private String code;
    private String name;
    private String status;
    private List<VariantValueItemResponse> values;
}
