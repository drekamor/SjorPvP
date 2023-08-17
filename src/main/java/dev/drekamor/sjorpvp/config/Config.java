package dev.drekamor.sjorpvp.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private final FileConfiguration config;
    public Config(FileConfiguration config){
        this.config = config;
    }
    public DatabaseCredentials getDatabaseCredentials() {
        ConfigurationSection sql = this.config.getConfigurationSection("sql");
        return new DatabaseCredentials(
                sql.getString("database"),
                sql.getString("user"),
                sql.getString("password"),
                sql.getString("host"),
                sql.getInt("port"),
                sql.getInt("poolSize"),
                sql.getLong("connectionTimeout"),
                sql.getLong("idleTimeout"),
                sql.getLong("maxLifetime")
        );
    }
    public InventoriesConfig getInventoriesConfig(){
        ConfigurationSection limit = this.config.getConfigurationSection("inventories.limit");
        return new InventoriesConfig(
                limit.getBoolean("enabled"),
                limit.getInt("maximum")
        );
    }
    public WarpsConfig getWarpsConfig(){
        ConfigurationSection limit = this.config.getConfigurationSection("warps.limit");
        return new WarpsConfig(
                limit.getBoolean("enabled"),
                limit.getInt("maximum")
        );
    }
    public List<String> getTeamNames() {
        return new ArrayList(this.config.getConfigurationSection("teams").getKeys(false));
    }

    public String getColour(String name) {
        ConfigurationSection teamsConfig = this.config.getConfigurationSection("teams");
        return teamsConfig.getString(name + ".colour");
    }
    public List<String> getDeathMessages(){
        return this.config.getStringList("messages");
    }

}

