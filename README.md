# Vente de formations – Application console Java

Application console de vente de formations, construite en architecture multicouche
(application, business, DAO, entités) pour pouvoir devenir plus tard une application web
sans réécrire le métier

Partie 1 : consulter le catalogue, le trier (par nom ou par date), le filtrer
 (présentiel ou distanciel), rechercher par mot-clé et afficher le détail d'une formation.

Partie optionnelle : remplir un panier sans être connecté, créer un compte ou se connecter,
  puis passer commande pour soi-même ou pour un autre client.


## 1. Prérequis

| Outil | Version conseillée | Rôle |

| JDK | 8 ou plus | Compiler et exécuter le programme |
| Visual Studio Code | récente | Ouvrir et lancer le projet |
| Extension Pack for Java | récente | Ajoute à VS Code la compilation et l'exécution Java |
| Wimp | récente | Fournit MariaDB et phpMyAdmin |
| Git | récente | Versionner le projet |
| Pilote JDBC MariaDB | `mariadb-java-client-3.5.9.jar` | Permettre à Java de se connecter à la base |

Vérifier Java et Git dans un terminal :

java -version
git --version


## 2. Récupérer le projet avec Git

-Option A : cloner le dépôt existant

git clone https://github.com/<mon-compte>/vente-formations.git
cd vente-formations

-Option B : créer le dépôt à partir de zéro

Dans le dossier du projet :


```bash
git init
git add .
git commit -m "Initialisation du projet vente de formations"
```
Puis créer un dépôt vide sur GitHub (sans README ni .gitignore) et le relier :

```bash
git remote add origin https://github.com/<mon-compte>/vente-formations.git
git branch -M main
git push -u origin main
```

### Fichier `.gitignore` conseillé

À placer à la racine du projet, pour ne pas versionner les fichiers générés :

```gitignore
# Fichiers compilés
bin/
*.class

# Réglages personnels de VS Code (on garde settings.json, utile à tous)
.vscode/*
!.vscode/settings.json

# Système
.DS_Store
Thumbs.db
```


### Bonnes pratiques de travail

- Faire des commits réguliers, avec un message clair qui dit ce qui a été fait :
  `git commit -m "adding README.md file"`.
- Développer la partie 2 sur une branche séparée, pour garder une partie 1 stable à présenter :

```bash
git checkout -b partie2
# ... travail sur le panier et les commandes ...
git checkout main        # revenir à la partie 1 pour la démo
```

---

## 3. Créer la base de données

Le script `sql/vente_formation.sql` crée **tout** en une fois : la base, les tables, les
données de test et un compte applicatif aux droits restreints. On peut
le relancer à tout moment pour remettre la base à zéro, quel que soit son état.

### Avec phpMyAdmin

1. Démarrer Wimp.
2. Ouvrir <http://localhost/phpmyadmin> et se connecter avec le compte `root`.
3. Cliquer sur l'onglet **Importer**, en haut de la page.
4. Choisir le fichier `sql/vente_formation.sql`, puis cliquer sur **Importer**.
5. Vérifier que la base `eval_java_avance` apparaît à gauche avec 7 tables :
   `formation`, `session`, `user`, `cart_item`, `customer`, `order`, `order_line`.

### En ligne de commande (alternative)

```bash
mysql -u root -p < sql/vente_formation.sql
```

### Ce que contient la base après le script

- **7 formations** : Python, Java, Git (présentiel et distanciel), Vercel (présentiel et
  distanciel) et C#, chacune avec une session datée.
- **Une session complète** (Git présentiel), pour tester l'affichage « COMPLET ».
- **Un compte de test** : email `test@formation.fr`, mot de passe `test`.
- **Un compte applicatif** `formation_app` / `change_me`. L'application se connecte avec ce
  compte, jamais avec `root`. Il ne peut ni modifier ni supprimer une commande.

---

## 4. Configurer la connexion à la base

Les paramètres de connexion ne sont pas écrits dans le code : ils sont lus dans le fichier
`resources/config.properties`. Vérifier qu'il contient :

```properties
db.driver.class=org.mariadb.jdbc.Driver
db.url=jdbc:mariadb://localhost:3306/eval_java_avance
db.login=formation_app
db.password=change_me
```

---

## 5. Ouvrir le projet dans VS Code

