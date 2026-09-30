package fr.fms.business;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import fr.fms.dao.SessionDao;
import fr.fms.entities.Mode;
import fr.fms.entities.Session;

/**
 * Cette classe signe le contrat CatalogBusiness. » Java vérifie qu'elle écrit bien les cinq méthodes promises
 */
public class CatalogBusinessImpl implements CatalogBusiness {

    private SessionDao sessionDao = new SessionDao();
    //La couche business passe par le DAO pour obtenir les données. Elle ne contient aucune requête SQL.

    @Override
    public List<Session> getSessionsSortedByName() {
        List<Session> sessions = sessionDao.readAll();
        sessions.sort(Comparator.comparing(session -> session.getFormation().getName()));
        return sessions;
/* Trois étapes :
On demande toutes les sessions au DAO.
On les trie, sessions.sort(...) tout ce au il y a entre paranthese c est selon quoi on trie et la c est : « … pour chaque session, le nom de sa formation »
On renvoie la liste triée. 
La flèche -> se lit « donne » donc une session donne le nom de sa formation
Java compare ensuite ces noms deux à deux, par ordre alphabétique
*/
    }
    

    @Override
    public List<Session> getSessionsSortedByDate() {
        List<Session> sessions = sessionDao.readAll();
        sessions.sort(Comparator.comparing(Session::getStartDate)); //raccourcie de session -> session.getStartDate(). On peut l'utiliser quand on appelle directement une méthode de la session, sans passer par sa formation
        return sessions;
    /* Même principe, mais on trie selon la date de début 
    Trier dans la couche business garde le DAO simple avec (une seule méthode readAll)
    */
    }

    @Override
    public List<Session> getSessionsByMode(Mode mode) { //tri sélectif
        //on prepare une liste vide, on parcourt toutes les sessions triees par nom et si le mode est celui demandé on le garde et on renvoie les sessions trouvées
        List<Session> result = new ArrayList<>();
        for (Session session : getSessionsSortedByName()) {
            if (session.getFormation().getMode() == mode) {
                result.add(session);
                //on part de la liste triée par nom ? Pour que le résultat soit bien ordonné, sans refaire de tri. On réutilise une méthode déjà écrite
                //Il n'y a qu'un seul Mode.PRESENTIEL donc on compare directement avec ==. Attention : pour comparer deux textes (String), il faudrait equals, et jamais == mais la c est un ENUM.
            }
        }
        return result;
    }

    @Override
    public List<Session> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        /*
        Avant d'interroger la base, on vérifie que le mot-clé a du sens :
        keyword == null : il n'y a pas de mot du tout ;
        le mot ne contient que des espaces. trim() retire les espaces au début et à la fin, et isEmpty() vérifie s'il reste quelque chose.
        Si l'une des deux conditions est vraie, on renvoie tout de suite une liste vide
        décision sur le comportement de l'application : elle a donc sa place ici, et pas dans le DAO
        */
        return sessionDao.readByKeyword(keyword.trim());
    }

    @Override
    public Session getSession(int id) {
        return sessionDao.read(id); //la couche business transmet la demande au DAO
    }
}