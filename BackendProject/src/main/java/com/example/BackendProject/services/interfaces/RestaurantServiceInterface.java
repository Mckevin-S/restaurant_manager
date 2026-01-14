package com.example.BackendProject.services.interfaces;

import com.example.BackendProject.dto.RestaurantDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface RestaurantServiceInterface {


    RestaurantDto createRestaurant(RestaurantDto restaurantDto);


    RestaurantDto updateRestaurant(Long id, RestaurantDto restaurantDto);


    void deleteRestaurant(Long id);

    RestaurantDto getSettings();

    RestaurantDto updateSettings(RestaurantDto restaurantDto);

    RestaurantDto getRestaurantById(Long id);

    List<RestaurantDto> getAllRestaurants();

    RestaurantDto uploadLogo(MultipartFile file) throws IOException;

}
