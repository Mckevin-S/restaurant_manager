package com.example.BackendProject.services.interfaces;

import com.example.BackendProject.dto.OrderItemOptionDto;

import java.util.List;

public interface OrderItemOptionServiceInterface {

    OrderItemOptionDto save(OrderItemOptionDto dto);

    List<OrderItemOptionDto> getByLigneCommande(Long ligneCommandeId);

    void delete(Long id);

}
