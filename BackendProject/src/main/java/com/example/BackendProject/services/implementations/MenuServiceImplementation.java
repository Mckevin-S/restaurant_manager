package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.MenuDto;
import com.example.BackendProject.entities.Menu;
import com.example.BackendProject.mappers.MenuMapper;
import com.example.BackendProject.repository.MenuRepository;
import com.example.BackendProject.services.interfaces.MenuServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuServiceImplementation implements MenuServiceInterface {

    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;

    public MenuServiceImplementation(MenuRepository menuRepository, MenuMapper menuMapper) {
        this.menuRepository = menuRepository;
        this.menuMapper = menuMapper;
    }

    @Override
    public MenuDto save(MenuDto menuDto) {
        if (menuDto.getNom() == null || menuDto.getNom().trim().isEmpty()) {
            throw new RuntimeException("Le nom du menu est obligatoire");
        }

        // Vérifier si un menu avec le même nom existe déjà
        if (menuRepository.findByNomIgnoreCase(menuDto.getNom()).isPresent()) {
            throw new RuntimeException("Un menu avec ce nom existe déjà");
        }

        Menu menu = menuMapper.toEntity(menuDto);
        Menu savedMenu = menuRepository.save(menu);
        return menuMapper.toDto(savedMenu);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuDto> getAll() {
        return menuRepository.findAll()
                .stream()
                .map(menuMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
//    @Transactional(readOnly = true)
    public MenuDto getById(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu non trouvé avec l'ID : " + id));
        return menuMapper.toDto(menu);
    }

    @Override
    public MenuDto update(Long id, MenuDto menuDto) {
        Menu menu = menuRepository.findById(id).get();
        if(menu == null) {
            throw new RuntimeException("Menu non trouvé avec l'ID : " + id);
        }else {
            menu.setNom(menuDto.getNom());
             menuRepository.save(menu);
            return menuMapper.toDto(menu);
        }


    }

    @Override
    public void delete(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu non trouvé avec l'ID : " + id));

        // Vérification d'intégrité : Ne pas supprimer si des catégories y sont liées
        if (menu.getCategories() != null && !menu.getCategories().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer un menu contenant des catégories actives");
        }

        menuRepository.delete(menu);
    }
}
