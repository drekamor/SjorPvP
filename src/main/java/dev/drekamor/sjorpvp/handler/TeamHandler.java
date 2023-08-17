package dev.drekamor.sjorpvp.handler;

import dev.drekamor.sjorpvp.SjorPvP;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class TeamHandler {
    private final SjorPvP plugin;
    private List<String> cache;
    public TeamHandler(SjorPvP plugin){
        this.plugin = plugin;
        this.cache = plugin.getSjorConfig().getTeamNames();
        this.cache.add("clear");
    }
    public List<String> getCache(){
        return this.cache;
    }
    public boolean team(Player player, String team){
        if(team.equals("clear")){
            player.displayName(Component.text(ChatColor.stripColor(player.getName())));
            player.playerListName(Component.text(ChatColor.stripColor(player.getName())));
            return true;
        }
        player.displayName(Component.text(getColour(this.plugin.getSjorConfig().getColour(team)) + player.getName()));
        player.playerListName(Component.text(getColour(this.plugin.getSjorConfig().getColour(team)) + player.getName()));
        return true;
    }
    private ChatColor getColour(String colour){
        return switch (colour){
            case "red" -> ChatColor.RED;
            case "blue" -> ChatColor.BLUE;
            case "yellow" -> ChatColor.YELLOW;
            case "green" -> ChatColor.GREEN;
            case  "black" -> ChatColor.BLACK;
            case "grey" -> ChatColor.GRAY;
            default -> ChatColor.WHITE;
        };
    }
}
