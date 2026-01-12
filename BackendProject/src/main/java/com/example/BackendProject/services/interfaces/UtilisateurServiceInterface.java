package com.example.BackendProject.services.interfaces;

import com.example.BackendProject.dto.UtilisateurDto;
import com.example.BackendProject.utils.RoleType;

import java.util.List;

public interface UtilisateurServiceInterface {
    UtilisateurDto save(UtilisateurDto utilisateurDto);

    List<UtilisateurDto> getAll();

    UtilisateurDto getById(Long  id);

    UtilisateurDto update(Long id, UtilisateurDto utilisateurDto);

    void delete(Long id);

    List<UtilisateurDto> findByRoleType(RoleType role);

    List<UtilisateurDto> search(String keyword);

    void changePassword(Long id, String oldPassword, String newPassword);

    void resetPassword(Long id, String newPassword);
    
    UtilisateurDto findByEmail(String email);

    UtilisateurDto getCurrentUtilisateur();

    UtilisateurDto updateProfile(UtilisateurDto utilisateurDto);
}
