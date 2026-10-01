package fr.fms.entities; //Le fichier appartient à la couche entités, le panier est une donnée de l'application, comme Session ou Formation

import java.util.ArrayList; //le type « liste », une suite d'éléments rangés dans l'ordre
import java.util.List; //la liste concrète qu'on fabrique réellement

/**
 * Le panier : la liste des sessions choisies par le visiteur.
 * Pour l'instant, il est gardé en mémoire (il n'est pas enregistré en base).
 */
public class Cart {
    private List<Session> sessions = new ArrayList<>(); //une liste qui ne peut contenir que des sessions
                                                           //session le nom de la variable
                                                           //liste vide, un panier neuf ne contient rien
//private cad aucune autre classe ne peut toucher directement à cette liste. Elle doit passer par les méthodes getter ci-dessous

    public List<Session> getSessions() { //getter qui permet au menu de lire la liste pour l'afficher
        return sessions;
    }

    /** Ajoute une session au panier. */
    public void add(Session session) { //void : la méthode ne renvoie rien, elle fait juste une action
                                      //Session session : elle reçoit la session à ajouter
        sessions.add(session);  //sessions.add(session) : on la met à la fin de la liste
        //Cette méthode ne vérifie rien : c'est la couche business qui vérifie les règles avant d'appeler add
    }
    

   
    public void remove(Session session) { 
        sessions.removeIf(s -> s.getId() == session.getId()); //removeIf(...) : « retire de la liste toutes les sessions qui remplissent cette condition »
              //pour chaque session s du panier : a-t-elle le même numéro que la session à retirer ? si oui on retire
    }

    /** Indique si une session (même identifiant) est déjà dans le panier, boolean */
    public boolean contains(Session session) {
        for (Session s : sessions) { //« Pour chaque session s du panier… »
            if (s.getId() == session.getId()) {
                return true;
                //si elle a le même numéro que celle qu'on cherche, réponds oui et return arrête la méthode.
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return sessions.isEmpty();
        //Répond true si le panier ne contient rien. Le menu l'utilise pour afficher « Votre panier est vide »
    }    //travail deja fait par la liste la juste on donne sa rep
         //les listes Java ont déjà une méthode isEmpty(), écrite par les créateurs de Java

    /** Additionne le prix de toutes les sessions du panier. */
    public double getTotal() { //double : la méthode renvoie un nombre à virgule
        double total = 0; //On part de zéro
        for (Session s : sessions) { //pour chaque session du panier on ajoute son prix au total
            total += s.getFormation().getPrice(); //on passe de la session à sa formation, puis on lit son prix
        }
        return total;
    }
}