#language: fr
@RESERVATION
Fonctionnalité: Réserver des places sur un train de la compagnie

  En tant que client de la compagnie ferroviaire
  Je souhaite réserver des places de train
  Afin de voyager avec des proches vers la destination de mon choix

  Contraintes métier:
  - Tous les sièges d'une réservation doivent être dans la même voiture

  - Un train dans son ensemble ne doit pas dépasser 70% de sa capacité.
  - Éviter de dépasser 70% de réservation par voiture.
  N.B:
  La cohabitation de ces deux contraintes métier peut amener à dépasser les 70 % pour certaines voitures.
  Le système doit équilibrer les réservations entre les voitures.

  Règle: Tous les sièges d'une même réservation doivent être placés dans la même voiture
    Scénario: Le client réserve des places sur un train vide
      Étant donné le train express_2000
        | Voiture | Sièges          |
        | A       | O-O-O-O-O-O-O-O |
        | B       | O-O-O-O-O-O-O-O |
      Quand le client réserve 4 sièges sur le train express_2000
      Alors une référence de réservation est affectée au client
      Et les places de la réservation sur le train express_2000 appartiennent toutes à la même voiture

    Scénario: Le client réserve des places sur un train partiellement occupé
      Étant donné le train local_1000
        | Voiture | Sièges          |
        | A       | X-X-O-O         |
        | B       | O-O-O-O-O-O-O-O |
        | C       | X-X-O-O         |
      Quand le client réserve 3 sièges sur le train local_1000
      Alors une référence de réservation est affectée au client
      Et les places de la réservation sur le train local_1000 sont
        | Sièges |
        | 1B     |
        | 2B     |
        | 3B     |

  Règle: Si possible, une voiture ne doit pas dépasser 70% de sa capacité

    Scénario: Le nombre de sièges demandé ne peut être réservé que dans une seule voiture du train
      Étant donné le train local_1000
        | Voiture | Sièges          |
        | A       | O-O-O-O         |
        | B       | O-O-O-O-O-O-O-O |
        | C       | O-O-O-O         |
      Quand le client réserve 5 sièges sur le train local_1000
      Alors une référence de réservation est affectée au client
      Et les places de la réservation sur le train local_1000 sont
        | Sièges |
        | 1B     |
        | 2B     |
        | 3B     |
        | 4B     |
        | 5B     |

    Scénario: Les sièges demandés ne peuvent être réservés que dans une seule voiture du train sans dépasser le seuil maximum de réservation
      Étant donné le train local_1000
        | Voiture | Sièges          |
        | A       | X-O-O-O         |
        | B       | X-X-X-X-O-O-O-O |
        | C       | X-O-O-O         |
      Quand le client réserve 3 sièges sur le train local_1000
      Alors une référence de réservation est affectée au client
      Et les places de la réservation sur le train local_1000 sont
        | Sièges |
        | 5B     |
        | 6B     |
        | 7B     |

    Scénario: Le train local_1000 n'a pas la capacité d'accueillir le nombre de voyageurs demandé
      Étant donné le train local_1000
        | Voiture | Sièges          |
        | A       | X-X-0-O         |
        | B       | X-X-X-X-X-O-O-O |
        | C       | X-X-O-O         |
      Quand le client réserve 3 sièges sur le train local_1000
      Alors aucune référence de réservation n'est affectée au client
      Et aucun siège n'a été réservé sur le train local_1000

  Règle: Pour un train dans son ensemble, pas plus de 70 % des sièges ne peuvent être réservés

    Scénario: Une voiture dépasse 70% de sa capacité mais pas le train
      Étant donné le train express_2000
        | Voiture | Sièges          |
        | A       | X-X-X-O-O-O-O-O |
        | B       | X-X-O-O-O-O-O-O |
      Quand le client réserve 5 sièges sur le train express_2000
      Alors une référence de réservation est affectée au client
      Et les places de la réservation sur le train express_2000 sont
        | Sièges |
        | 3B     |
        | 4B     |
        | 5B     |
        | 6B     |
        | 7B     |