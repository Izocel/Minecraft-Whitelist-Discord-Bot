package Whitelist_Je.mysql;

import java.sql.SQLException;

public class DatabaseManager {
    private dbConnection userinfo;

    public DatabaseManager() {
        this.userinfo = new dbConnection(new dbCredentials(
                "127.0.0.1",
                "whitelist_je",
                "@whitelist_je2022",
                "whitelist_je", 3306));
    }

    public dbConnection getUserinfo() {
        return userinfo;
    }

    public void close() {
        try {
            this.userinfo.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
