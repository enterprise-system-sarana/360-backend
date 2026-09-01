package com.saranaresturantsystem.dto.response.users;


import com.saranaresturantsystem.dto.response.common.BaseEntityResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public  class RoleResponse extends BaseEntityResponse {
    private  Long id ;
    private  String code ;
    private  String name ;
    private  String description ;
    private  String status ;
    private  Set<Long> permissionIds;
    private  List<PermissionResponse> permissions ;
}
