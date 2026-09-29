package fr.fms.dao;

import java.util.List;

/*
interface : ce n'est pas une classe qui fait quelque chose, c'est un contrat. Elle liste des méthodes sans les écrire. 
Toute classe qui signe ce contrat est obligée de les écrire
T est une case vide, remplie par chaque DAO : Dao<Session>, Dao<User>...
 */

public interface Dao<T> { 
   

    /** Renvoie l'objet dont l'identifiant est id, ou null s'il n'existe pas. */
    T read(int id);

    /** Renvoie tous les objets de la table. */
    List<T> readAll();
}