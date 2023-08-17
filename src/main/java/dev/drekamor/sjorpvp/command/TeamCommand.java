package dev.drekamor.sjorpvp.command;

import dev.drekamor.sjorpvp.handler.TeamHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class TeamCommand implements TabExecutor {
    private final TeamHandler handler;
    public TeamCommand(TeamHandler handler){
        this.handler = handler;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(!(sender instanceof Player)){
            sender.sendMessage("Only players can use this command");
            return false;
        }
        if(args.length != 1) return false;
        return handler.team((Player) sender, args[0]);
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return switch (args.length) {
            case 1 -> handler.getCache();
            default -> null;
        };
    }
}
