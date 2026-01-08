# Stage 1: Build the React application
FROM node:18-alpine AS build
WORKDIR /app
COPY package.json ./
# Installer les dépendances
RUN npm install
COPY . ./
# Construire l'application React
RUN npm run build

# Stage 2: Serve the React application with Nginx
FROM nginx:alpine
# Copier le build statique depuis le stage de build
COPY --from=build /app/dist /usr/share/nginx/html
# Copier une configuration Nginx personnalisée si vous en avez une
# COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]