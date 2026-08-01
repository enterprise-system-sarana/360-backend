package com.saranaresturantsystem.services.users;

import com.saranaresturantsystem.dto.request.users.UserRequest;
import com.saranaresturantsystem.dto.response.users.UserResponse;
import com.saranaresturantsystem.entities.users.User;
import org.springframework.data.domain.Page;


import java.util.Map;

public interface UserService {
    Page<UserResponse> getAll(Map<String, String> params);
    UserResponse getById(Long id);
    UserResponse create(UserRequest request);
    UserResponse update(Long id, UserRequest request);
    void delete(Long id);
    User findById(Long id);
    User getCurrentUser();
}
