# Guide de Configuration du Monitoring

## Composants du Monitoring

### 1. **Prometheus** (Collecteur de Métriques)
- **URL**: http://localhost:9090
- **Rôle**: Scrape les métriques du backend Spring Boot
- **Config**: `prometheus.yml`
- **Stockage**: Volume Docker `prometheus-data`

### 2. **Grafana** (Visualisation)
- **URL**: http://localhost:3000
- **Credentials**: admin/admin (par défaut)
- **Rôle**: Visualiser les métriques avec des dashboards
- **Dashboards**: Dashboards Spring Boot préinstallés

### 3. **Spring Boot Actuator**
- **Endpoints**:
  - `/actuator/health` - État de santé
  - `/actuator/metrics` - Liste des métriques
  - `/actuator/prometheus` - Format Prometheus
  - `/actuator/info` - Information app
  - `/actuator/env` - Variables d'environnement
- **Port**: 3006 (Backend)

## Setup Initial

### 1. Ajouter la source de données Prometheus à Grafana

1. Ouvrez http://localhost:3000
2. Allez à **Configuration** > **Data Sources**
3. Cliquez **Add data source**
4. Sélectionnez **Prometheus**
5. URL: `http://prometheus:9090`
6. Cliquez **Save & Test**

### 2. Importer le dashboard Spring Boot

1. Dans Grafana, allez à **Dashboards** > **Import**
2. Uploadez le fichier `spring-boot-dashboard.json`
3. Sélectionnez la source de données Prometheus
4. Cliquez **Import**

## Métriques Moniteurées

### Métriques HTTP
- `http_server_requests_seconds_count` - Nombre de requêtes
- `http_server_requests_seconds` - Latence des requêtes
- Codes HTTP (2xx, 4xx, 5xx)

### Métriques JVM
- `jvm_memory_used_bytes` - Mémoire utilisée
- `jvm_memory_max_bytes` - Mémoire max
- `jvm_threads_live_threads` - Threads actifs
- `jvm_gc_memory_promoted_bytes_total` - GC promotions

### Métriques Tomcat
- `tomcat_threads_live_threads` - Threads Tomcat
- `tomcat_threads_busy_threads` - Threads occupés
- `tomcat_threads_max_threads` - Threads max

### Métriques Base de Données
- Nombre de connexions
- Temps de requête

## Commandes Utiles

```bash
# Démarrer tous les services
docker-compose up -d

# Voir les logs
docker-compose logs -f backend
docker-compose logs -f prometheus
docker-compose logs -f grafana

# Arrêter les services
docker-compose down

# Supprimer les volumes (clean)
docker-compose down -v
```

## Requêtes Prometheus Utiles

### Requêtes PromQL pour les graphiques:

```promql
# Requêtes par seconde
rate(http_server_requests_seconds_count[5m])

# Latence p95
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))

# Latence p99
histogram_quantile(0.99, rate(http_server_requests_seconds_bucket[5m]))

# Taux d'erreurs
rate(http_server_requests_seconds_count{status=~"4..|5.."}[5m])

# Mémoire JVM utilisée
jvm_memory_used_bytes{area="heap"}

# CPU usage
process_cpu_usage

# Statut du backend
up{job="spring-boot-backend"}

# Connexions actives à la base de données
hikaricp_connections_active
```

## Configuration pour Production

### Variables d'environnement (.env)

```env
# Base de données
DB_ROOT_PASSWORD=votre_pwd_root
DB_USER=restaurant_user
DB_PASSWORD=votre_pwd_user

# JWT
JWT_SECRET=votre_secret_jwt

# Grafana
GRAFANA_ADMIN_USER=admin
GRAFANA_ADMIN_PASSWORD=votre_pwd_grafana
```

## Alertes et Seuils Recommandés

### Créer une alerte Grafana

1. Dans un panel, allez à **Alert** tab
2. Définissez les conditions:
   - Latence p95 > 500ms
   - Taux d'erreurs > 5%
   - Mémoire JVM > 80%
   - Threads > 100
3. Configurez les notifications (email, Slack, etc.)

## Troubleshooting

### Prometheus ne scrape pas le backend
- Vérifier: `http://localhost:9090/targets`
- Vérifier la config `prometheus.yml`
- Vérifier que le backend est accessible: `http://localhost:3006/actuator/prometheus`

### Grafana ne voit pas les données
- Vérifier que Prometheus scrape les données
- Vérifier la connexion à la source de données
- Attendre quelques minutes pour les premières données

### Mémoire des volumes
```bash
# Voir l'utilisation des volumes
docker system df

# Nettoyer
docker system prune
```

## Améliorations Futures

- [ ] Alerting intégré (AlertManager)
- [ ] Logs centralisés (ELK Stack)
- [ ] Traces distribuées (Jaeger)
- [ ] Dashboards personnalisés
- [ ] Notifications (Slack, Email)
- [ ] Sauvegardes automatiques des dashboards
