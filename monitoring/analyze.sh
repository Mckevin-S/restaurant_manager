#!/bin/bash

# Script d'analyse SonarQube pour le backend

set -e

echo "🔍 Démarrage de l'analyse SonarQube..."
echo ""

# Configuration
SONAR_HOST_URL="${SONAR_HOST_URL:-http://localhost:9000}"
SONAR_PROJECT_KEY="restaurant-backend"

# Vérifier si SonarQube est accessible
echo "✓ Vérification de la connexion à SonarQube..."
if ! curl -f -s "$SONAR_HOST_URL/api/system/status" > /dev/null; then
    echo "❌ Erreur: SonarQube n'est pas accessible à $SONAR_HOST_URL"
    echo "Veuillez démarrer SonarQube avec: docker-compose up -d sonarqube"
    exit 1
fi
echo "✓ SonarQube est accessible"
echo ""

# Demander le token si non fourni
if [ -z "$SONAR_LOGIN" ]; then
    echo "⚠️  Token SonarQube non trouvé"
    echo "Accédez à $SONAR_HOST_URL et générez un token:"
    echo "  Account > Security > Generate Token"
    echo ""
    read -p "Entrez votre token SonarQube: " SONAR_LOGIN
    export SONAR_LOGIN=$SONAR_LOGIN
fi

# Se placer dans le répertoire du backend
cd "$(dirname "$0")/../BackendProject"

echo "🏗️  Compilation et tests..."
mvn clean test 2>&1 | tail -20

echo ""
echo "📊 Envoi des résultats à SonarQube..."
mvn sonar:sonar \
    -Dsonar.projectKey=$SONAR_PROJECT_KEY \
    -Dsonar.host.url=$SONAR_HOST_URL \
    -Dsonar.login=$SONAR_LOGIN

echo ""
echo "✅ Analyse terminée!"
echo "📈 Consultez les résultats sur: $SONAR_HOST_URL/dashboard?id=$SONAR_PROJECT_KEY"
