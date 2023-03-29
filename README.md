# DactyloGame

Projet de Compléments de POO L3 de l'Université de Paris 2022/2023



**DactyloGame** un jeu pour apprendre à taper au clavier et s’améliorer en se mesurant à d’autres joueurs.

Les mots à taper sont des mots issus du dictionnaire français *(sans les accents, par exemple le mot "été" devient "ete")* dont le nombre de lettres varie entre 3 et 10.<br>

****&#x26A0;**** Les mots peuvent être vulgaires.

## Utilisation du programme

### Client

- Compilation avec la commande `./gradlew :client:build`
  - Les fichiers compilés se trouvent dans le dossier **client/build/libs**
  - Pour lancer avec le jar:
    - Double cliquer sur `DactyloGame-Client.jar`
    - Ouvrir un terminal dans le dossier **client/build/libs** et taper la commande `java -jar DactyloGame-Client.jar`
- Lancement avec la commande `./gradlew :client:run`
- Génération de la documentation avec la commande `./gradlew :client:javadoc`
  - La documentation se trouve dans le fichier **client/build/docs/javadoc/index.html**
- Lancement des tests avec la commande `./gradlew :client:test`

### Serveur

- Compilation avec la commande `./gradlew :server:build`
  - Les fichiers compilés se trouvent dans le dossier **client/build/libs**
  - Pour lancer avec le jar: ouvrir un terminal dans le dossier **server/build/libs** et taper la commande `java -jar DactyloGame-Server.jar [args]`
    - Exemple: `java -jar DactyloGame-Server.jar -port 4000 -pnb 10`
- Génération de la documentation avec la commande `./gradlew :server:javadoc`
  - La documentation se trouve dans le fichier **server/build/docs/javadoc/index.html**
- Lancement avec la commande `./gradlew :server:run`
- Lancement avec les arguments `./gradlew :server:run --args='[args]'`
  - Exemple de lancement avec les arguments: `./gradlew :server:run --args='-port 4000 -pnb 10'`


| Obligatoire |  Nom  | Argument obligatoire | Valeur par défaut |                    Description                    |
| :---------: | :---: | :-------------------: | :---------------: | :------------------------------------------------: |
|  &#x274E;  | -src |         file         |    dictionaire    | Source des mots, file chemin vers un fichier texte |
|  &#x274E;  | -pnb |          int          |         2         |             Nombre de joueurs maximum             |
|  &#x274E;  | -life |          int          |        25        |                   Nombre de vie                   |
|  &#x274E;  | -port |          int          |       24165       |             Changer le port du serveur             |

## Diagramme de classe

[UML_Server.pdf](UML_Server.pdf?t=1673102309957)


[UML_Client.pdf](UML_Client.pdf?t=1673102303851)

## Mode de jeu

### Solo

Une session termine lorsque le joueur n'a plus de vie. Le joueur perd autant de vie qu'il y a d'erreurs dans le mot validé. Le joueur à la possibilité de gagné des vies grâce à des mots bonus à tapés correctement du premier coup.<br>
Pour chaque mot, le joueur possède un certain temps pour l'écrire. Si le temps est écoulé, un nouveau mot est ajouté à la liste et la validation du mot est forcée. Si la liste est pleine, on passe au prochain mot.

### Entrainement

Pour terminer une session, le joueur doit correctement écrire un certain nombre de mots. Il n'y a pas de vie ni de temps dans ce mode, seul le nombre de mots compte.

### Multijoueur

À l'exception des bonus et du temps, les règles sont les mêmes que pour le mode solo.

Dans ce mode, les bonus sont des mots à ajoutés à la liste de l'adversaire.

Pour ne pas avoir à relancer le serveur, à la fin d'une partie multijoueur, une nouvelle est lancée en attente.

## Menu

### Solo

Lance une partie solo.

### Entrainement

Lance une partie d'entrainement.

### Multijoueur

Lance une partie multijoueur.

### Statisques

Affiche les statistiques du joueur pour le mode solo.

### Paramètres

Permet de modifier les paramètres du jeu.

- Nombre de vie
- Nombre de mots pour augmenter d'un niveau
- Nombre de mots pour terminer une session d'entrainement
- Temps pour taper un mot
- Changer le pseudo du joueur
- Réinitialiser le profil du joueur

## Securité du profil

Chaque profil un UUID est unique, il est généré à la création du profil.
Lors de la creation du profil:

- Attribution d'un UUID unique au profil.
- Création du pcUUID pour l'ordinateur avec `Le nom du system d'exploitation # Le nom de l'utilisateur # Le nom de l'ordinateur # Le chemin vers le dossier home`

À chaque lancement du jeu, on vérifie que le pcUUID contenu dans le profil est le même que celui généré au moment de lancement. Si ce n'est pas le cas, on renitialise le profil.<br>
Le fichier *(server.dg)* contient toutes les informations sur le joueur, il est stocker dans le dossier **home/.DactyloClient**, ce qui permet de le lance le jeu depuis n'importe quel dossier.
Ce fichier est chiffre avec une cle.