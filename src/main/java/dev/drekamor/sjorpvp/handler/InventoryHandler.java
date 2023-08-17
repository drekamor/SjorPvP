package dev.drekamor.sjorpvp.handler;

import dev.drekamor.sjorpvp.Database;
import dev.drekamor.sjorpvp.SjorPvP;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class InventoryHandler {
    private final SjorPvP plugin;
    private final Database database;
    private List<String> cache;
    public InventoryHandler(SjorPvP plugin, Database database){
        this.plugin = plugin;
        this.database = database;
        updateCache();
    }

    /**
     * Saves the inventory of the player into the database under the provided name
     *
     * @param player the CommandSender instance of Player
     * @param name the name of the inventory
     * @return always returns true
     *
     */
    public boolean save(Player player, String name){
        if(this.plugin.getSjorConfig().getInventoriesConfig().limitEnabled()){
            if(this.database.countInventories(player.getUniqueId().toString()) > this.plugin.getSjorConfig().getInventoriesConfig().limit()){
                this.plugin.info("%s has reached their inventories limit".formatted(player.getName()));
                player.sendMessage("You have reached your inventories limit");

                return true;
            }
        }

        String inventoryContents = this.toBase64(player.getInventory().getContents());
        String armourContents = this.toBase64(player.getInventory().getArmorContents());

        if(this.database.saveInventory(player.getUniqueId().toString(), name, inventoryContents, armourContents)){
            this.plugin.info("%s saved inventory %s".formatted(player.getName(), name));
            player.sendMessage("Saved inventory %s".formatted(name));

            updateCache();

            return true;
        }

        player.sendMessage("Unable to save %s".formatted(name));
        return true;
    }
    /**
     * Loads the inventory with the provided name and sets it for the provided player
     *
     * @param player the CommandSender instance of Player
     * @param name the name of the inventory
     * @return always returns true
     *
     */
    public boolean load(Player player, String name){
        try {
            String[] serializedInventory = this.database.getInventory(name);
            if(serializedInventory == null){
                player.sendMessage("Inventory %s does not exist".formatted(name));
                return true;
            }
            ItemStack[] inventoryContents = this.fromBase64(serializedInventory[0]);
            ItemStack[] armourContents = this.fromBase64(serializedInventory[1]);

            player.getInventory().setContents(inventoryContents);
            player.getInventory().setArmorContents(armourContents);
            player.updateInventory();

            this.plugin.info("%s loaded inventory %s".formatted(player.getName(), name));
            player.sendMessage("Loaded inventory %s".formatted(name));

            return true;
        } catch (IOException e) {
            player.sendMessage("Unable to load %s".formatted(name));
            throw new RuntimeException(e);
        }
    }
    /**
     * Removes the inventory with the provided name from the database
     *
     * @param player the CommandSender instance of Player
     * @param name the name of the inventory
     * @return always returns true
     *
     */
    public boolean remove(Player player, String name){
        if(this.database.removeInventory(name)){
            this.plugin.info("%s removed inventory %s".formatted(player.getName(), name));
            player.sendMessage("Removed inventory %s".formatted(name));

            updateCache();

            return true;
        }
        player.sendMessage("Unable to remove %s".formatted(name));
        return true;
    }
    /**
     * Lists the names of the saved inventories
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
        player.sendMessage("There are no saved inventories");
        return true;
    }
    private void updateCache(){
        this.cache = database.getInventoryNames();
    }
    public List<String> getCache(){
        return this.cache;
    }

    private String toBase64(ItemStack[] items) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(items.length);

            for (ItemStack item : items)
                dataOutput.writeObject(item);

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }
    private ItemStack[] fromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int i = 0; i < items.length; i++)
                items[i] = (ItemStack) dataInput.readObject();

            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}
