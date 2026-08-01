package com.saranaresturantsystem.dto.response.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Long groupId;
    private String groupCode;
    private String groupName;
    private Boolean checked = false;
}
