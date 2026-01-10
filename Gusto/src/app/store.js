import { configureStore } from '@reduxjs/toolkit';
import LoginReducer from '../features/LoginSlice';
import serveurReducer from '../features/ServeurDashboardSlice'; 
import kitchenReducer from '../features/KitchenDashboardSlice';

export const store = configureStore({
  reducer: {
    auth: LoginReducer,
    serveur: serveurReducer, 
    kitchen: kitchenReducer,
  },
});