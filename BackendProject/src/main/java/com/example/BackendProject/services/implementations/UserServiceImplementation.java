package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.UserDto;
import com.example.BackendProject.mappers.UserMapper;
import com.example.BackendProject.repository.UserRepository;
import com.example.BackendProject.services.interfaces.UserServiceInterface;
import com.example.BackendProject.utils.RoleType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.PrivateKey;
import java.util.List;

public class UserServiceImplementation implements UserServiceInterface {

    private UserMapper userMapper;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;





    @Override
    public UserDto save(UserDto userDto) {
        return null;
    }

    @Override
    public List<UserDto> getAll() {
        return null;
    }

    @Override
    public UserDto getById(String userDto) {
        return null;
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<UserDto> findByRoleType(RoleType role) {
        return null;
    }

    @Override
    public List<UserDto> search(String nom) {
        return null;
    }

    @Override
    public void changePassword(Long id, String oldPassword, String newPassword) {

    }

    @Override
    public void resetPassword(Long id, String newPassword) {

    }
}
