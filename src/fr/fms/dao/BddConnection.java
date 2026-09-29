package fr.fms.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

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