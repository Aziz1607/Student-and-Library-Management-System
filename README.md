# Projet CORBA (Java IDL) - Gestion d'Institut & Bibliothèque

Projet d'architecture distribuée basé sur **CORBA** (Common Object Request Broker Architecture) et **Java IDL**.
Il permet la gestion à distance d'une promotion d'étudiants, le calcul des moyennes pondérées par épreuve, ainsi que l'emprunt de livres au sein d'une bibliothèque universitaire.

---

## 📋 Table des matières

1. [Architecture & Vue d'ensemble](#-architecture--vue-densemble)
2. [Structure du projet](#-structure-du-projet)
3. [Prérequis importants (Java 8)](#-prérequis-importants-java-8)
4. [Guide de compilation et d'exécution](#-guide-de-compilation-et-dexécution)
5. [Fonctionnalités du Client](#-fonctionnalités-du-client)

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

