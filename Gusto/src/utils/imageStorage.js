/**
 * Image Storage Utility - Gère le stockage des images locales dans Gusto/src/images
 */

/**
 * Obtenir l'URL d'affichage de l'image
 * @param {string} photoUrl - La référence d'image (chemin relatif depuis src/images ou data URL)
 * @returns {string} L'URL complète pour afficher l'image
 */
export const getImageDisplayUrl = (photoUrl) => {
    if (!photoUrl) return null;
    
    // Si c'est déjà une data URL ou une URL complète, la retourner
    if (photoUrl.startsWith('data:') || photoUrl.startsWith('http')) {
        return photoUrl;
    }
    
    // Sinon, c'est une référence locale - construire le chemin depuis src/images
    const imagePath = `/src/images/${photoUrl}`;
    return new URL(imagePath, window.location.origin).href;
};

/**
 * Importer une image et la sauvegarder localement
 * @param {File} file - Le fichier image à importer
 * @returns {Promise<string>} Le nom du fichier sauvegardé
 */
export const saveImageLocally = async (file) => {
    // Générer un nom unique
    const timestamp = Date.now();
    const random = Math.random().toString(36).substring(7);
    const sanitizedName = file.name.replace(/[^a-zA-Z0-9.-]/g, '_');
    const uniqueFileName = `${timestamp}_${random}_${sanitizedName}`;
    
    // Cette fonction sera appelée depuis le backend pour sauvegarder l'image
    // Le frontend envoie juste le fichier avec son nom
    return uniqueFileName;
};

/**
 * Créer un gestionnaire d'erreur d'image
 * @returns {function} Fonction onError pour les tags img
 */
export const createImageErrorHandler = () => {
    return (e) => {
        console.warn('Erreur chargement image:', e.target.src);
        // Utiliser une image placeholder
        e.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 300"%3E%3Crect fill="%23f3f4f6" width="400" height="300"/%3E%3Ctext x="200" y="150" text-anchor="middle" dy=".3em" fill="%239ca3af" font-size="16" font-family="system-ui"%3EImage non disponible%3C/text%3E%3C/svg%3E';
    };
};

export default {
    getImageDisplayUrl,
    saveImageLocally,
    createImageErrorHandler
};
