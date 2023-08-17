package dev.drekamor.sjorpvp;

import dev.drekamor.sjorpvp.command.*;
import dev.drekamor.sjorpvp.config.Config;
import dev.drekamor.sjorpvp.handler.*;
import dev.drekamor.sjorpvp.listener.PlayerDeathListener;
import org.bukkit.plugin.java.JavaPlugin;

public class SjorPvP extends JavaPlugin {
    private Config config;
    private Database database;
    private InventoryHandler inventoryHandler;
    private WarpsHandler warpsHandler;
    private PlayerDeathHandler playerDeathHandler;
    private PlayerStatsHandler playerStatsHandler;
    private TeamHandler teamHandler;
    private SpectatorHandler spectatorHandler;
    @Override
    public void onEnable() {
        info("Initializing");

        this.saveDefaultConfig();
        this.reloadConfig();

        this.config = new Config(this.getConfig());
        info("Successfully loaded the config");

        info("Initializing database connection");
        this.database = new Database(this, config.getDatabaseCredentials());
        this.database.initializeTables();

        info("Preparing handlers");
        this.inventoryHandler = new InventoryHandler(this, this.database);
        this.warpsHandler = new WarpsHandler(this, this.database);
        this.playerStatsHandler = new PlayerStatsHandler(this, this.database);
        this.playerDeathHandler = new PlayerDeathHandler(this, this.playerStatsHandler);
        this.teamHandler = new TeamHandler(this);
        this.spectatorHandler = new SpectatorHandler(this);

        info("Registering commands");
        this.registerCommands();

        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this, this.playerDeathHandler, this.playerStatsHandler), this);

    }
    private void registerCommands(){
        this.getCommand("inventory").setExecutor(new InventoryCommand(this.inventoryHandler));
        this.getCommand("warps").setExecutor(new WarpsCommand(this.warpsHandler));
        this.getCommand("warp").setExecutor(new WarpCommand(this.warpsHandler));
        this.getCommand("stats").setExecutor(new StatsCommand(this.playerStatsHandler));
        this.getCommand("team").setExecutor(new TeamCommand(this.teamHandler));
        this.getCommand("spectate").setExecutor(new SpectateCommand(this.spectatorHandler));
    }
    @Override
    public void onDisable() {
        getLogger().info("Stopping");
    }
    public void info(String string){getLogger().info(string);}
    public void warning(String string){getLogger().warning(string);}
    public void severe(String string){getLogger().severe(string);}
    public Config getSjorConfig(){
        return  this.config;
    }
}
