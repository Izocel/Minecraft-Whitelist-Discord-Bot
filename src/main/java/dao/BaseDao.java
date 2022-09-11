package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import mysql.DbCredentials;
import configs.ConfigManager;
import models.User;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class BaseDao {

    static private ConfigManager Configs = new ConfigManager();
    protected Connection connection;
    protected DbCredentials creds;
    protected String tablename;
    private Logger logger;

    public BaseDao() {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getName());
        this.creds = this.getCredentials();
    }

    protected DbCredentials getCredentials() {
        return new DbCredentials(
                Configs.get("mysqlHost", "127.0.0.1"),
                Configs.get("mysqlUser", "root"),
                Configs.get("mysqlPass", "mysql"),
                Configs.get("mysqlDb", "whitelist_je"),
                Integer.parseInt(Configs.get("mysqlPort", "3306")));
    }

    protected Connection open() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            this.connection = DriverManager.getConnection(
                    this.creds.toURI(),
                    this.creds.getUser(),
                    this.creds.getPass());

            this.logger.info("Connexion à la base de donnée !");

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            this.logger.warning("Connexion impossible à la base de donnée !");
        }
        return this.connection;

    }

    protected void close() throws SQLException {
        try {
            this.connection.close();
            this.logger.info("Déconnexion à la base de donnée !");

        } catch (SQLException e) {
            e.printStackTrace();
            this.logger.warning("Déconnexion impossible à la base de donnée !");
        }
    }

    public JSONArray toJsonArray(ResultSet resultSet) {

        JSONArray results = new JSONArray();

        try {
            ResultSetMetaData md = resultSet.getMetaData();

            int numCols = md.getColumnCount();
            List<String> colNames = IntStream.range(0, numCols)
                    .mapToObj(i -> {
                        try {
                            return md.getColumnName(i + 1);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            return "?";
                        }
                    })
                    .collect(Collectors.toList());

            while (resultSet.next()) {
                JSONObject row = new JSONObject();
                colNames.forEach(cn -> {
                    try {
                        row.put(cn, resultSet.getObject(cn));
                    } catch (JSONException | SQLException e) {
                        e.printStackTrace();
                    }
                });
                results.put(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    public User find(Integer id) {

        JSONArray results = new JSONArray();

        try {
            String sql = "SELECT * FROM " + this.tablename + " WHERE id = ?;";
            this.open();
            final PreparedStatement pstmt = this.connection.prepareStatement(sql);
            pstmt.setInt(1, id);

            final ResultSet resultSet = pstmt.getResultSet();
            results = this.toJsonArray(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            this.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return (User) results.get(0);
    }

    public JSONArray findAll() {
        JSONArray results = new JSONArray();

        try {
            String sql = "SELECT * FROM " + this.tablename + ";";

            this.open();
            final PreparedStatement pstmt = this.connection.prepareStatement(sql);
            pstmt.executeQuery();

            final ResultSet resultSet = pstmt.getResultSet();
            results = this.toJsonArray(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            this.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }
}
