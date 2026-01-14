import apiClient from '../services/apiClient';

// Image utilities for handling image display and errors
export function getImageUrl(url) {
  if (!url) return null;
  // If it's already a full URL, return as is
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }
  // If it's a relative API URL, construct the full URL
  if (url.startsWith('/api/images/')) {
    // Extract base URL and construct full image URL
    const apiBaseUrl = apiClient.defaults.baseURL; // e.g., 'http://localhost:8080/api'
    const baseUrl = apiBaseUrl.replace('/api', ''); // Remove '/api' to get 'http://localhost:8080'
    return baseUrl + url; // Combine to get 'http://localhost:8080/api/images/...'
  }
  // Otherwise return as is
  return url;
}

export function createImageErrorHandler(onError) {
  return (e) => {
    console.warn('Image failed to load:', e.target.src);
    if (onError) onError(e);
    // Set a fallback SVG
    e.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 400"%3E%3Crect fill="%23f1f5f9" width="400" height="400"/%3E%3Ctext x="50%25" y="50%25" font-size="16" fill="%2394a3b8" text-anchor="middle" dominant-baseline="middle"%3EImage non disponible%3C/text%3E%3C/svg%3E';
  };
}

export function validateImageFile(file, maxSizeMB = 5) {
  const errors = [];

  if (!file) {
    errors.push('Aucun fichier sélectionné');
    return errors;
  }

  if (!file.type.startsWith('image/')) {
    errors.push('Le fichier doit être une image');
  }

  const sizeMB = file.size / (1024 * 1024);
  if (sizeMB > maxSizeMB) {
    errors.push(`La taille ne doit pas dépasser ${maxSizeMB}MB (actuellement ${sizeMB.toFixed(2)}MB)`);
  }

  return errors;
}
