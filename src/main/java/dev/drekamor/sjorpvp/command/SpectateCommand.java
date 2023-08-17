package dev.drekamor.sjorpvp.command;

import dev.drekamor.sjorpvp.handler.SpectatorHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpectateCommand implements TabExecutor {
    private final SpectatorHandler handler;
    public SpectateCommand(SpectatorHandler handler){
        this.handler = handler;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(!(sender instanceof Player)){
            sender.sendMessage("Only players can use this command");
            return false;
        }
        if(args.length > 1) return false;

        if(args.length == 1){
            if(Bukkit.getPlayerExact(args[0]) != null){
                handler.spectate((Player) sender, Bukkit.getPlayerExact(args[0]));
                return true;
            }
            return false;
        }

        return handler.spectate((Player) sender, null);
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> list = new ArrayList<>();
        for(Player player : Bukkit.getOnlinePlayers()){
            list.add(player.getName());
        }
        return switch (args.length) {
            case 1 -> list;
            default -> null;
        };
    }
}
