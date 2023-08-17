package dev.drekamor.sjorpvp.command;

import dev.drekamor.sjorpvp.handler.PlayerStatsHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {
    private final PlayerStatsHandler handler;
    public StatsCommand(PlayerStatsHandler handler){
        this.handler = handler;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(!(sender instanceof Player)){
            sender.sendMessage("Only players can use this command");
            return false;
        }

        if(args.length != 0)
            return false;

        return handler.stats((Player) sender);
    }
}
