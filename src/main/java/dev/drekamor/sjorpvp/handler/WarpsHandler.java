package dev.drekamor.sjorpvp.handler;

import dev.drekamor.sjorpvp.Database;
import dev.drekamor.sjorpvp.SjorPvP;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class WarpsHandler {
    private final SjorPvP plugin;
    private final Database database;
    private List<String> cache;
    public WarpsHandler(SjorPvP plugin, Database database){
        this.plugin = plugin;
        this.database = database;
        updateCache();
    }

    /**
     * Saves the location of the player into the database under the provided name
     *
     * @param player the CommandSender instance of Player
     * @param name the name of the warp
     * @return always returns true
     *
     */
    public boolean save(Player player, String name){
        if(this.plugin.getSjorConfig().getWarpsConfig().limitEnabled()){
            if(this.database.countWarps(player.getUniqueId().toString()) > this.plugin.getSjorConfig().getWarpsConfig().limit()){
                this.plugin.info("%s has reached their warps limit".formatted(player.getName()));
                player.sendMessage("You have reached your warps limit");

                return true;
            }
        }

        String location = this.toBase64(player.getLocation());

        if(this.database.saveWarp(player.getUniqueId().toString(), name, location)){
            this.plugin.info("%s saved warp %s".formatted(player.getName(), name));
            player.sendMessage("Saved warp %s".formatted(name));

            updateCache();

            return true;
        }
        player.sendMessage("Unable to save %s".formatted(name));
        return true;
    }
    /**
     * Removes the warp with the provided name from the database
     *
     * @param player the CommandSender instance of Player
     * @param name the name of the warp
     * @return always returns true
     *
     */
    public boolean remove(Player player, String name){
        if(this.database.removeWarp(name)){
            this.plugin.info("%s removed warp %s".formatted(player.getName(), name));
            player.sendMessage("Removed warp %s".formatted(name));

            updateCache();

            return true;
        }
        player.sendMessage("Unable to remove %s".formatted(name));
        return true;
    }
    /**
     * Lists the names of the saved warps
     *
     * @param player the CommandSender instance of Player
     * @return always returns true
     *
     */
    public boolean list(Player player){
        if(cache != null && cache.size() > 0){
            StringBuilder message = new StringBuilder(cache.get(0));
            for(int i = 1; i < cache.size(); i++){
                message.append(" ").append(cache.get(i));
            }

            player.sendMessage(message.toString());
            return true;
        }
        player.sendMessage("There are no saved warps");
        return true;
    }
    /**
     * Teleports the player to the warp with the provided name
     *
     * @param player the CommandSender instance of Player
     * @param name the name of the warp
     * @return always returns true
     *
     */
    public boolean warp(Player player, String name){
        try {
            String serializedLocation = this.database.getWarp(name);
            if(serializedLocation == null){
                player.sendMessage("Warp %s does not exist".formatted(name));
                return true;
            }
            Location location = this.fromBase64(serializedLocation);

            if(player.teleport(location)){
                this.plugin.info("Sent %s to %s".formatted(player.getName(), name));
                player.sendMessage("Teleporting to %s".formatted(name));

                return true;
            }
            player.sendMessage("Unable to get you to %s".formatted(name));
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void updateCache(){
        this.cache = database.getWarpNames();
    }
    public List<String> getCache(){
        return this.cache;
    }
    private String toBase64(Location location) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(location);

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save location.", e);
        }
    }

    private Location fromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            Location location = (Location) dataInput.readObject();

            dataInput.close();
            return location;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}
