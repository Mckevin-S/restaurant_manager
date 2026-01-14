# Corrections - Image Upload System

## Problème Initial
❌ Erreur 500 lors du POST `/plats/{id}/upload-image`

## Causes Identifiées
1. **Chemin relatif invalide**: `../Gusto/src/images` ne fonctionne pas depuis le contexte d'exécution de Spring Boot
2. **FormData Content-Type**: Axios ajoutait un header incompatible avec le multipart
3. **Validation fichier manquante**: Pas de vérification du type MIME reçu
4. **Logs insuffisants**: Difficile à debugger sans informations détaillées

## Solutions Implémentées

### 1. ✅ Backend - PlatServiceImplementation.java

**Nouvelle méthode `getUploadDir()`:**
```java
private String getUploadDir() {
    String currentDir = System.getProperty("user.dir");
    String uploadDir = currentDir.replace("BackendProject", "Gusto") 
        + File.separator + "src" + File.separator + "images";
    return uploadDir;
}
```

**Avantages:**
- Chemin construit dynamiquement au runtime
- Fonctionne quel que soit le répertoire parent
- Remplace "BackendProject" par "Gusto" de manière fiable

**Résultat:**
- Si `user.dir = C:\...\RestaurantManager\BackendProject`
- Alors `uploadDir = C:\...\RestaurantManager\Gusto\src\images`

### 2. ✅ Frontend - apiClient.js

**Gestion FormData dans interceptor:**
```javascript
if (config.data instanceof FormData) {
    delete config.headers['Content-Type'];
}
```

**Avantages:**
- Supprime le header `Content-Type: application/json` pour FormData
- Laisse le navigateur générer les boundaries multipart correctement
- Appliqué automatiquement pour tous les FormData

### 3. ✅ Backend - Validation fichier

**Ajoutée:**
```java
String contentType = file.getContentType();
if (contentType == null || !contentType.startsWith("image/")) {
    throw new RuntimeException("Le fichier doit être une image");
}
```

**Avantages:**
- Valide le type MIME avant redimensionnement
- Erreur claire si fichier non-image reçu
- Économise ressources en rejetant tôt

### 4. ✅ Backend - Logs détaillés

**Ajoutés:**
- Chemin upload exact (pour vérifier le bon répertoire)
- Type MIME reçu (pour déboguer les problèmes de type)
- Début/fin du redimensionnement
- Permissions du dossier (existe, writable, readable)
- Taille du fichier sauvegardé

### 5. ✅ Frontend - MenuManagement.jsx

**Simplifié:**
- Suppression du paramètre 'filename' inutile dans FormData
- Garder seulement le fichier nécessaire

### 6. ✅ Frontend - Page de test

**Créé: `ImageUploadTest.jsx`**
- Route: `/test/image-upload`
- Permet de tester l'upload indépendamment
- Affiche les erreurs complètes
- Logs détaillés dans la console

## Fichiers Modifiés

| Fichier | Changements |
|---------|-------------|
| `PlatServiceImplementation.java` | Méthode getUploadDir(), validation MIME, logs détaillés |
| `apiClient.js` | Gestion FormData dans interceptor request |
| `MenuManagement.jsx` | Simplification du FormData |
| `ImageUploadTest.jsx` | ✨ Nouveau fichier - page de test |
| `App.jsx` | Route `/test/image-upload` ajoutée |
| `DEBUG_IMAGE_UPLOAD.md` | ✨ Nouveau - guide de dépannage |

## Comment Tester

### Option 1: Test automatisé
```
1. URL: http://localhost:5173/test/image-upload
2. Sélectionner une image
3. Voir le preview
4. Cliquer "Uploader"
5. Vérifier les logs (DevTools → Console)
6. Vérifier le résultat (succès ou erreur détaillée)
```

### Option 2: Test via MenuManagement
```
1. URL: http://localhost:5173/manager/menu
2. Cliquer "Ajouter Plat"
3. Remplir le formulaire
4. Sélectionner une image (preview s'affiche immédiatement)
5. Cliquer "Confirmer"
6. Vérifier la liste (image doit apparaître)
```

## Logs Backend à Vérifier

```
Chemin upload: C:\Users\Mc-kevin\faux\Gestion restaurant\RestaurantManager\Gusto\src\images
Type MIME reçu: image/jpeg
Début du redimensionnement d'image vers: ...
Image redimensionnée avec succès. Taille du fichier: 12345 bytes
```

## Prochaines Étapes

1. Redémarrer le backend: `mvn spring-boot:run`
2. Redémarrer le frontend: `npm run dev`
3. Tester via `/test/image-upload`
4. Consulter les logs du backend
5. Si ça marche: utiliser normalement via MenuManagement
6. Si erreur: consulter `DEBUG_IMAGE_UPLOAD.md`

## Status

✅ **Backend fixes appliquées**
✅ **Frontend FormData handling corrigé**
✅ **Validation fichier ajoutée**
✅ **Logs détaillés ajoutés**
✅ **Page de test créée**
✅ **Documentation complétée**

⏳ **Prêt à compiler et tester**
