package com.saranaresturantsystem.specification.users.role;

import com.saranaresturantsystem.specification.common.StatusFilter;

public record RoleFilter(
        String name ,
        String code ,
        String status
) implements StatusFilter {
}
