package com.saranaresturantsystem.dto.response.finances;

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
public class CurrencyResponse extends BaseEntityResponse {
    private  Long id ;
    private  String code ;
    private  String name ;
    private  String operation ;
    private  double rate ;
    private  String symbol ;
    private String status ;
}
