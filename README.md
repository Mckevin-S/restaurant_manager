# 🍽️ Application de Gestion de Restaurant

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19.2.0-blue.svg)](https://reactjs.org/)

Application complète de gestion de restaurant avec backend Spring Boot et frontend React, incluant authentification 2FA, gestion des commandes, suivi des stocks et tableau de bord analytique.

---

## 📋 Table des Matières

- [Fonctionnalités](#-fonctionnalités)
- [Architecture](#-architecture)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Utilisation](#-utilisation)
- [Tests](#-tests)
- [Déploiement](#-déploiement)
- [API Documentation](#-api-documentation)
- [Contribution](#-contribution)

---

## ✨ Fonctionnalités

### 🔐 Authentification & Sécurité
- Authentification JWT avec tokens sécurisés
- Authentification à deux facteurs (2FA) par SMS
- Gestion des rôles (Manager, Serveur, Cuisinier)
- Hashage des mots de passe avec BCrypt

### 📊 Gestion Complète
- **Plats** : CRUD complet, catégorisation, upload d'images
- **Commandes** : Création, suivi en temps réel, historique
- **Tables** : Gestion des zones et disponibilité
- **Utilisateurs** : Gestion du personnel et permissions
- **Stock** : Suivi des ingrédients et mouvements
- **Paiements** : Multiple modes de paiement
- **Promotions** : Gestion des offres spéciales

### 📈 Tableau de Bord
- Statistiques en temps réel
- Plats les plus vendus
- Revenus journaliers/mensuels
- Graphiques interactifs

### 🔔 Temps Réel
- WebSocket pour les notifications
- Mise à jour automatique des commandes
- Synchronisation cuisine/serveur

---

## 🏗️ Architecture

```
RestaurantManager/
├── BackendProject/          # API Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/example/BackendProject/
│   │   │   │       ├── controllers/      # Endpoints REST
│   │   │   │       ├── services/         # Logique métier
│   │   │   │       ├── repository/       # Accès données
│   │   │   │       ├── entities/         # Modèles JPA
│   │   │   │       ├── dto/              # Data Transfer Objects
│   │   │   │       ├── mappers/          # MapStruct
│   │   │   │       ├── security/         # JWT, Config
│   │   │   │       ├── exceptions/       # Gestion erreurs
│   │   │   │       └── utils/            # Utilitaires
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── application-dev.properties
│   │   │       ├── application-test.properties
│   │   │       └── application-prod.properties
│   │   └── test/                # Tests unitaires & intégration
│   └── pom.xml
│
├── Gusto/                   # Frontend React
│   ├── src/
│   │   ├── pages/           # Pages de l'application
│   │   ├── features/        # Redux slices
│   │   ├── services/        # API calls
│   │   └── widget/          # Composants réutilisables
│   └── package.json
│
├── docker-compose.yml       # Orchestration Docker
├── .env.example            # Variables d'environnement
└── README.md
```

---

## 🔧 Prérequis

### Développement Local

- **Java 21** ou supérieur
- **Maven 3.8+**
- **Node.js 18+** et npm
- **MySQL 8.0** (ou MariaDB 10.6+)
- **Git**

### Avec Docker (Recommandé)

- **Docker 20.10+**
- **Docker Compose 2.0+**

---

## 🚀 Installation

### Option 1 : Installation Locale

#### 1. Cloner le projet

```bash
git clone https://github.com/Mckevin-S/restaurant_manager.git
cd restaurant_manager
```

#### 2. Configuration de la Base de Données

```bash
# Démarrer MySQL (WampServer ou XAMPP)
# Créer la base de données
mysql -u root -p
CREATE DATABASE restaurant CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;
```

#### 3. Configuration Backend

```bash
cd BackendProject

# Copier et configurer les variables d'environnement
cp src/main/resources/application-dev.properties.example src/main/resources/application-dev.properties

# Éditer application-dev.properties avec vos paramètres
# spring.datasource.password=votre_mot_de_passe
# jwt.secret=votre_secret_jwt_securise

# Installer les dépendances et compiler
mvn clean install

# Lancer l'application
mvn spring-boot:run
```

Le backend sera accessible sur `http://localhost:3006`

#### 4. Configuration Frontend

```bash
cd ../Gusto

# Installer les dépendances
npm install

# Créer le fichier .env
echo "VITE_API_URL=http://localhost:3006/api" > .env

# Lancer le serveur de développement
npm run dev
```

Le frontend sera accessible sur `http://localhost:5173`

### Option 2 : Installation avec Docker 🐳 (Recommandé)

```bash
# 1. Cloner le projet
git clone https://github.com/Mckevin-S/restaurant_manager.git
cd restaurant_manager

# 2. Configurer les variables d'environnement
cp .env.example .env
# Éditer .env avec vos valeurs

# 3. Lancer tous les services
docker-compose up -d

# 4. Vérifier les logs
docker-compose logs -f

# 5. Arrêter les services
docker-compose down
```

Services disponibles :
- **Frontend** : http://localhost:5173
- **Backend** : http://localhost:3006
- **MySQL** : localhost:3307
- **API Docs** : http://localhost:3006/swagger-ui.html

---

## ⚙️ Configuration

### Variables d'Environnement

Créer un fichier `.env` à la racine du projet :

```env
# Base de données
DB_ROOT_PASSWORD=votre_mot_de_passe_root
DB_USER=restaurant_user
DB_PASSWORD=mot_de_passe_securise

# JWT Secret (IMPORTANT: Changer en production!)
JWT_SECRET=votre_secret_jwt_tres_long_et_securise_minimum_256_bits

# Twilio (pour SMS 2FA)
TWILIO_ACCOUNT_SID=votre_account_sid
TWILIO_AUTH_TOKEN=votre_auth_token
TWILIO_PHONE_NUMBER=votre_numero_twilio
```

### Profils Spring

- **dev** : Développement local (par défaut)
- **test** : Tests automatisés (H2 en mémoire)
- **prod** : Production (variables d'environnement obligatoires)

```bash
# Changer de profil
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## 💻 Utilisation

### Comptes par Défaut

Après le premier démarrage, utilisez ces comptes de test :

| Rôle | Email | Mot de passe |
|------|-------|--------------|
| Manager | manager@restaurant.com | manager123 |
| Serveur | serveur@restaurant.com | serveur123 |
| Cuisinier | cuisinier@restaurant.com | cuisinier123 |

⚠️ **Changez ces mots de passe en production !**

### Workflow Git

```bash
# Créer une branche pour une nouvelle fonctionnalité
git checkout -b feature/nom-de-la-fonctionnalite

# Faire vos modifications et commits
git add .
git commit -m "feat: description de la fonctionnalité"

# Pousser vers le dépôt distant
git push -u origin feature/nom-de-la-fonctionnalite

# Créer une Pull Request sur GitHub
```

---

## 🧪 Tests

### Backend

```bash
cd BackendProject

# Lancer tous les tests
mvn test

# Lancer les tests avec couverture
mvn test jacoco:report

# Lancer uniquement les tests unitaires
mvn test -Dtest=*Test

# Lancer uniquement les tests d'intégration
mvn test -Dtest=*IntegrationTest

# Voir le rapport de couverture
open target/site/jacoco/index.html
```

### Frontend (react)

```bash
cd Gusto

# Lancer les tests (à configurer)
npm test

# Lancer les tests en mode watch
npm test -- --watch

# Générer le rapport de couverture
npm test -- --coverage
```

---

## 🚢 Déploiement

### Build de Production

#### Backend

```bash
cd BackendProject

# Compiler pour la production
mvn clean package -DskipTests

# Le JAR sera dans target/BackendProject-0.0.1-SNAPSHOT.jar

# Lancer en production
java -jar -Dspring.profiles.active=prod target/BackendProject-0.0.1-SNAPSHOT.jar
```

#### Frontend

```bash
cd Gusto

# Build de production
npm run build

# Les fichiers seront dans dist/
# Déployer sur Netlify, Vercel, ou serveur web
```

### Docker Production

```bash
# Build et déploiement avec Docker
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# Mise à jour de l'application
docker-compose pull
docker-compose up -d --build
```

---

## 📚 API Documentation

### Swagger UI

Une fois l'application lancée, accédez à la documentation interactive :

**http://localhost:3006/swagger-ui.html**

### Endpoints Principaux

| Méthode | Endpoint | Description | Auth |
|---------|----------|-------------|------|
| POST | `/api/Auth/login` | Connexion utilisateur | ❌ |
| POST | `/api/Auth/verify-2fa` | Vérification 2FA | ❌ |
| GET | `/api/plats` | Liste des plats | ✅ |
| POST | `/api/plats` | Créer un plat | ✅ Manager |
| GET | `/api/commandes` | Liste des commandes | ✅ |
| PATCH | `/api/commandes/{id}/statut` | Changer statut | ✅ |
| GET | `/api/users` | Liste utilisateurs | ✅ Manager |

---

## 🤝 Contribution

Les contributions sont les bienvenues ! Suivez ces étapes :

1. **Fork** le projet
2. Créez votre branche (`git checkout -b feature/AmazingFeature`)
3. Committez vos changements (`git commit -m 'feat: Add AmazingFeature'`)
4. Poussez vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrez une **Pull Request**

### Conventions de Commit

Nous utilisons [Conventional Commits](https://www.conventionalcommits.org/) :

- `feat:` Nouvelle fonctionnalité
- `fix:` Correction de bug
- `docs:` Documentation
- `style:` Formatage
- `refactor:` Refactoring
- `test:` Ajout de tests
- `chore:` Tâches de maintenance

---

---

## 👥 Auteurs

- **Mckevin-S** - *Développeur Principal* - [GitHub](https://github.com/Mckevin-S)

---


## 📞 Support

Pour toute question ou problème :

- 📧 Email : kamguemmckevin@gmail.com
- 🐛 Issues : [GitHub Issues](https://github.com/Mckevin-S/restaurant_manager/issues)
- 📖 Wiki : [Documentation complète](https://github.com/Mckevin-S/restaurant_manager/wiki)

---

