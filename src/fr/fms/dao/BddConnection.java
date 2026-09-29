package fr.fms.dao;

import java.io.FileInputStream;  //Ouvre le fichier config.properties
import java.io.IOException; //L'erreur si le fichier est introuvable.
import java.io.InputStream; //Le type de la variable input qui lit ce fichier
import java.sql.Connection; //	Le type de la connexion à la base
import java.sql.DriverManager; //L'outil qui ouvre la connexion avec l'URL, le login et le mot de passe
import java.sql.SQLException; //L'erreur possible si la connexion échoue (base arrêtée, mauvais mot de passe…)
import java.util.Properties; //Comprendre les lignes nom=valeur du fichier (dictionnaire en python)
                      //Pour Java, au départ, ce n'est qu'un bloc de texte. Il ne sait pas que db.login est un nom et formation_app sa valeur
                      // Properties fait ce découpage a chaque ligne au =. Il lit le fichier et range chaque ligne dans une sorte de dictionnaire

/**
 * Connexion unique à la base de données (pattern Singleton).
 * Les paramètres sont lus dans resources/config.properties.
 */
public class BddConnection {
    private static Connection connection = null;

    private BddConnection() { }

    public static Connection getConnection() {
        if (connection == null) {
            try (InputStream input = new FileInputStream("resources/config.properties")) {
                Properties props = new Properties();
                props.load(input);
                connection = DriverManager.getConnection(
                        props.getProperty("db.url"),
                        props.getProperty("db.login"),
                        props.getProperty("db.password"));
            } catch (IOException | SQLException e) {
                System.out.println("Connexion à la base impossible : " + e.getMessage());
            }
        }
        return connection;
    }
}