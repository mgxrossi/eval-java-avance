package fr.fms.business;

/**
 * tout type public doit être dans son propre fichier
 * Elle est utilisée par plusieurs fichiers.
 * c'est l'héritage. CartException est une sorte d'Exception donc récupère automatiquement tout ce que sait faire une exception Java
 * Erreur métier levée quand une session ne peut pas être ajoutée au panier.
 */
public class CartException extends Exception { //je crée ma propre sorte d'erreur avec un nom clair qui indique d ou vient l erreur
    public CartException(String message) { //constructeur : il porte le même nom que la classe, et il est appelé à chaque new CartException(...)
                                 //il transmet le mesage a Exception
        super(message); //super veut dire appelle le constructeur de la classe parente
    }
}

//classe CartException : elle n'a aucun attribut. Il n'y a pas de case message dedans. Alors où est rangé le texte « Cette session est complète… »?
//Exception, écrite par les créateurs de Java, possède déjà : une case cachée pour ranger un message ;
// et une méthode getMessage() pour le relire.