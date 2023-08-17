package dev.drekamor.sjorpvp.handler;

import dev.drekamor.sjorpvp.Database;
import dev.drekamor.sjorpvp.SjorPvP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

public class PlayerStatsHandler {
    private final SjorPvP plugin;
    private final Database database;
    public PlayerStatsHandler(SjorPvP plugin, Database database){
        this.plugin = plugin;
        this.database = database;
    }
    public void addVictory(String uuid){
        String serializedStats = this.database.getPlayerStats(uuid);
        PlayerStats stats = new PlayerStats();
        if(serializedStats != null)
            stats = new PlayerStats(serializedStats);

        stats.addVictory();

        this.plugin.info("Updated statistics for " + uuid);

        this.database.savePlayerStats(uuid, stats.serialize());
    }
    public void addLoss(String uuid){
        String serializedStats = this.database.getPlayerStats(uuid);
        PlayerStats stats = new PlayerStats();
        if(serializedStats != null)
            stats = new PlayerStats(serializedStats);

        stats.addLoss();

        this.plugin.info("Updated statistics for " + uuid);

        this.database.savePlayerStats(uuid, stats.serialize());
    }
    public boolean stats(Player player){
        String serializedStats = this.database.getPlayerStats(player.getUniqueId().toString());
        PlayerStats stats = new PlayerStats();
        if(serializedStats != null)
            stats = new PlayerStats(serializedStats);

        String message = "-------Stats-------" +
                "\n" +
                "Total fights: " + stats.getFights() +
                "\n" +
                "Victories: " + stats.getVictories() +
                "\n" +
                "Current kill streak: " + stats.getKillStreak() +
                "\n" +
                "Longest kill streak: " + stats.getLongestKillStreak() +
                "\n" +
                "-------------------";
        player.sendMessage(Component.text(message).color(TextColor.color(44032)));
        return true;
    }
    public int getKillStreak(String uuid){
        String serializedStats = this.database.getPlayerStats(uuid);
        PlayerStats stats = new PlayerStats();
        if(serializedStats != null)
            stats = new PlayerStats(serializedStats);

        return stats.getKillStreak();
    }
}
class PlayerStats {
    private int fights;
    private int victories;
    private int killStreak;
    private int longestKillStreak;
    public PlayerStats(){
    }
    public PlayerStats(int fights, int victories, int killStreak, int longestKillStreak){
        this.fights = fights;
        this.victories = victories;
        this.killStreak = killStreak;
        this.longestKillStreak = longestKillStreak;
    }
    public PlayerStats(String serializedPlayerStats){
        this.deserialize(serializedPlayerStats);
    }
    public void addVictory(){
        this.victories ++;
        this.fights ++;
        this.killStreak ++;
    }
    public void addLoss(){
        this.fights ++;

        if(this.killStreak > this.longestKillStreak)
            this.longestKillStreak = this.killStreak;

        this.killStreak = 0;
    }

    public int getFights() {
        return this.fights;
    }

    public int getVictories() {
        return this.victories;
    }

    public int getLosses(){
        return this.fights - this.victories;
    }

    public int getKillStreak() {
        return this.killStreak;
    }

    public int getLongestKillStreak() {
        return this.longestKillStreak;
    }

    public String serialize(){
        String data = this.fights +
                ":" +
                this.victories +
                ":" +
                this.killStreak +
                ":" +
                this.longestKillStreak;
        return data;
    }
    private void deserialize(String data){
        String[] dataArray = data.split(":");

        if(dataArray.length < 4)
            return;

        this.fights = Integer.parseInt(dataArray[0]);
        this.victories = Integer.parseInt(dataArray[1]);
        this.killStreak = Integer.parseInt(dataArray[2]);
        this.longestKillStreak = Integer.parseInt(dataArray[3]);
    }
}
