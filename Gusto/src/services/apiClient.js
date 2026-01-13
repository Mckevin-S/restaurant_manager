import axios from 'axios';
import toast from 'react-hot-toast';

/**
 * Centralized Axios instance for the Gusto application.
 */
const apiClient = axios.create({
    baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
    timeout: 15000, // Augmenté pour les rapports PDF
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
    },
});


// Request interceptor: Inject JWT token if available
apiClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Response interceptor: Global error handling
apiClient.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        // Extraction du message d'erreur standardisé du backend (ReponseErreur)
        const backendMessage = error.response?.data?.message;
        const details = error.response?.data?.details;

        let message = backendMessage || error.message || 'Une erreur inattendue est survenue';

        if (details && Array.isArray(details) && details.length > 0) {
            message += `: ${details.join(', ')}`;
        }

        // Log error for debugging
        console.error('[API Error]', {
            status: error.response?.status,
            message: message,
            path: error.response?.data?.path,
            error: error.response || error
        });

        if (error.response?.status === 401) {
            // Ne pas rediriger si on est déjà sur la page de login ou si c'est une tentative de login échouée
            const isLoginRequest = error.config.url.includes('/Auth/login');
            if (!isLoginRequest) {
                toast.error('Session expirée. Veuillez vous reconnecter.');
                localStorage.removeItem('token');
                // Optionnel: window.location.href = '/login';
            } else {
                toast.error(message);
            }
        } else if (error.response?.status === 403) {
            toast.error('Vous n\'avez pas les droits nécessaires pour cette action.');
        } else if (error.response?.status === 404) {
            toast.error('Ressource introuvable.');
        } else if (error.response?.status >= 500) {
            toast.error('Le serveur rencontre un problème. Veuillez réessayer plus tard.');
        } else {
            toast.error(message);
        }

        return Promise.reject(error);
    }
);

export default apiClient;
