package fr.fms.entities;

/**
 * Mode d'une formation : en présentiel ou à distance.
 * Nouveau fichier car Tout type public (classe, interface ou enum) doit être dans son propre fichier
 * Pas d'attributs, pas de constructeur, pas de getters : il n'y a rien d'autre à stocker qu'un choix entre deux valeurs
 * Java donne automatiquement à chaque enum quelques outils prêts à l'emploi
 * 
 * Mode ne renvoie rien. Il définit les valeurs possibles et c est la méthode getMode qui renvoie PRESENTIEL ou DISTANCIEL
 * Un type dit juste quelle sorte de valeur peut exister
 */
public enum Mode { //Avec classa a l inverse on pourrait fabriquer autant d'objets qu'on veut avec new
                   //Avec enum, la liste des valeurs est fixée une fois pour toutes. Il n'existe que deux modes. On ne peut même pas faire new Mode() : Java refuse
    PRESENTIEL,
    DISTANCIEL
}