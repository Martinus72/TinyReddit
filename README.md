# TinyReddit
Dans le cadre de notre module "Développement d'applications sur le CLOUD (X2IP070)" nous devions réaliser une Google App Engine similaire à ce que l'on peut retrouver sur le site Reddit.

## Authors
* **Alexandre Goux** - *Developer* - [alexandre-goux](https://github.com/alexandre-goux)
* **Martin Levrard** - *Developer* - [MartinL](https://github.com/Martinus72)

## Liens utiles

* [Application Google App Engine](https://1-dot-reddit-205206.appspot.com/)
* [Repot Github du projet](https://github.com/Martinus72/TinyReddit)
* [Explorer de l'API REST](https://apis-explorer.appspot.com/apis-explorer/?base=https://1-dot-reddit-205206.appspot.com/_ah/api#p/)

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

Ces résultats sont cohérents avec le principe du datastore de Google vu en cours. En effet, nous savons que le datastore scale donc les temps d'exécution restent semblables mêmes avec un plus grand nombre de ressources.