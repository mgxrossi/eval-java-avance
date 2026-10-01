package fr.fms.business;

import fr.fms.entities.Cart;
import fr.fms.entities.Session;

/**
 * Règles du panier : pas de session complète, pas de doublon.
 */
public class CartBusinessImpl implements CartBusiness { //je signe le contrat CartBusiness donc il faut les 3 methodes promises

    private Cart cart = new Cart(); //panier vide au départ
                               //private car personne ne peut y toucher direct il faut passer par les methodes en dessous
                               //un seul panier pendant toute l'utilisation du programme

    @Override
    public void addToCart(Session session) throws CartException { //ajouter en verifiant les regles
                                      //@Override signale que c'est la méthode promise par le contrat. 
                                      // On y retrouve le throws CartException annoncé dans l'interface
                                      //elle ne renvoie rien elle ajoute juste
      if (session.isFull()) { //Premier contrôle : la session est-elle complète ? on demande a la session elle meme, si oui throw arrete immediatement la methode et message d alerte
            throw new CartException("Cette session est complète, elle ne peut pas être ajoutée au panier.");
        }
        if (cart.contains(session)) { //est-elle déjà dans le panier ? pareil
            throw new CartException("Cette formation est déjà dans votre panier.");
        }
        cart.add(session); //Si on arrive jusqu'ici, c'est que les deux contrôles sont passés. On ajoute donc la session au panier
    }

    @Override
    public void removeFromCart(Session session) {
        cart.remove(session);
    }
    //on demande simplement au panier de retirer la session

    @Override
    public Cart getCart() { //On renvoie le panier, pour que le menu puisse l'afficher avec son total
        return cart;
    }
    //renvoie le panier, pour que le menu puisse l'afficher, avec son total
}
