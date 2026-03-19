#language: fr
@RESERVATION
Fonctionnalité: Réserver des places dans un train de la compagnie

  En tant que client de la compagnie ferroviaire
  Je souhaite réserver des places de train
  Afin de voyager avec des proches vers la destination de mon choix

  Contraintes métier:
  - Tous les sièges d'une réservation doivent être dans la même voiture
  - Un train dans son ensemble ne doit pas dépasser 70% de sa capacité

  N.B:
  La cohabitation de ces deux contraintes métier peut amener à dépasser les 70 % pour certaines voitures.
  Le système doit équilibrer les réservations entre les voitures.

  Contexte:
    Étant donné les trains de la compagnie ferroviaire
      | Identifiant du train |
      | express_2000         |
      | local_1000           |
    Et que le service de réservation est disponible

  Règle: Tous les sièges d'une même réservation doivent être placés dans la même voiture
    Plan du Scénario: Réserver des places dans un train vide
      Quand le client réserve <Nombre de sièges> places sur le train express_2000
      Alors une référence de réservation est affectée au client
      Et les <Nombre de sièges> sièges de la réservation appartiennent à la même voiture du train express_2000
      Exemples:
        | Nombre de sièges |
        | 4                |
        | 8                |

    Scénario: Une réservation est placée dans une voiture partiellement occupée
      Étant donné que 2 sièges sont déjà réservés dans la voiture A du train express_2000
      Quand le client réserve 3 places sur le train express_2000
      Alors une référence de réservation est affectée au client
      Et les 3 sièges de la réservation appartiennent à la même voiture du train express_2000

  Règle: Pour un train dans son ensemble, pas plus de 70 % des sièges ne peuvent être réservés
    Scénario: Un client réserve 4 places sur un train qui a atteint 70% de sa capacité d'accueil
      Lorsque chaque voiture du train local_1000 a 70% de sièges réservés
      Quand le client réserve 4 places sur le train local_1000
      Alors aucune référence de réservation n'est affectée au client
      Et aucun nouveau siège n'a été réservé sur le train local_1000

    Scénario: Placer une réservation sans dépasser le seuil d'un train
      Étant donné que les sièges réservés sur le train express_2000
        | Siège réservé |
        | 1A            |
        | 2A            |
        | 3A            |
        | 1B            |
        | 2B            |
      Quand le client réserve 2 places sur le train express_2000
      Alors une référence de réservation est affectée au client
      Et les 2 sièges de la réservation appartiennent à la voiture B du train express_2000