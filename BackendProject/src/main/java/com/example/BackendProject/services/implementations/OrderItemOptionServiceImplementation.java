package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.OrderItemOptionDto;
import com.example.BackendProject.entities.OrderItemOption;
import com.example.BackendProject.mappers.OrderItemOptionMapper;
import com.example.BackendProject.repository.OrderItemOptionRepository;
import com.example.BackendProject.services.interfaces.OrderItemOptionServiceInterface;
import com.example.BackendProject.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderItemOptionServiceImplementation implements OrderItemOptionServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemOptionServiceImplementation.class);
    private final OrderItemOptionRepository repository;
    private final OrderItemOptionMapper mapper;

    public OrderItemOptionServiceImplementation(OrderItemOptionRepository repository, OrderItemOptionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public OrderItemOptionDto save(OrderItemOptionDto dto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de sauvegarde d'une option de commande - Ligne de commande ID: {}", 
                    context, dto.getLigneCommande() != null ? dto.getLigneCommande() : "N/A");
        OrderItemOption entity = mapper.toEntity(dto);
        OrderItemOption saved = repository.save(entity);
        logger.info("{} Option de commande sauvegardée avec succès. ID: {}", context, saved.getId());
        return mapper.toDto(saved);
    }

    @Override
    public List<OrderItemOptionDto> getByLigneCommande(Long ligneCommandeId) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération des options pour la ligne de commande ID: {}", context, ligneCommandeId);
        List<OrderItemOptionDto> options = repository.findByLigneCommandeId(ligneCommandeId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        logger.info("{} {} options récupérées pour la ligne de commande ID: {}", context, options.size(), ligneCommandeId);
        return options;
    }

    @Override
    public void delete(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de suppression de l'option de commande ID: {}", context, id);
        if (!repository.existsById(id)) {
            logger.error("{} Option de commande non trouvée avec l'ID: {}", context, id);
            throw new RuntimeException("Option de commande non trouvée avec l'ID: " + id);
        }
        repository.deleteById(id);
        logger.info("{} Option de commande ID: {} supprimée avec succès", context, id);
    }
}
