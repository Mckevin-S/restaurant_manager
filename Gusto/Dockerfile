# Stage 1: Build
# Passage à Node 20 car react-router@7 requiert node >= 20.0.0
FROM node:20-alpine AS build

WORKDIR /app

# Copier les fichiers de dépendances
COPY package*.json ./

# CORRECTIF : Suppression de --only=production
# Nous avons besoin des devDependencies (Vite, TypeScript, etc.) pour compiler le projet.
RUN npm ci

# Copier le code source
COPY . .

# Build de l'application (Vite sera maintenant trouvé dans node_modules)
RUN npm run build

# Stage 2: Servir avec nginx
FROM nginx:alpine

# Nettoyer le répertoire par défaut de nginx pour éviter les conflits
RUN rm -rf /usr/share/nginx/html/*

# Copier les fichiers buildés du stage précédent
COPY --from=build /app/dist /usr/share/nginx/html

# Copier votre configuration nginx (indispensable pour le routage React/SPA)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]