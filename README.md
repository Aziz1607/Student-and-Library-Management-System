# Projet CORBA (Java IDL) - Gestion d'Institut & Bibliothèque

Projet d'architecture distribuée basé sur **CORBA** (Common Object Request Broker Architecture) et **Java IDL**.
Il permet la gestion à distance d'une promotion d'étudiants, le calcul des moyennes pondérées par épreuve, ainsi que l'emprunt de livres au sein d'une bibliothèque universitaire.

---

## 📋 Table des matières

1. [Architecture & Vue d'ensemble](#-architecture--vue-densemble)
2. [Structure du projet](#-structure-du-projet)
3. [Prérequis importants (Java 8)](#-prérequis-importants-java-8)
4. [Analyse du code & Améliorations apportées](#-analyse-du-code--améliorations-apportées)
5. [Guide de compilation et d'exécution](#-guide-de-compilation-et-dexécution)
6. [Fonctionnalités du Client](#-fonctionnalités-du-client)

---

## 🏛️ Architecture & Vue d'ensemble

Le projet repose sur le modèle client-serveur CORBA :
- **IDL (`Institue.idl`)** : Spécifie les structures de données (`Epreuve`, `Livre`) et les interfaces distantes (`Etudiant`, `Promotion`).
- **Service de nommage (`tnameserv`)** : Permet au serveur d'enregistrer l'objet distant `Promotion` et au client de le résoudre dynamiquement (sur le port 900 par défaut).
- **Serveur (`serveur/`)** : Implémente les servants POA (`PromotionImpl`, `EtudiantImpl`) et active les objets distants via le RootPOA.
- **Client (`client/`)** : Fournit une interface console (CLI) conviviale et sécurisée pour interagir avec le serveur.

---

## 📁 Structure du projet

```
ExamenMidd/
│
├── Institue.idl              # Spécification IDL de l'institut
│
├── Institue/                 # Fichiers générés par le compilateur idlj
│   ├── Epreuve.java          # Struct représentant une épreuve
│   ├── Livre.java            # Struct représentant un livre
│   ├── Etudiant.java         # Interface CORBA pour un étudiant
│   ├── EtudiantPOA.java      # Skeleton POA pour Etudiant
│   ├── Promotion.java        # Interface CORBA pour la promotion
│   ├── PromotionPOA.java     # Skeleton POA pour Promotion
│   └── ...                   # Helpers, Holders et Stubs générés
│
├── serveur/                  # Implémentation du serveur
│   ├── EtudiantImpl.java     # Servant Etudiant (calculs notes, emprunts)
│   ├── PromotionImpl.java    # Servant Promotion (recherche, moyenne promo)
│   └── Serveur.java          # Programme principal (initialisation ORB, Naming)
│
├── client/                   # Interface client
│   └── Client.java           # Programme console interactif
│
├── compile.bat               # Script de compilation automatique
├── run-serveur.bat           # Script de lancement du serveur
├── run-client.bat            # Script de lancement du client
└── README.md                 # Documentation du projet
```

---

## ⚠️ Prérequis importants (Java 8)

> **Important** : Le module CORBA (`org.omg.CORBA.*`, `idlj`, `tnameserv`) a été déprécié dans Java 9 et **définitivement supprimé du JDK standard à partir de Java 11**.  
> Ce projet nécessite donc **Java 8 (JDK 1.8)** pour être compilé et exécuté.

Si vous avez plusieurs versions de Java installées, définissez votre variable `JAVA_HOME` vers JDK 8 :
```powershell
# Exemple sous Windows PowerShell :
$env:JAVA_HOME = "C:\Program Files\Java\jdk1.8.0_202"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```
*(Les scripts `.bat` fournis détectent automatiquement JDK 1.8 s'il est installé dans le chemin standard).*

---

## 🔍 Analyse du code & Améliorations apportées

Le code initial comportait plusieurs anomalies critiques qui provoquaient des plantages système ou faussaient les résultats. Voici les améliorations majeures apportées :

### 1. Correction du calcul de la moyenne (`CalculerLaMoyenne`)
* **Problème initial** : 
  1. Si l'étudiant n'avait aucune épreuve (`totalCoef == 0`), le calcul produisait `NaN`.
  2. L'utilisation de `DecimalFormat` dépendait de la langue du système. Sous un environnement français/européen, le formateur écrivait une virgule (ex: `"14,50"`), provoquant un crash immédiat `java.lang.NumberFormatException: For input string: "14,50"`.
* **Correction** : Sécurisation de la division par zéro (retourne `0.0` si aucune épreuve) et arrondi à 2 décimales via `Math.round(moy * 100.0) / 100.0`, indépendant de la Locale.

### 2. Correction de l'emprunt de livre (`EmprunterUnLivre`)
* **Problème initial** :
  1. En cas de livre inexistant sans emprunt préalable, la méthode exécutait `this.livres.get(-1)`, provoquant une `ArrayIndexOutOfBoundsException`.
  2. Lorsque le quota de 2 livres était dépassé, la méthode renvoyait `null`. Or, en CORBA, un `struct` (`Livre`) **ne peut pas être nul lors de la sérialisation**, provoquant un `NullPointerException` fatale sur le serveur lors de la réponse au client.
* **Correction** : 
  - La méthode ne renvoie jamais `null`. En cas d'erreur ou de quota atteint, un objet `Livre` sentinelle explicatif avec des chaînes non nulles est retourné (ex: numéro 0 ou négatif avec message d'erreur).
  - Validation du quota (maximum 2 livres par étudiant) et interdiction d'emprunter deux fois le même livre.

### 3. Moyenne de la promotion (`CalculerMoyenneDeLaPromotion`)
* **Problème initial** : Si la liste des étudiants était vide, `somme / 0` produisait `NaN`.
* **Correction** : Vérification de liste vide (`return 0.0`) et arrondi sécurisé à 2 décimales.

### 4. Gestion des étudiants en doublon
* **Problème initial** : Aucun contrôle d'unicité lors de l'ajout d'un étudiant (`AjouterUnEtudiant`).
* **Correction** : Vérification du numéro d'étudiant avant enregistrement pour éviter les doublons.

### 5. Démarrage et arrêt du serveur (`Serveur.java`)
* **Problème initial** : `Runtime.getRuntime().exec("tnameserv ...")` était exécuté de manière asynchrone sans attendre le démarrage du socket (race condition `COMM_FAILURE`), et le processus restait orphelin à l'arrêt.
* **Correction** : Ajout d'un délai d'attente pour initialisation socket, gestion des cas où `tnameserv` tourne déjà, et ajout d'un `ShutdownHook` pour stopper proprement le service lors de l'arrêt du serveur.

### 6. Robustesse de l'interface client (`Client.java`)
* **Problème initial** : La moindre saisie de texte à la place d'un chiffre faisait crasher l'application avec `InputMismatchException`. Le client ne donnait aucune visibilité sur les livres disponibles avant d'emprunter.
* **Correction** :
  - Méthodes de lecture sécurisées (`lireEntier`, `lireDouble`) avec boucle de re-saisie.
  - Support transparent du point `.` et de la virgule `,` pour la saisie des notes décimales.
  - Validation des notes (entre 0 et 20) et des coefficients (> 0).
  - Affichage automatique du catalogue de la bibliothèque lors de l'option d'emprunt.
  - Messages clairs en cas de succès ou d'échec.

---

## 🚀 Guide de compilation et d'exécution

### Option A : Avec les scripts automatiques (Recommandé)

1. **Compiler le projet** :
   ```cmd
   .\compile.bat
   ```

2. **Démarrer le serveur** (dans un premier terminal) :
   ```cmd
   .\run-serveur.bat
   ```

3. **Lancer le client** (dans un second terminal) :
   ```cmd
   .\run-client.bat
   ```

---

### Option B : Commandes manuelles

1. **Générer les fichiers IDL (si modification de `Institue.idl`)** :
   ```cmd
   idlj -fall Institue.idl
   ```

2. **Compiler tous les fichiers Java** :
   ```cmd
   javac -encoding UTF-8 Institue\*.java serveur\*.java client\*.java
   ```

3. **Lancer le serveur** :
   ```cmd
   java -cp . serveur.Serveur
   ```

4. **Lancer le client** :
   ```cmd
   java -cp . client.Client
   ```

*(Par défaut, le port utilisé pour le service de nommage est le port **900**).*

---

## 💻 Fonctionnalités du Client

Le client propose un menu interactif complet :

```
====================== MENU ======================
  1. Ajouter un étudiant
  2. Ajouter une épreuve à un étudiant
  3. Liste des épreuves d'un étudiant
  4. Calculer la moyenne d'un étudiant
  5. Calculer la moyenne de la promotion
  6. Rechercher un étudiant
  7. Emprunter un livre pour un étudiant
  0. Quitter
==================================================
```

### Catalogue de la bibliothèque disponible pour l'emprunt :
- `[1]` **Les Misérables** - Victor Hugo (1862)
- `[2]` **1984** - George Orwell (1949)
- `[3]` **Le Petit Prince** - Antoine de Saint-Exupéry (1943)
- `[4]` **La Peste** - Albert Camus (1947)
- `[5]` **Don Quichotte** - Miguel de Cervantes (1605)
- `[6]` **Crime et Châtiment** - Fiodor Dostoïevski (1866)
- `[7]` **L'Étranger** - Albert Camus (1942)

