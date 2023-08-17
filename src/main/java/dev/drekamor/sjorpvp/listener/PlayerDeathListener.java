package dev.drekamor.sjorpvp.listener;

import dev.drekamor.sjorpvp.SjorPvP;
import dev.drekamor.sjorpvp.handler.PlayerDeathHandler;
import dev.drekamor.sjorpvp.handler.PlayerStatsHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    private final SjorPvP plugin;
    private final PlayerDeathHandler handler;
    private final PlayerStatsHandler statsHandler;
    public PlayerDeathListener(SjorPvP plugin, PlayerDeathHandler handler, PlayerStatsHandler statsHandler){
        this.plugin = plugin;
        this.handler = handler;
        this.statsHandler = statsHandler;
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getPlayer();

        event.deathMessage(handler.getDeathMessage(victim));

        if(victim.getKiller() == null)
            return;

        statsHandler.addVictory(victim.getKiller().getUniqueId().toString());

        Bukkit.broadcast(handler.getDeathAnnouncement(victim));

        statsHandler.addLoss(victim.getUniqueId().toString());
    }

}
