package fr.fms.entities;

/**
 * Mode d'une formation : en présentiel ou à distance.
 * Pas une classe ordinaire
 */
public enum Mode { //Avec class, on peut fabriquer autant d'objets qu'on veut avec new
                   //Avec enum, la liste des valeurs est fixée une fois pour toutes. Il n'existe que deux modes. On ne peut même pas faire new Mode() : Java refuse
    PRESENTIEL,
    DISTANCIEL
}