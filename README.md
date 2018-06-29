# TinyReddit by **Alexandre Goux & Martin Levrard**
Dans le cadre de notre module "Développement d'applications sur le CLOUD (X2IP070)" nous devions réaliser une Google App Engine similaire à ce que l'on peut retrouver sur le site Reddit.

## Features
Voici la liste des fonctionnalités de notre application :

* Poster un message;
* Consulter les derniers messages postés;
* Consulter les messages les plus "HOT";
* Consulter ses messages postés;
* Consulter les messages pour lesquels ont a voté.

## Résultats
Nous devions aussi réalisé une série de mesure sur notre API REST:

**Temps de poste d'un message**

| Nb Voters     |     100         |     1000       |  5000          |
| :------------ | :-------------: | :------------: | :------------: |
| Moyenne       |    299      |        631   |        821 |

**Temps d'extraction de "mes votes" avec 10 messages**

| Nb Voters     |     100         |     1000       |  5000          |
| :------------ | :-------------: | :------------: | :------------: |
| Moyenne       |     813         |        912 |        980  |

**Temps d'extraction de "mes votes" avec 50 messages** 

| Nb Voters     |     100         |     1000       |  5000          |
| :------------ | :-------------: | :------------: | :------------: |
| Moyenne       |     892        |        933  |        987  |

**Temps d'extraction de "mes votes" avec 100 messages**

| Nb Voters     |     100         |     1000       |  5000          |
| :------------ | :-------------: | :------------: | :------------: |
| Moyenne       |     911      |        933  |       991 |

Ces résultats sont cohérents avec le principe dû datastore de Google vu en cours. En effet, nous savons que le datastore scale donc les temps d'exécution restent semblables mêmes avec un plus grand nombre de ressources.