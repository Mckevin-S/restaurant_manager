@echo off
REM Script d'analyse SonarQube pour le backend (Windows)

echo.
echo  Demarrage de l'analyse SonarQube...
echo.

REM Configuration
set SONAR_HOST_URL=http://localhost:9000
set SONAR_PROJECT_KEY=restaurant-backend

REM Verifier si SonarQube est accessible
echo Verification de la connexion a SonarQube...
curl -f -s "%SONAR_HOST_URL%/api/system/status" > nul
if errorlevel 1 (
    echo.
    echo  Erreur: SonarQube n'est pas accessible a %SONAR_HOST_URL%
    echo Veuillez demarrer SonarQube avec: docker-compose up -d sonarqube
    pause
    exit /b 1
)
echo ✓ SonarQube est accessible
echo.

REM Demander le token si non fourni
if "%SONAR_LOGIN%"=="" (
    echo Token SonarQube non trouve
    echo Acces a %SONAR_HOST_URL% et generez un token:
    echo   Account ^> Security ^> Generate Token
    echo.
    set /p SONAR_LOGIN="Entrez votre token SonarQube: "
)

REM Se placer dans le repertoire du backend
cd /d "%~dp0..\BackendProject"

echo  Compilation et tests...
call mvn clean test

echo.
echo  Envoi des resultats a SonarQube...
call mvn sonar:sonar ^
    -Dsonar.projectKey=%SONAR_PROJECT_KEY% ^
    -Dsonar.host.url=%SONAR_HOST_URL% ^
    -Dsonar.login=%SONAR_LOGIN%

echo.
echo  Analyse terminee!
echo  Consultez les resultats sur: %SONAR_HOST_URL%/dashboard?id=%SONAR_PROJECT_KEY%
pause
