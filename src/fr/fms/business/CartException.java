package fr.fms.business;

/**
 * Erreur métier levée quand une session ne peut pas être ajoutée au panier.
 */
public class CartException extends Exception { //je crée ma propre sorte d'erreur avec un nom clair qui indique d ou vient l erreur
                           //Elle hérite de la class Exception de Java donc de tout ce que sait faire une exception Java naturellement : être lancée avec throw, être attrapée avec catch, garder un message
    public CartException(String message) { //l'espace vide où on écrit le texte de l'alerte
        super(message); //super veut simplement dire « la partie héritée d'Exception
    }
}