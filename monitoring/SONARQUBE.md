# SonarQube - Analyse de Qualité du Code

## Vue d'ensemble

SonarQube est une plateforme open-source d'analyse de code statique qui évalue:
- **Bugs**: Erreurs potentielles du code
- **Vulnérabilités**: Failles de sécurité
- **Code Smell**: Problèmes de maintenabilité
- **Couverture de test**: Pourcentage de code couvert par les tests
- **Duplication**: Code dupliqué

## Accès à SonarQube

- **URL**: http://localhost:9000
- **Credentials par défaut**: admin / admin
- **Port**: 9000

## Architecture

### Services

1. **SonarQube Server** (port 9000)
   - Interface web
   - Stockage des analyses
   - Base de données PostgreSQL/MySQL

2. **MySQL Database**
   - Stockage des résultats d'analyse
   - Configuration de SonarQube

### Volumes

- `sonarqube-data`: Données de SonarQube
- `sonarqube-logs`: Logs de SonarQube
- `sonarqube-extensions`: Plugins SonarQube

## Configuration Initiale

### 1. Démarrer SonarQube

```bash
docker-compose up -d sonarqube
```

Attendre 60-90 secondes pour que SonarQube soit prêt.

### 2. Configuration du Projet dans SonarQube

1. Accédez à http://localhost:9000
2. Cliquez **Create a local project**
3. **Project key**: `restaurant-backend`
4. **Project name**: `Restaurant Manager Backend`
5. Cliquez **Create project**
6. Sélectionnez **Maven** comme build tool
7. Copier la **sonar login token** (ou générer un nouveau)

### 3. Configuration du Token

```bash
# Stocker le token dans les variables d'environnement
export SONAR_LOGIN=<votre_token>
export SONAR_HOST_URL=http://localhost:9000
```

## Lancer l'Analyse

### Option 1: Maven (Recommandé)

```bash
cd BackendProject

# Nettoyer et analyser
mvn clean verify sonar:sonar \
  -Dsonar.login=<votre_token> \
  -Dsonar.host.url=http://localhost:9000

# Ou avec les variables d'env
mvn clean verify sonar:sonar
```

### Option 2: SonarScanner (Standalone)

```bash
# Installer SonarScanner
# https://docs.sonarqube.org/latest/analyzing-source-code/scanners/sonarscanner/

# Configurer sonar-scanner.properties
sonar.host.url=http://localhost:9000
sonar.login=<votre_token>
sonar.projectKey=restaurant-backend
sonar.sources=.

# Lancer le scanner
sonar-scanner
```

## Métriques Analysées

### Bugs et Vulnérabilités

- **Blocker**: Erreurs critiques
- **Critical**: Risques de sécurité élevés
- **Major**: Problèmes importants
- **Minor**: Améliorations mineures
- **Info**: Informations

### Maintenabilité

- **Code Smell**: Problèmes de qualité/maintenabilité
- **Cognitive Complexity**: Complexité du code
- **Code Duplication**: Pourcentage de code dupliqué

### Couverture de Test

```bash
# Générer les rapports de couverture
mvn clean test jacoco:report

# SonarQube lit automatiquement:
# target/site/jacoco/jacoco.xml
```

### Quality Gate

Un Quality Gate est un ensemble de conditions qui doivent être respectées:

**Quality Gate par défaut**:
- Coverage >= 80%
- Duplicated Lines (%) <= 3%
- Maintainability Rating <= A
- Security Rating = A
- Security Hotspots Reviewed = 100%

## Fichiers de Configuration

### pom.xml - Propriétés SonarQube

```xml
<properties>
  <sonar.projectKey>restaurant-backend</sonar.projectKey>
  <sonar.host.url>http://localhost:9000</sonar.host.url>
  <sonar.sources>src/main</sonar.sources>
  <sonar.tests>src/test</sonar.tests>
  <sonar.coverage.jacoco.xmlReportPaths>target/site/jacoco/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>
</properties>
```

