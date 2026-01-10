import apiClient from './apiClient';

/**
 * Service pour la gestion des données de l'API Gusto.
 * Utilise l'instance centralisée apiClient pour la gestion du JWT et des erreurs.
 */

// --- PLATS ---

/** Récupère la liste de tous les plats */
export const getPlats = () => apiClient.get('/plats');

// --- CATÉGORIES ---

/** Récupère toutes les catégories de plats */
export const getCategories = () => apiClient.get('/categories');

// --- TABLES ---

/** Récupère l'état de toutes les tables du restaurant */
export const getTables = () => apiClient.get('/tables');

// --- COMMANDES ---

/** 
 * Crée une nouvelle commande
 * @param {Object} commande - Les données de la commande (CommandeDto)
 */
export const createCommande = (commande) => apiClient.post('/commandes', commande);

/** 
 * Met à jour le statut d'une commande
 * @param {number} id - ID de la commande
 * @param {string} statut - Nouveau statut
 */
export const updateStatut = (id, statut) => apiClient.patch(`/commandes/${id}/statut`, { statut });

/** Récupère toutes les commandes */
export const getCommandes = () => apiClient.get('/commandes');

/** Récupère les commandes en attente */
export const getCommandesEnAttente = () => apiClient.get('/commandes/statut/EN_ATTENTE');

/** Retourne l'URL du ticket pour une commande donnée */
export const getTicketUrl = (id) => `${apiClient.defaults.baseURL}/commandes/${id}/ticket`;

// --- PROMOTIONS ---

/** Récupère les promotions actives */
export const getPromotions = () => apiClient.get('/promotions');

/** Crée une promotion */
export const createPromotion = (data) => apiClient.post('/promotions', data);

/** Met à jour une promotion existing */
export const updatePromotion = (id, data) => apiClient.put(`/promotions/${id}`, data);

/** Supprime une promotion */
export const deletePromotion = (id) => apiClient.delete(`/promotions/${id}`);

// --- INGRÉDIENTS & STOCK ---

/** Récupère tous les ingrédients */
export const getIngredients = () => apiClient.get('/ingredients');

/** Récupère les ingrédients dont le stock est critique */
export const getIngredientsEnAlerte = () => apiClient.get('/ingredients/alertes');

// --- RECETTES ---

/** Récupère toutes les recettes */
export const getRecettes = () => apiClient.get('/recettes');

// --- STATISTIQUES ---

/** Récupère les statistiques du tableau de bord */
export const getStats = () => apiClient.get('/stats');

// --- AUTH ---
// Ajout des méthodes d'authentification manquantes dans le service original
export const login = (credentials) => apiClient.post('/Auth/login', credentials);
export const getCurrentUser = () => apiClient.get('/users/me');
export const register = (data) => apiClient.post('/Auth/register', data);
export const verify2fa = (data) => apiClient.post('/Auth/verify-2fa', data);

// --- SYSTÈME ---

/** Vérifie si le serveur est en ligne */
export const checkHealth = () => apiClient.get('/categories'); // Utilise un endpoint simple et public
