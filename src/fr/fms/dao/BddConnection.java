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
     //static : cette case appartient à la classe elle-même, et non à un objet
        //y en a qu une seule partagée par tout le monde pour tout le programme
        //Connection : le type de ce qu'on y range, une connexion à une base de données
        //au démarrage, la case est vide

    private BddConnection() { }
    //le constructeur privé vide
    //Résultat : personne, ailleurs dans le code, ne peut écrire new BddConnection()
    //Pour être sûr qu'on ne fabrique jamais plusieurs objets BddConnection
    //C'est le principe du Singleton : un objet qui n'existe qu'en un seul exemplaire

    public static Connection getConnection() {
       
        if (connection == null) { //C'est ce if qui garantit qu'on n'ouvre la connexion qu'une seule fois
            try (InputStream input = new FileInputStream("resources/config.properties")) {
                //ouvre le fichier de config, en partant de la racine du projet
                Properties props = new Properties(); //crée un « dictionnaire » vide, props
                props.load(input); //load lit le fichier et le remplit, ligne par ligne
                connection = DriverManager.getConnection( //reçoit ces trois informations, et ouvre la connexion grâce au pilote JDBC
                    //connexion = : range la connexion ouverte dans la case static
                        props.getProperty("db.url"),
                        props.getProperty("db.login"),
                        props.getProperty("db.password"));
            } catch (IOException | SQLException e) {
                System.out.println("Connexion à la base impossible : " + e.getMessage());
            }
        }
        return connection; //On renvoie la connexion à celui qui l'a demandée
    }
}

//try-with-resources : le fichier sera refermé automatiquement à la fin, même en cas d'erreur