1. Installer l'extension **Extension Pack for Java** (éditée par Microsoft) depuis l'onglet
   Extensions de VS Code (`Ctrl+Shift+X`).
2. **Fichier → Ouvrir le dossier…** et choisir le dossier du projet `vente-formations`.
3. Attendre que VS Code finisse de charger le projet Java (indicateur en bas à gauche).
4. Vérifier que le pilote JDBC est bien pris en compte. Le fichier `.vscode/settings.json`
   doit contenir :

```json
{
    "java.project.sourcePaths": ["src"],
    "java.project.outputPath": "bin",
    "java.project.referencedLibraries": ["lib/**/*.jar"]
}
```

   Tout fichier `.jar` placé dans `lib/` est alors ajouté automatiquement. On peut aussi le
   vérifier dans le panneau **Java Projects** (en bas de l'explorateur), rubrique
   **Referenced Libraries**.

5. Vérifier qu'aucune erreur n'apparaît dans l'onglet **Problèmes** (`Ctrl+Shift+M`).

---

## 6. Lancer l'application

1. Ouvrir la classe `src/fr/fms/app/App.java`.
2. Cliquer sur **Run** juste au-dessus de la méthode `main`, ou appuyer sur `F5`.
3. Le menu s'affiche dans le **terminal** intégré de VS Code, où l'on tape ses choix.

---

## 7. Utiliser l'application

### Menu principal

```
1. Afficher le catalogue (tri par nom)
2. Afficher le catalogue (tri par date)
3. Afficher seulement le présentiel
4. Afficher seulement le distanciel
5. Rechercher par mot-clé
0. Quitter
```

### Parcours de démonstration conseillé

1. **Catalogue** : afficher la liste. Seuls le nom, le mode et le prix apparaissent, avec
   « COMPLET » pour Git présentiel.
2. **Tri** : comparer le tri par nom (C# en premier) et le tri par date (Git distanciel en
   premier).
3. **Recherche** : taper `developpeur` sans accent, puis un mot absent comme `cuisine`, qui
   affiche « Aucune formation ne correspond à votre recherche ».
4. **Détail** : choisir une formation par son numéro pour voir sa description, sa durée,
   sa date et ses places, puis revenir en arrière.

Le panier, les comptes et les commandes (partie 2) sont développés sur la branche `partie2`.

### Règles de gestion appliquées

| N° | Règle |
| --- | --- |
| RG1 | Une formation est soit en présentiel, soit en distanciel. Un même nom n'est possible que si le mode diffère. |
| RG2 | Le prix (TTC) et la durée (7, 14 ou 21 jours) dépendent du mode. |
| RG3 | Une session sans place est affichée « COMPLET » et ne peut pas être ajoutée au panier. |
| RG4 | Une même session apparaît au plus une fois dans un panier et dans une commande. |
| RG5 | L'email sert d'identifiant de connexion et est unique. |
| RG6 | On ne peut pas commander avec un panier vide, ni sans être connecté. |
| RG7 | Une commande concerne un seul client, saisi au moment de commander. |
| RG8 | Une commande n'est ni modifiable ni annulable. |

---

## 8. Remettre la base à zéro

Relancer simplement le script SQL (étape 3). Il supprime puis recrée toutes les tables et
leurs données de test : les paniers, comptes et commandes créés pendant les essais
disparaissent.

---

## 9. Organisation du projet

```
vente-formations/
├── README.md
├── .gitignore
├── .vscode/
│   └── settings.json
├── lib/
│   └── mariadb-java-client-3.5.9.jar
├── resources/
│   └── config.properties
├── sql/
│   └── vente_formation.sql
├── docs/
│   ├── specifications-fonctionnelles.pdf
│   ├── cas-utilisation.png
│   ├── diagramme-classes.puml / .png
│   ├── diagramme-sequence.puml / .png
│   └── mcd.loo / .png
└── src/fr/fms/
    ├── entities/   Formation, Session, Mode, User, Cart, Customer, Order
    ├── dao/        Accès à la base (pattern DAO, singleton de connexion, factory)
    ├── business/   Règles métier (catalogue, panier, utilisateurs, commandes)
    └── app/        App : menu console, point d'entrée de l'application
```

Chaque couche ne parle qu'à la couche voisine : `app` appelle `business`, qui appelle `dao`,
qui interroge la base. Les `entities` circulent entre toutes les couches. Pour passer au web,
il suffira de remplacer la couche `app`.