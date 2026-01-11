package com.example.BackendProject.services.interfaces;

import com.example.BackendProject.dto.RestaurantDto;

import java.util.List;

public interface RestaurantServiceInterface {


    RestaurantDto createRestaurant(RestaurantDto restaurantDto);


    RestaurantDto updateRestaurant(Long id, RestaurantDto restaurantDto);


    void deleteRestaurant(Long id);

    RestaurantDto getSettings();

    RestaurantDto updateSettings(RestaurantDto restaurantDto);

    RestaurantDto getRestaurantById(Long id);

    List<RestaurantDto> getAllRestaurants();

}
