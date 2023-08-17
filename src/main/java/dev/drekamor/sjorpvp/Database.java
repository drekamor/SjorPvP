package dev.drekamor.sjorpvp;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.drekamor.sjorpvp.config.DatabaseCredentials;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Database {
    private final SjorPvP plugin;
    private DataSource dataSource;
    public Database(SjorPvP plugin, DatabaseCredentials credentials){
        this.plugin = plugin;

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://%s:%s/%s".formatted(credentials.host(), credentials.port(), credentials.database()));
        hikariConfig.setUsername(credentials.user());
        hikariConfig.setMaximumPoolSize(credentials.poolSize());
        hikariConfig.setConnectionTimeout(credentials.connectionTimeout());
        hikariConfig.setIdleTimeout(credentials.idleTimeout());
        hikariConfig.setMaxLifetime(credentials.maxLifetime());
        if(credentials.password() != null){
            hikariConfig.setPassword(credentials.password());
        }
        hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");

        this.dataSource = new HikariDataSource(hikariConfig);
        this.plugin.info("Database configuration is ready");
    }
    private Connection getConnection(){
        try {
            return getDataSource().getConnection();
        } catch (Exception e){
            plugin.severe("Could not Establish connection to the database");
            plugin.severe(Arrays.toString(e.getStackTrace()));
        }
        return null;
    }
    private DataSource getDataSource() throws SQLException {
        if (this.dataSource != null) {
            return this.dataSource;
        }
        throw new SQLException("No data source available");
    }
    public void initializeTables(){
        try {
            Connection connection = this.getConnection();
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `inventories`" +
                    "(uuid VARCHAR(128), name VARCHAR(64) UNIQUE, sic TEXT, sac TEXT);").execute();
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `warps`" +
                    "(uuid VARCHAR(128), name VARCHAR(64) UNIQUE, location TEXT);").execute();
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `player_stats`" +
                    "(uuid VARCHAR(128) UNIQUE, stats TEXT);").execute();
            connection.close();
        } catch (Exception e) {
            plugin.severe("Failed to initialize tables");
            e.printStackTrace();
        }
    }
    public boolean saveInventory(String uuid, String name, String sic, String sac){
        try {
            Connection connection = this.getConnection();
            PreparedStatement statement = connection.prepareStatement("REPLACE INTO inventories(uuid, name, sic, sac) VALUES (?, ?, ?, ?)");
            statement.setString(1, uuid);
            statement.setString(2, name);
            statement.setString(3, sic);
            statement.setString(4, sac);
            statement.execute();
            statement.close();
            connection.close();
            return true;
        } catch (Exception e) {
            plugin.severe("Unable to add inventory %s to database.".formatted(name));
            e.printStackTrace();
            return false;
        }
    }
    public String[] getInventory(String name){
        try {
            Connection connection = this.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM inventories WHERE name=?");
            statement.setString(1, name);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                String sic = result.getString("sic");
                String sac = result.getString("sac");
                statement.close();
                result.close();
                connection.close();

                return new String[]{sic, sac};
            }
        } catch (Exception e) {
            plugin.severe("Unable to get inventory %s from database.".formatted(name));
            e.printStackTrace();
        }
        return null;
    }
    public boolean removeInventory(String name){
        try {
            Connection connection = this.getConnection();
            PreparedStatement statement = connection.prepareStatement("DELETE FROM inventories WHERE name=?");
            statement.setString(1, name);
            statement.execute();
            statement.close();
            connection.close();
            return true;
        } catch (Exception exception) {
            plugin.severe("Unable to remove inventory %s.".formatted(name));
            exception.printStackTrace();
            return false;
        }
    }
    public int countInventories(String uuid){
        try {
            Connection connection = this.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM inventories WHERE uuid=?");
            statement.setString(1, uuid);
            ResultSet result = statement.executeQuery();
            if(result.next()){
                int count = result.getInt(1);
                statement.close();
                result.close();
                connection.close();
                return count;
            }
        } catch (Exception e) {
            plugin.severe("Unable to get inventory count.");
            e.printStackTrace();
        }
        return 0;
    }
    public List<String> getInventoryNames(){
        try {
            Connection connection = this.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT name FROM inventories");
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                List<String> names = new ArrayList<>();
                names.add(result.getString("name"));
                while(result.next()){
                    names.add(result.getString("name"));
                }
                statement.close();
                result.close();
                connection.close();
                return names;
            }
        } catch (Exception e) {
            plugin.severe("Unable to get the inventory list.");
            e.printStackTrace();
        }
        return null;
    }
    public boolean saveWarp(String uuid, String name, String location){
        try {
            Connection connection = this.getConnection();
            PreparedStatement statement = connection.prepareStatement("REPLACE INTO warps(uuid, name, location) VALUES (?, ?, ?)");
            statement.setString(1, uuid);
            statement.setString(2, name);
            statement.setString(3, location);
            statement.execute();
            statement.close();
            connection.close();
            return true;
        } catch (Exception e) {
            plugin.severe("Unable to add warp %s to database.".formatted(name));
            e.printStackTrace();
            return false;
        }
    }
    public String getWarp(String name){
        try {
            Connection connection = this.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM warps WHERE name=?");
            statement.setString(1, name);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                String location = result.getString("location");
                statement.close();
                result.close();
                connection.close();

                return location;
            }
        } catch (Exception e) {
            plugin.severe("Unable to get warp %s from database.".formatted(name));
            e.printStackTrace();
        }
        return null;
    }
    public boolean removeWarp(String name){
        try {
            Connection connection = this.getConnection();
            PreparedStatement statement = connection.prepareStatement("DELETE FROM warps WHERE name=?");
            statement.setString(1, name);
            statement.execute();
            statement.close();
            connection.close();
            return true;
        } catch (Exception exception) {
            plugin.severe("Unable to remove warp %s.".formatted(name));
            exception.printStackTrace();
            return false;
        }
    }
    public int countWarps(String uuid){
        try {
            Connection connection = this.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM warps WHERE uuid=?");
            statement.setString(1, uuid);
            ResultSet result = statement.executeQuery();
            if(result.next()){
                int count = result.getInt(1);
                statement.close();
                result.close();
                connection.close();
                return count;
            }
        } catch (Exception e) {
            plugin.severe("Unable to get warp count.");
            e.printStackTrace();
        }
        return 0;
    }
    public List<String> getWarpNames(){
        try {
            Connection connection = this.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT name FROM warps");
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                List<String> names = new ArrayList<>();
                names.add(result.getString("name"));
                while(result.next()){
                    names.add(result.getString("name"));
                }
                statement.close();
                result.close();
                connection.close();
                return names;
            }
        } catch (Exception e) {
            plugin.severe("Unable to get the warps list.");
            e.printStackTrace();
        }
        return null;
    }
    public boolean savePlayerStats(String uuid, String stats){
        try {
            Connection connection = this.getConnection();
            PreparedStatement statement = connection.prepareStatement("REPLACE INTO player_stats(uuid, stats) VALUES (?, ?)");
            statement.setString(1, uuid);
            statement.setString(2, stats);
            statement.execute();
            statement.close();
            connection.close();
            return true;
        } catch (Exception e) {
            plugin.severe("Unable to save stats for %s to database.".formatted(uuid));
            e.printStackTrace();
            return false;
        }
    }
    public String getPlayerStats(String uuid){
        try {
            Connection connection = this.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM player_stats WHERE uuid=?");
            statement.setString(1, uuid);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                String stats = result.getString("stats");
                statement.close();
                result.close();
                connection.close();

                return stats;
            }
        } catch (Exception e) {
            plugin.severe("Unable to get stats for %s from database.".formatted(uuid));
            e.printStackTrace();
        }
        return null;
    }
}
