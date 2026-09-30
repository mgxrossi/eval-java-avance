package fr.fms.business;

import java.util.List;

import fr.fms.entities.Mode;
import fr.fms.entities.Session;

/**
 * Ce que l'application sait faire avec le catalogue (partie 1).
 */
public interface CatalogBusiness {

    /** Toutes les sessions, triées par ordre alphabétique du nom de formation. */
    List<Session> getSessionsSortedByName();

    /** Toutes les sessions, triées de la plus proche à la plus lointaine. */
    List<Session> getSessionsSortedByDate();

    /** Uniquement les sessions du mode demandé (présentiel ou distanciel). */
    List<Session> getSessionsByMode(Mode mode);

    /** Les sessions dont le nom ou la description contient le mot-clé. */
    List<Session> searchByKeyword(String keyword);

    /** Une session précise, pour en afficher le détail (null si introuvable). */
    Session getSession(int id);
}