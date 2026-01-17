-- Créer la base de données SonarQube si elle n'existe pas
CREATE DATABASE IF NOT EXISTS sonarqube CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Créer l'utilisateur SonarQube s'il n'existe pas
CREATE USER IF NOT EXISTS 'sonar'@'%' IDENTIFIED BY 'sonar';

-- Accorder les permissions
GRANT ALL PRIVILEGES ON sonarqube.* TO 'sonar'@'%';
FLUSH PRIVILEGES;
