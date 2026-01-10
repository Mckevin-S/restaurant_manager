# 🤝 Guide de Contribution

Merci de votre intérêt pour contribuer au projet Restaurant Manager ! Ce document vous guidera à travers le processus de contribution.

## 📋 Table des Matières

- [Code de Conduite](#code-de-conduite)
- [Comment Contribuer](#comment-contribuer)
- [Standards de Code](#standards-de-code)
- [Processus de Pull Request](#processus-de-pull-request)
- [Signaler un Bug](#signaler-un-bug)
- [Proposer une Fonctionnalité](#proposer-une-fonctionnalité)

---

## 📜 Code de Conduite

En participant à ce projet, vous acceptez de respecter notre code de conduite. Soyez respectueux, inclusif et professionnel dans toutes vos interactions.

---

## 🚀 Comment Contribuer

### 1. Fork et Clone

```bash
# Fork le projet sur GitHub, puis :
git clone https://github.com/VOTRE-USERNAME/restaurant_manager.git
cd restaurant_manager
git remote add upstream https://github.com/Mckevin-S/restaurant_manager.git
```

### 2. Créer une Branche

```bash
# Toujours créer une nouvelle branche pour vos modifications
git checkout -b feature/nom-de-votre-fonctionnalite

# Ou pour un bugfix
git checkout -b fix/description-du-bug
```

### 3. Faire vos Modifications

- Écrivez du code propre et commenté
- Suivez les conventions de code du projet
- Ajoutez des tests pour vos modifications
- Mettez à jour la documentation si nécessaire

### 4. Tester

```bash
# Backend
cd BackendProject
mvn test

# Frontend
cd Gusto
npm test
```

### 5. Commit

Utilisez des messages de commit clairs suivant la convention [Conventional Commits](https://www.conventionalcommits.org/) :

```bash
git add .
git commit -m "feat: ajouter la gestion des réservations"
```

**Types de commits** :
- `feat:` Nouvelle fonctionnalité
- `fix:` Correction de bug
- `docs:` Documentation uniquement
- `style:` Formatage, point-virgules manquants, etc.
- `refactor:` Refactoring de code
- `test:` Ajout ou modification de tests
- `chore:` Maintenance, dépendances, etc.

### 6. Push et Pull Request

```bash
git push origin feature/nom-de-votre-fonctionnalite
```

Puis créez une Pull Request sur GitHub avec :
- Un titre clair
- Une description détaillée des changements
- Des captures d'écran si pertinent
- La référence aux issues liées

---

## 💻 Standards de Code

### Backend (Java/Spring Boot)

```java
/**
 * Toujours documenter les classes et méthodes publiques
 * @param id Identifiant du plat
 * @return Le plat trouvé
 * @throws RessourceNonTrouveeException Si le plat n'existe pas
 */
public PlatDto getById(Long id) {
    // Code bien indenté et lisible
    return platRepository.findById(id)
        .map(platMapper::toDto)
        .orElseThrow(() -> new RessourceNonTrouveeException("Plat", "id", id));
}
```

**Conventions** :
- Noms de classes en PascalCase
- Noms de méthodes en camelCase
- Noms de variables descriptifs
- Utiliser les annotations Spring appropriées
- Gérer les exceptions correctement
- Écrire des tests unitaires

### Frontend (React/JavaScript)

```javascript
/**
 * Composant pour afficher un plat
 * @param {Object} props - Les propriétés du composant
 * @param {Object} props.plat - Les données du plat
 */
const PlatCard = ({ plat }) => {
  // Hooks en premier
  const [loading, setLoading] = useState(false);
  
  // Fonctions ensuite
  const handleClick = () => {
    // ...
  };
  
  // Rendu
  return (
    <div className="plat-card">
      {/* Contenu */}
    </div>
  );
};
```

**Conventions** :
- Composants en PascalCase
- Fonctions et variables en camelCase
- Utiliser les hooks React appropriés
- Déstructurer les props
- Commenter le code complexe

---

## 🔄 Processus de Pull Request

### Checklist avant de soumettre

- [ ] Le code compile sans erreurs
- [ ] Tous les tests passent
- [ ] Le code suit les conventions du projet
- [ ] La documentation est à jour
- [ ] Les commits sont bien formatés
- [ ] Pas de fichiers sensibles (.env, secrets, etc.)
- [ ] Le code est testé localement

### Revue de Code

Votre PR sera examinée par les mainteneurs. Soyez patient et réceptif aux commentaires. Les points vérifiés :

1. **Qualité du code** : Lisibilité, maintenabilité
2. **Tests** : Couverture et pertinence
3. **Performance** : Pas de régressions
4. **Sécurité** : Pas de failles introduites
5. **Documentation** : Clarté et complétude

---

## 🐛 Signaler un Bug

### Avant de signaler

1. Vérifiez que le bug n'a pas déjà été signalé
2. Assurez-vous d'utiliser la dernière version
3. Vérifiez que ce n'est pas un problème de configuration

### Template de Bug Report

```markdown
**Description du Bug**
Une description claire et concise du bug.

**Étapes pour Reproduire**
1. Aller à '...'
2. Cliquer sur '...'
3. Faire défiler jusqu'à '...'
4. Voir l'erreur

**Comportement Attendu**
Ce qui devrait se passer normalement.

**Comportement Actuel**
Ce qui se passe réellement.

**Captures d'écran**
Si applicable, ajoutez des captures d'écran.

**Environnement**
- OS: [e.g. Windows 11]
- Navigateur: [e.g. Chrome 120]
- Version: [e.g. 1.0.0]

**Logs**
```
Collez les logs pertinents ici
```

**Contexte Additionnel**
Toute autre information utile.
```

---

## ✨ Proposer une Fonctionnalité

### Template de Feature Request

```markdown
**Problème à Résoudre**
Décrivez le problème que cette fonctionnalité résoudrait.

**Solution Proposée**
Décrivez comment vous imaginez la fonctionnalité.

**Alternatives Considérées**
Autres approches que vous avez envisagées.

**Bénéfices**
- Bénéfice 1
- Bénéfice 2

**Complexité Estimée**
Faible / Moyenne / Élevée

**Captures d'écran / Maquettes**
Si vous avez des visuels.
```

---

## 📚 Ressources Utiles

- [Documentation Spring Boot](https://spring.io/projects/spring-boot)
- [Documentation React](https://react.dev/)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Git Flow](https://nvie.com/posts/a-successful-git-branching-model/)

---

## 💬 Questions ?

Si vous avez des questions, n'hésitez pas à :

- Ouvrir une [Discussion GitHub](https://github.com/Mckevin-S/restaurant_manager/discussions)
- Contacter les mainteneurs
- Consulter la [documentation](https://github.com/Mckevin-S/restaurant_manager/wiki)

---

**Merci de contribuer au projet Restaurant Manager ! 🎉**
