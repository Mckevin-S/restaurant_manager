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

// Mettre à jour le statut (ex: EN_PREPARATION -> PRETE)
export const updateStatut = (id, statut) =>
    apiClient.patch(`/commandes/${id}/statut`, null, { params: { statut } });


/** Récupère toutes les commandes */
export const getCommandes = () => apiClient.get('/commandes');

/** Récupère les commandes en attente */
export const getCommandesEnAttente = () => apiClient.get('/commandes/statut/EN_ATTENTE');

/** Récupère les commandes prêtes pour l'encaissement */
export const getCommandesServies = () => apiClient.get('/commandes/statut/SERVIE');

/** Retourne l'URL du ticket pour une commande donnée */
export const getTicketUrl = (id) => `${apiClient.defaults.baseURL}/commandes/${id}/ticket`;

/** Récupérer une commande par ID */
export const getCommandeById = (id) => apiClient.get(`/commandes/${id}`);

/** Effectuer un paiement simplifié pour une commande */
export const effectuerPaiement = (commandeId, montant, typePaiement) =>
    apiClient.post('/paiements/effectuer', null, { params: { commandeId, montant, typePaiement } });

// /** Télécharger le ticket PDF pour une commande (blob) */
// export const downloadTicket = (id) =>
//     apiClient.get(`/commandes/${id}/ticket`, { responseType: 'blob' });

/** Télécharger le ticket PDF pour une commande (blob) */
export const downloadTicket = (id) =>
    apiClient.get(`/commandes/${id}/ticket`, { responseType: 'blob' });

/** Récupérer les paramètres/infos du restaurant (pour l'impression) */
export const getRestaurantSettings = () => apiClient.get('/restaurants/settings');

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
