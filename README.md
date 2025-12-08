# Application de Gestion de Restaurant 


Ce projet est une application de gesttion de restaurant qui utilise
les languages springboot pour le backend et ReactJS pour le frontend 

#  1. Prérequis

Avant de lancer le projet, assurez-vous d'avoir installé :

-   **Java 17+**
-   **WampServer ou Xamp**
-   **Git**
-   **Un IDE (IntelliJ IDEA recommandé ou vsCode selon votre preference)**

------------------------------------------------------------------------

# 2. Cloner le projet

Ouvrez le repertoire ou vous voulez cloner le projet depui votre terminal
et entrez la commande ci-dessous
``` bash
git clone https://github.com/Mckevin-S/restaurant_manager.git
```

Accédez au dossier :

``` bash
cd restaurant_manager
```

------------------------------------------------------------------------

#  3. Gestion des branches Git

##  3.1 Voir les branches existantes

``` bash
git branch -a
```

------------------------------------------------------------------------

##  3.2 Créer une nouvelle branche locale

``` bash
git checkout -b feature/nom-de-la-fonctionnalite
```

------------------------------------------------------------------------

##  3.3 Lier une branche locale à une branche distante existante

``` bash
git checkout -b feature/nom-de-la-fonctionnalite origin/feature/nom-de-la-fonctionnalite
```

------------------------------------------------------------------------

##  3.4 Pousser une nouvelle branche locale vers le dépôt distant

``` bash
git push -u origin feature/nom-de-la-fonctionnalite
```

------------------------------------------------------------------------

##  3.5 Mettre à jour votre branche

``` bash
git pull origin feature/nom-de-la-fonctionnalite
```

------------------------------------------------------------------------
# 4 . Build & Exécution

##  4.1 Compiler

``` bash
mvn clean install
```

##  4.2 Lancer

``` bash
mvn spring-boot:run
```

ou :

``` bash
java -jar target/restaurant-0.0.1-SNAPSHOT.jar
```

------------------------------------------------------------------------

#  5. Tests

``` bash
mvn test
```

------------------------------------------------------------------------

#  6. Workflow Git

``` bash
git checkout -b feature/gestion-commandes
git add .
git commit -m "feat: ajout de la gestion des commandes"
git push -u origin feature/gestion-commandes
```




