package Whitelist_Je.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class dbConnection {
    private dbCredentials dbCredentials;
    private Connection connection;

    public dbConnection(dbCredentials dbCredentials) {
        this.dbCredentials = dbCredentials;
        this.connect();
    }

    private void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(this.dbCredentials.toURI(), this.dbCredentials.getUser(), this.dbCredentials.getPass());
            Logger.getLogger("Minecraft").info("Connecté à la base de donnée !");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            Logger.getLogger("Minecraft").warning("Connexion impossible à la base de donnée !");
        }

    }

    public void close() throws SQLException {
        if(this.connection != null ) {
            if(!this.connection.isClosed() ) {
                this.connection.close();
            }
        }
    }

    public Connection getConnection() throws SQLException {
        if(this.connection != null ) {
            if(!this.connection.isClosed()) {
                return this.connection;
            }
        }

        connect();
        return this.connection;
    }
}
