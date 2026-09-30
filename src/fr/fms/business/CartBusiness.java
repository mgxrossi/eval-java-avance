package fr.fms.business;

import fr.fms.entities.Cart;
import fr.fms.entities.Session;

/**
 * Ce que l'application sait faire avec le panier.
 */
public interface CartBusiness { //On déclare un contrat : la liste de ce qu'on peut faire avec le panier. Le mot interface signifie qu'il n'y aura aucun code ici

    /**
     * Ajouter une session au panier
     * @throws CartException si la session est complète ou déjà dans le panier, 
     * en fait c est un avertissement qui dit cet ajout peut échouer, et dans ce cas, une alerte CartException sera lancée
     * il faut un try catch pour quiquonque appelle cette méthode
     */
    void addToCart(Session session) throws CartException; 

    /** Retirer une session du panier */
    void removeFromCart(Session session);

    /** Renvoie le panier actuel */
    Cart getCart();
}