package com.saranaresturantsystem.specification.finances.currency;

import com.saranaresturantsystem.specification.common.StatusFilter;

public record CurrencyFilter(
        String code ,
        String name
        ,String status
) implements StatusFilter {

}
