package com.example.BackendProject.services.interfaces;

import com.example.BackendProject.dto.UserDto;
import com.example.BackendProject.utils.RoleType;

import java.util.List;

public interface UserServiceInterface {


   UserDto save(UserDto userDto);

    List<UserDto> getAll();

    UserDto getById(String  userDto);

    UserDto update(Long id, UserDto userDto);

    void delete(Long id);

    List<UserDto> findByRoleType(RoleType role);

    List<UserDto> search(String nom);

    void changePassword(Long id, String oldPassword, String newPassword);

    public void resetPassword(Long id, String newPassword);
    
    
}
