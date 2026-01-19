.PHONY: monitoring

# Démarrer tous les services avec monitoring
docker-up:
	docker-compose up -d

# Arrêter tous les services
docker-down:
	docker-compose down

# Voir les logs
docker-logs:
	docker-compose logs -f

# Rebuild les images
docker-build:
	docker-compose build

# Build et redémarrage
docker-rebuild: docker-down docker-build docker-up

# Accéder aux services
monitoring-info:
	@echo "=== Monitoring Services ==="
	@echo "Frontend:     http://localhost:5173"
	@echo "Backend API:  http://localhost:3006"
	@echo "Prometheus:   http://localhost:9090"
	@echo "Grafana:      http://localhost:3000 (admin/admin)"
	@echo "SonarQube:    http://localhost:9000 (admin/admin)"
	@echo "MySQL:        localhost:3307"
	@echo ""

# Lancer l'analyse SonarQube
sonar-analyze:
	@echo " Lancement de l'analyse SonarQube..."
	@bash analyze.sh

# Voir les logs SonarQube
sonar-logs:
	docker logs -f restaurant-sonarqube

# Redémarrer SonarQube
sonar-restart:
	docker restart restaurant-sonarqube

# Nettoyer la base SonarQube
sonar-clean:
	docker exec restaurant-mysql mysql -u sonar -psonar -e "DROP DATABASE IF EXISTS sonarqube;"
	docker exec restaurant-mysql mysql -u sonar -psonar -e "CREATE DATABASE sonarqube CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
	docker restart restaurant-sonarqube

# Nettoyer les volumes
docker-clean: docker-down
	docker-compose down -v

help:
	@echo "Available targets:"
	@echo "  docker-up        - Start all services"
	@echo "  docker-down      - Stop all services"
	@echo "  docker-logs      - View logs"
	@echo "  docker-build     - Build images"
	@echo "  docker-rebuild   - Rebuild and restart"
	@echo "  docker-clean     - Stop and remove volumes"
	@echo "  monitoring-info  - Show service URLs"
	@echo ""
	@echo "SonarQube targets:"
	@echo "  sonar-analyze    - Run code analysis"
	@echo "  sonar-logs       - View SonarQube logs"
	@echo "  sonar-restart    - Restart SonarQube"
	@echo "  sonar-clean      - Clean SonarQube database"
