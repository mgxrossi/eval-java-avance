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
            "SELECT s.id_session, s.start_date, s.available_seats, "
            + "f.id_formation, f.name, f.description, f.mode, f.duration_days, f.price "
            + "FROM session s JOIN formation f ON s.id_formation = f.id_formation";

    private Connection connection = BddConnection.getConnection();
    //Le DAO récupère la connexion unique à la base, fournie par BddConnection. Il ne l'ouvre pas lui-même

    @Override
    //@Override signale que cette méthode est celle promise par le contrat Dao. Elle reçoit un numéro et renvoie une Session
    public Session read(int id) {
        String sql = SELECT_SESSIONS + " WHERE s.id_session = ?";
        //On prend la requête de base et on ajoute une condition : « seulement la session qui a ce numéro ». Le ? est une case vide, qu'on remplira juste après
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            //PreparedStatement : la requête préparée, prête à être envoyée.
            //ps.setInt(1, id) : « mets le nombre id dans le premier ? »
            //avec ? la valeur est toujours traitée comme une simple donnée, jamais comme du code pour eviter que l'utilisateur tape du sql malveillant
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return toSession(rs);
            //executeQuery() envoie la requête et renvoie le résultat, un ResultSet. 
            //comme un tableau de lignes, avec un curseur placé avant la première ligne et 
            //rs.next() avance le curseur d'une ligne. Il renvoie true s'il a trouvé une ligne, false sinon
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
        List<Session> sessions = new ArrayList<>(); //On crée une liste vide, qu'on va remplir
        try (PreparedStatement ps = connection.prepareStatement(SELECT_SESSIONS);
             ResultSet rs = ps.executeQuery()) {
                //La requête de base, sans WHERE, puisqu'on veut tout
            while (rs.next()) {
                sessions.add(toSession(rs));
                //while au lieu de if : tant qu'il reste des lignes, on transforme chacune en Session et on l'ajoute à la liste
            }
        } catch (SQLException e) {
            System.out.println("Erreur de lecture des sessions : " + e.getMessage());
        } //En cas d'erreur, readAll renvoie une liste vide et non null, pour éviter une erreur plus loin dans le programme
        return sessions;
    }

    /**
     * Renvoie les sessions dont le nom ou la description de la formation contient le mot-clé.
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
            //%git% par ex signifie donc « contient git, n'importe où dans le texte »
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            //que ce soit dans le premier pour le nom, ou le second pour la description
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sessions.add(toSession(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur de recherche : " + e.getMessage());
        }
        return sessions;
    }

    /** Transforme une ligne de résultat SQL en objet Session (avec sa Formation) */
    private Session toSession(ResultSet rs) throws SQLException {
        //si la lecture d'une colonne échoue, l'erreur remonte à la méthode qui l'a appelée, où elle sera attrapée par son catch
        Formation formation = new Formation(
                rs.getInt("id_formation"),
                rs.getString("name"),
                rs.getString("description"),
                Mode.valueOf(rs.getString("mode")),
                rs.getInt("duration_days"),
                rs.getDouble("price"));
                //rs.getInt("...") lit un nombre entier, rs.getString("...") un texte, rs.getDouble("...") un nombre à virgule, etc
                //Mode.valueOf("PRESENTIEL") transforme le texte lu en base en valeur de l'enum Mode
        return new Session(
                rs.getInt("id_session"),
                formation,
                rs.getDate("start_date").toLocalDate(),
                rs.getInt("available_seats"));
        //Puis on fabrique la session, en lui donnant la formation qu'on vient de créer
    }
}