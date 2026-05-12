Kata java _Train Reservation_ d'Emily BACHE disponible [ici](https://github.com/emilybache/KataTrainReservation).
Le kata a été retravaillé pour intégrer des spécifications cucumber en combinaison avec Spring Boot.

Les instructions initiales ont été traduites et retranscrites ci-après pour les besoins de l'exercice.

# Kata : Réservation de train

Une compagnie ferroviaire souhaite améliorer son service de réservation en ligne.

Ils aimeraient non seulement pouvoir vendre des billets en ligne, mais aussi décider exactement quels sièges doivent
être réservés au moment de la réservation.

Vous travaillez sur le service « TicketOffice » et votre prochaine tâche consiste à mettre en place la fonctionnalité
permettant de réserver des places dans un train particulier.

L'opérateur ferroviaire dispose d'une architecture orientée services, et l'interface dont vous aurez besoin ainsi que
certains services que vous devrez utiliser sont déjà mis en place.

## Objectifs

Définir un service REST devant répondre aux contraintes suivantes :

Le service de billetterie doit répondre à une requête HTTP POST contenant :

- le train sur lequel le client souhaite réserver des sièges
- le nombre de sièges souhaités

La réponse du service doit renvoyer un document json détaillant la réservation effectuée avec trois champs :

- l'identifiant du train
- la référence de la réservation
- les identifiants des sièges qui ont été réservés

Exemple json de sièges réservés sur le train 'express_2000' :

```json
{
  "train_id": "express_2000",
  "booking_reference": "75bcd15",
  "seats": [
    "1A",
    "1B"
  ]
}
```

S'il n'est pas possible de trouver des sièges appropriés à réserver, le service renvoie

- une liste de sièges vide
- une chaîne vide pour la référence de réservation

Exemple json de sièges non disponible pour le train 'express_2000' :

```json
{
  "train_id": "express_2000",
  "booking_reference": "",
  "seats": []
}
```

## Règles métiers relatives aux réservations

Il existe diverses règles métiers et politiques relatives aux sièges pouvant être réservés :

- Pour un train dans son ensemble, pas plus de 70 % des sièges ne doivent être réservés
- Aucun wagon ne devrait avoir plus de 70 % de sièges réservés cependant :
    - Tous les sièges d'une même réservation doivent être placés dans le même wagon.
      → Cela peut vous amener à dépasser les 70 % pour certains wagons, mais veillez à respecter les 70 % pour
      l'ensemble du train.

## Dépendances - Services en place

Vous aurez besoin d'utiliser deux services REST existants.

À des fins de test, chacun des services sont accessibles en local sur les ports 8081 et 8082 pour le profil Spring
_local_.

Des services Python sont démarrés pour simuler la génération de référence de réservation et les données des trains avant
et après chaque réservation.

Les services réels se comporteront de la même manière, mais seront disponibles sur des URL différentes.

### Service de référence de réservation

Pour affecter une référence de réservation unique à un client, un service REST a été mis en place.

#### [Client du service de référence de réservation](src/main/java/fr/shodo/lille/train_reservation/api/BookingReferenceClient.java)

```java

@HttpExchange
public interface BookingReferenceClient {
    @GetExchange("/booking_reference")
    String getBookingReference();
}
```

Une requête GET à l'adresse suivante :

```
    http://localhost:8082/booking_reference
```

Renverra une chaîne de caractères qui ressemble à ceci :

```
    75bcd15
```

### Service de données sur les trains

Vous pouvez obtenir des informations sur chaque train en utilisant ce service.
Les informations superflues (la destination et le point de départ du train, l'heure de départ, la présence d'une
voiture-restaurant, etc.) ont été volontairement omis.

Les sièges existants dans le train et leur disponibilité sont les seules informations qui nous intéressent.

Une place est disponible si le champ « booking_reference » contient une chaîne vide.

#### [Client du référentiel de trains](src/main/java/fr/shodo/lille/train_reservation/api/TrainDataServiceClient.java)

```java

@HttpExchange(
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE)
public interface TrainDataServiceClient {

    @GetExchange(value = "/data_for_train/{trainId}")
    ResponseEntity<String> dataForTrain(@PathVariable("trainId") String trainId);

    @PostExchange(url = "/reserve")
    void reserveSeats(
            @RequestParam("train_id") String trainId,
            @RequestParam("seats") String seatsJson,
            @RequestParam("booking_reference") String bookingReference
    );

    @PostExchange("/reset/{trainId}")
    void resetTrain(@PathVariable("trainId") String trainId);
}
```

Pour obtenir des données sur le train portant l'identifiant « express_2000 » par exemple,
effectuez une requête GET à l'adresse suivante :

```
    http://localhost:8081/data_for_train/express_2000
```

Cette requête renverra un document JSON contenant des informations sur les places disponibles dans ce train.
Le document obtenu ressemblera par exemple à ceci :

```json
{
  "seats": {
    "1A": {
      "booking_reference": "",
      "seat_number": "1",
      "coach": "A"
    },
    "2A": {
      "booking_reference": "75bcd15",
      "seat_number": "2",
      "coach": "A"
    }
  }
}
```

Pour réserver des places dans un train, vous devez envoyer une requête POST à cette URL :

```
    http://localhost:8081/reserve
```

Les données de la requête indiquant les sièges à réserver doit contenir les champs :

```
    "train_id", "seats", "booking_reference"
```

Le champ « seats » doit être une liste encodée en JSON d'identifiants de sièges, par exemple :

```
    ["1A", "2A"]
```

Les deux autres champs sont des chaînes de caractères ordinaires.

Notez que le serveur vous empêchera de réserver si la référence de réservation a déjà été affectée à un autre siège.

Il est possible de supprimer toutes les réservations sur un train donné via la requête suivante :

```
    http://localhost:8081/reset/express_2000
```

Utilisez-la avec précaution.