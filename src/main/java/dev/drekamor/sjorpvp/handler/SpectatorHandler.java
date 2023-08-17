package dev.drekamor.sjorpvp.handler;

import dev.drekamor.sjorpvp.SjorPvP;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class SpectatorHandler {
    private final SjorPvP plugin;
    public SpectatorHandler(SjorPvP plugin){
        this.plugin = plugin;
    }
    public boolean spectate(Player player, @Nullable Player target){
        if(target != null){
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(target);
            return true;
        }
        GameMode gameMode = player.getGameMode();
        player.setGameMode(gameMode == GameMode.SPECTATOR? GameMode.ADVENTURE : GameMode.SPECTATOR);
        return true;
    }
}
