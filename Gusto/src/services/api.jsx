import axios from 'axios';

const API_URL = 'http://localhost:3006/api';

// Récupération des plats (PlatDto)
export const getPlats = () => axios.get(`${API_URL}/plats`);

// Récupération des catégories (CategoryDto)
export const getCategories = () => axios.get(`${API_URL}/categories`);

// Récupération des tables (TableRestaurantDto)
export const getTables = () => axios.get(`${API_URL}/tables`);

// CRÉATION : Utilisation obligatoire de .post pour envoyer le CommandeDto
export const createCommande = (commande) => axios.post(`${API_URL}/commandes`, commande);

//Update le statut 
export const updateStatut = (id, statut) => axios.patch(`${API_URL}/commandes/${id}/statut`, { statut });