### sonar-project.properties

Configuration du projet SonarQube:
- Clé du projet
- Répertoires sources
- Couverture de code
- Exclusions

## Pipeline CI/CD avec SonarQube

### Exemple: GitHub Actions

```yaml
name: SonarQube Analysis

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  sonarqube:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      
      - name: Build and analyze
        working-directory: BackendProject
        run: |
          mvn clean verify sonar:sonar \
            -Dsonar.projectKey=restaurant-backend \
            -Dsonar.host.url=${{ secrets.SONAR_HOST_URL }} \
            -Dsonar.login=${{ secrets.SONAR_TOKEN }}
```

### Exemple: GitLab CI

```yaml
sonarqube-check:
  stage: test
  image: maven:3.8.1-jdk-17
  variables:
    SONAR_HOST_URL: $SONAR_HOST_URL
    SONAR_LOGIN: $SONAR_TOKEN
  script:
    - cd BackendProject
    - mvn clean verify sonar:sonar
  only:
    - merge_requests
    - main
```

## Règles de Qualité Personnalisées

### Créer un Quality Profile

1. Accédez à **Quality Profiles** dans SonarQube
2. Cliquez **Create**
3. Sélectionnez la langue (Java)
4. Nommez le profil (ex: "Restaurant Backend")
5. Ajoutez/désactivez les règles selon vos besoins

### Exemple: Désactiver certaines règles

- Code smell trop strict
- Règles inutiles pour votre contexte
- Ajouter des règles custom

## Intégration avec le Monitoring

### Ajouter SonarQube à la stack monitoring

```bash
# Créer un dashboard Grafana pour SonarQube
# Plugin Grafana: SonarQube
```

## Commandes Utiles

```bash
# Nettoyer les analyses précédentes
docker exec restaurant-sonarqube curl -X DELETE \
  http://localhost:9000/api/ce/projects?project=restaurant-backend \
  -u admin:admin

# Voir les logs
docker logs -f restaurant-sonarqube

# Accéder à la DB SonarQube
docker exec -it restaurant-mysql mysql -u restaurant_user -p restaurant_pass

# Vérifier la santé
curl http://localhost:9000/api/system/health | jq
```

## Troubleshooting

### SonarQube ne démarre pas

```bash
# Vérifier les logs
docker logs restaurant-sonarqube

# Vérifier la connexion DB
docker logs restaurant-mysql

# Redémarrer
docker restart restaurant-sonarqube
```

### Analyse Maven ne se connecte pas

```bash
# Vérifier la connectivité
curl http://localhost:9000/api/system/status

# Token invalide?
# Régénérer le token dans SonarQube: 
# Account > Security > Generate Token
```

### Base de données SonarQube pleine

```bash
# Nettoyer les anciennes analyses
curl -X DELETE \
  http://localhost:9000/api/ce/projects?inactiveMonthsBeforeDelete=1 \
  -u admin:admin
```

## Bonnes Pratiques

1. **Exécuter l'analyse avant chaque commit**
   - Utiliser Git Hooks
   - CI/CD automatique

2. **Maintenir une couverture de test >= 80%**
   - Ajouter des tests pour les nouvelles fonctionnalités
   - Améliorer progressivement

3. **Corriger les bugs et vulnérabilités en priorité**
   - Blocker/Critical en premier
   - Major ensuite

4. **Réduire les Code Smell**
   - Refactoring progressif
   - Amélioration continue

5. **Documenter le code**
   - Javadoc pour les classes publiques
   - Commentaires pour la logique complexe

## Nextés Futures

- [ ] Alerting sur les issues critiques
- [ ] Intégration Jira pour tracking
- [ ] Rapports hebdomadaires
- [ ] Benchmarking avec d'autres projets
- [ ] Plugin personnalisés
- [ ] Intégration complète CI/CD
