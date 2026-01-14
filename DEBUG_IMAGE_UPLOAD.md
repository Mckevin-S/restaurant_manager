# Image Upload - Guide de Dépannage

## Erreur 500 - Éléments à vérifier

### 1. Backend - Chemin d'accès au dossier images

**Changement effectué:**
- La méthode `getUploadDir()` construit maintenant le chemin dynamiquement
- Elle remplace "BackendProject" par "Gusto" dans le chemin courant
- Résultat: `C:\...\Gestion restaurant\RestaurantManager\Gusto\src\images`

**Vérification:**
```bash
# Depuis BackendProject, vérifie que le chemin est correct
System.getProperty("user.dir") # Affiche le répertoire de BackendProject
# Le chemin remplacé doit pointer vers Gusto/src/images
```

### 2. Permissions d'accès

Le backend essaie de:
1. Vérifier que le dossier existe
2. Le créer s'il n'existe pas
3. Y écrire les fichiers

**À vérifier:**
- ✅ Le dossier `Gusto/src/images/` existe
- ✅ Permissions lecture/écriture sur le dossier
- ✅ Pas de fichiers en lecture seule bloquant l'écriture

### 3. Validation du fichier

**Contrôles ajoutés:**
- Vérification que le fichier n'est pas vide
- Vérification du Content-Type (doit commencer par `image/`)
- Logs détaillés du type MIME reçu

**Si erreur:**
```
Type MIME invalide: null ou non-image/*
```

### 4. Redimensionnement d'image

**Process:**
1. Reçoit le fichier multipart
2. Redimensionne en 800x600
3. Compresse à 75% de qualité
4. Sauve au format JPEG

**Si erreur:**
- Vérifier que Thumbnailator peut lire l'image
- Vérifier les permissions d'écriture
- Consulter les logs détaillés du backend

## Logs Backend à consulter

```
[Log] Chemin upload: C:\...\Gusto\src\images
[Log] Type MIME reçu: image/jpeg (ou image/png, etc.)
[Log] Début du redimensionnement d'image vers: ...
[Log] Image redimensionnée avec succès. Taille du fichier: XXXX bytes
```

## FormData - Gestion des Headers

**Important:**
- Axios **ne doit PAS** définir `Content-Type` pour FormData
- Le navigateur génère le boundary multipart automatiquement
- L'interceptor apiClient supprime le header si FormData est détecté

**Code:**
```javascript
if (config.data instanceof FormData) {
    delete config.headers['Content-Type'];
}
```

## Workflow correct

```
Frontend:
1. User sélectionne image
2. FileReader affiche preview
3. Form submit appelle handleCreatePlat()
4. Crée le plat vide (photoUrl = '')
5. Appelleutils handleImageUploadForPlat()

Request FormData:
1. FormData avec clé 'file'
2. ApiClient supprime Content-Type
3. Navigateur ajoute Content-Type: multipart/form-data; boundary=...
4. Backend reçoit le fichier correctement

Backend:
1. Valide le fichier (vérification type MIME)
2. Crée le dossier Gusto/src/images s'il n'existe pas
3. Génère UUID + .jpg
4. Redimensionne 800x600 @ 75%
5. Sauve le fichier
6. Stocke juste le nom du fichier en BD
7. Retourne {photoUrl: 'uuid.jpg'}

Frontend:
1. Reçoit {photoUrl: 'uuid.jpg'}
2. Utilise getImageDisplayUrl() pour afficher
3. URL devient: /src/images/uuid.jpg
```

## Test Route Disponible

Une page de test a été créée pour debugger:
```
URL: http://localhost:5173/test/image-upload

Features:
- Sélectionner une image
- Voir le preview immédiat
- Upload manuel avec logs détaillés
- Affichage des erreurs complètes
- Affichage du platId et photoUrl reçus
```

## Prochaines étapes

1. ✅ Backend compilation avec fixes de chemin
2. ⏳ Redémarrer backend avec `mvn spring-boot:run`
3. ⏳ Tester via la route `/test/image-upload`
4. ⏳ Vérifier les logs du backend
5. ⏳ Utiliser MenuManagement normalement

## Fichiers modifiés

**Backend:**
- `PlatServiceImplementation.java`
  - Nouvelle méthode `getUploadDir()` avec chemin dynamique
  - Validation du type MIME ajoutée
  - Logs détaillés du redimensionnement

**Frontend:**
- `apiClient.js` - Gestion FormData dans interceptor
- `MenuManagement.jsx` - Simplification du FormData
- `ImageUploadTest.jsx` - Page de test (nouveau fichier)
- `App.jsx` - Route de test ajoutée
