package fr.fms.dao; //ce fichier appartient à la couche DAO

/*les imports :
les outils Java : ceux de java.sql pour parler à la base, et ceux de java.util pour faire des listes ;
+ mes propres classes de entities */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List; //outil List sert à renvoyer plusieurs objets d'un coup

import fr.fms.entities.Formation;
import fr.fms.entities.Mode;
import fr.fms.entities.Session;


//Lit les sessions en base, avec leur formation
public class SessionDao implements Dao<Session> { //SessionDao signe le contrat Dao, pour des objets Session

    /*En base, une session ne contient pas le nom ni le prix : elle ne contient que le numéro de sa formation (id_formation). 
    Le JOIN colle chaque ligne de session avec la ligne de formation qui a le même numéro. 
    On récupère ainsi tout d'un coup : la date et les places de la session, plus le nom, le mode et le prix de la formation 
    s et f sont des surnoms donnés aux tables (session s, formation f
    */
    private static final String SELECT_SESSIONS =
        //déclare une constante Java, et cette constante contient une requête SQL
            "SELECT s.id_session, s.start_date, s.available_seats, "
            + "f.id_formation, f.name, f.description, f.mode, f.duration_days, f.price "
            + "FROM session s JOIN formation f ON s.id_formation = f.id_formation";
            //SELECT liste les colonnes qu'on veut récupérer. 
            //Le préfixe s. ou f. indique de quelle table vient chacune
            //ON s.id_formation = f.id_formation : chaque session est associée à la formation qui a le même numéro

    private Connection connection = BddConnection.getConnection();
    //Le DAO récupère la connexion unique à la base, fournie par BddConnection. Il ne l'ouvre pas lui-même

    @Override
    //@Override signale que cette méthode est celle promise par le contrat Dao. Elle reçoit un numéro et renvoie une Session
    public Session read(int id) {
        String sql = SELECT_SESSIONS + " WHERE s.id_session = ?";
        //donne-moi les sessions avec leur formation et j' ajoute une précision : seulement celle qui a tel numéro.
        //Le numéro n'est pas encore écrit : à sa place, il y a un ?
        //avec ? la valeur est toujours traitée comme une simple donnée, jamais comme du code pour eviter que l'utilisateur tape du sql
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            //On transforme cette demande en formulaire officiel, prêt à être envoyé à la base
            //le try verifie que tout est ok
            ps.setInt(1, id);
            //ps.setInt(1, id) : dans la premiere case vide écris le num demaqndé
            try (ResultSet rs = ps.executeQuery()) {
                //On envoie le formulaire à la base. Elle répond avec un résultat, rs : une pile de fiches trouvées.
                if (rs.next()) {
                    return toSession(rs);
            //rs.next() avance le curseur d'une ligne. et demande « y a-t-il une fiche dans la pile ?
            //if et pas while, car on attend au maximum une ligne : un numéro de session est unique
            //Si une ligne est trouvée, toSession(rs) la transforme en objet Session
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur de lecture de la session " + id + " : " + e.getMessage());
        } //attrape l'erreur si la base ne répond pas, ou si la requête est fausse
        return null;
        //si rien n'a été trouvé, on renvoie null, qui veut dire « rien ». La couche business saura alors que ce numéro n'existe pas
    }

    @Override
    public List<Session> readAll() {
        List<Session> sessions = new ArrayList<>(); 
        //avant le = : List : le type de la variable, <Session> ce que la liste contient, ici que des sessions, et sessions : le nom de la variable
        // avec le = on range dans la variable ce qu il y a après le =
        // Arraylist c est le type de liste, <> c est <Session>, () : on n'envoie rien au constructeur, donc la liste est créée vide
        //Et si une erreur survient, la liste reste vide mais existe bien. C'est pour ça qu'on la crée en tout premier, avant le try 
        try (PreparedStatement ps = connection.prepareStatement(SELECT_SESSIONS);
             ResultSet rs = ps.executeQuery()) {
                //La requête de base, sans WHERE, puisqu'on veut tout
            while (rs.next()) {
                sessions.add(toSession(rs));
                //while au lieu de if : tant qu'il reste des lignes, on transforme chacune en Session et on l'ajoute à la liste
            }
        } catch (SQLException e) {
            System.out.println("Erreur de lecture des sessions : " + e.getMessage());
        } //En cas d'erreur, readAll renvoie une liste vide et non null
        return sessions;
    }

    /**
     * Renvoie les sessions dont le nom ou la description de la formation contient le mot-clé.
     * On cherche le mot-clé dans le nom et la description de la formation
     * La recherche ignore les majuscules et les accents grâce à la collation de la base.
     */
    public List<Session> readByKeyword(String keyword) {
        //cette méthode ne fait pas partie du contrat Dao. Elle est propre à SessionDao, car tous les DAO n'ont pas besoin d'une recherche par mot-clé
        List<Session> sessions = new ArrayList<>();
        String sql = SELECT_SESSIONS + " WHERE f.name LIKE ? OR f.description LIKE ?";
        //LIKE : « ressemble à ». Il permet de chercher un morceau de texte
        //OR : on cherche dans le nom ou dans la description
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            //%git% par ex signifie n'importe quoi, puis git, puis n'importe quoi donc contient git, n'importe où dans le texte
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            //que ce soit dans le premier pour le nom, ou le second pour la description
            try (ResultSet rs = ps.executeQuery()) { //ps.executeQuery() envoie la requête à la base ; ette réponse est rangée dans la variable rs, de type ResultSet
                while (rs.next()) { //cette fois, il peut y avoir plusieurs résultats. Tant qu'il reste une ligne, on la transforme en Session et on l'ajoute à la liste
                    sessions.add(toSession(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur de recherche : " + e.getMessage());
        } //Si la base ne répond pas, on affiche un message au lieu de planter
        return sessions;
    }

    /** Transforme une ligne de résultat SQL en objet Session (avec sa Formation) */
    private Session toSession(ResultSet rs) throws SQLException {
        //si la lecture d'une colonne échoue, l'erreur remonte à la méthode qui l'a appelée, où elle sera attrapée par son catch
        //toSession est appelée par les trois méthodes de lecture de SessionDao
        Formation formation = new Formation( //nouvelle objet qu'on range dans une variable
                rs.getInt("id_formation"),
                rs.getString("name"),
                rs.getString("description"),
                Mode.valueOf(rs.getString("mode")),
                rs.getInt("duration_days"),
                rs.getDouble("price"));
                //On appelle le constructeur de Formation, en lui donnant ses six valeurs
                //Chaque lecture utilise la bonne méthode selon le type : rs.getInt("...") lit un nombre entier, rs.getString("...") un texte, rs.getDouble("...") un nombre à virgule, etc
                //Mode.valueOf("PRESENTIEL") transforme le texte lu en base en valeur de l'enum Mode car l'attribut de Formation attend une valeur de l'enum Mode
        return new Session(
            //On appelle le constructeur de Session, avec ses quatre valeurs
                rs.getInt("id_session"),
                formation,
                rs.getDate("start_date").toLocalDate(), //conversion car format sql mais l'attribut de Session attend une LocalDate
                rs.getInt("available_seats"));
        //Puis on fabrique la session, en lui donnant la formation qu'on vient de créer
    }
}

//toSession est appelée à l'intérieur du try de read
//Le try délimite une zone surveillée, et le catch est le filet placé sous cette zone. Toute erreur de type SQLException qui survient dans la zone tombe dans ce filet, y compris celles qui viennent d'une méthode appelée depuis cette zone