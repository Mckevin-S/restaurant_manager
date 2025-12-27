package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.OrderItemOptionDto;
import com.example.BackendProject.entities.OrderItemOption;
import com.example.BackendProject.mappers.OrderItemOptionMapper;
import com.example.BackendProject.repository.OrderItemOptionRepository;
import com.example.BackendProject.services.interfaces.OrderItemOptionServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderItemOptionServiceImplementation implements OrderItemOptionServiceInterface {

    private final OrderItemOptionRepository repository;
    private final OrderItemOptionMapper mapper;

    public OrderItemOptionServiceImplementation(OrderItemOptionRepository repository, OrderItemOptionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public OrderItemOptionDto save(OrderItemOptionDto dto) {
        OrderItemOption entity = mapper.toEntity(dto);

        // Sécurité : Si le nom ou le prix n'est pas dans le DTO,
        // on pourrait les récupérer de l'objet OptionItem lié ici.

        OrderItemOption saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public List<OrderItemOptionDto> getByLigneCommande(Long ligneCommandeId) {
        return repository.findByLigneCommandeId(ligneCommandeId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
