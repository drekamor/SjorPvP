package dev.drekamor.sjorpvp.handler;

import dev.drekamor.sjorpvp.SjorPvP;
import dev.drekamor.sjorpvp.util.RandomizerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.List;

public class PlayerDeathHandler {
    private final SjorPvP plugin;
    private final PlayerStatsHandler statsHandler;
    public PlayerDeathHandler(SjorPvP plugin, PlayerStatsHandler statsHandler){
        this.plugin = plugin;
        this.statsHandler = statsHandler;
    }
    public Component getDeathMessage(Player victim){
        TextComponent.Builder deathMessageComponent = Component.text().content(victim.getName())
                .append(getPotionComponent(victim, PotionType.INSTANT_HEAL));

        if(victim.getKiller() == null){
            deathMessageComponent.append(Component.text(" has died"));
            return deathMessageComponent.build();
        }

        deathMessageComponent.append(Component.text(" was killed by "))
                .append(Component.text(victim.getKiller().getName()))
                .append(getPotionComponent(victim.getKiller(), PotionType.INSTANT_HEAL))
                .append(Component.text(" using "))
                .append(victim.getKiller().getInventory().getItemInMainHand()
                        .getType() == Material.AIR?
                        Component.text("their hands") :
                        victim.getKiller().getInventory().getItemInMainHand().displayName()
                );
        return deathMessageComponent.build();
    }
    private Component getPotionComponent(Player player, PotionType... potionTypes){
        TextComponent.Builder potionComponent = Component.text().content("[");

        int i = 0;
        for (PotionType potionType : potionTypes){
            potionComponent
                    .append(Component.text(countPotions(player.getInventory().getContents(), potionType)))
                    .append(Component.text("ō").color(TextColor.color(getPotionColour(potionType))));
            i++;
            if(i < potionTypes.length){
                 potionComponent.append(Component.text(", "));
            }
        }
        potionComponent.append(Component.text("]"));
        return  potionComponent.build();
    }
    private int countPotions(ItemStack[] items, PotionType potionType){
        int count = 0;
        for(ItemStack item : items){
            if(item != null && item.getItemMeta() != null &&item.hasItemMeta()){
                if(item.getItemMeta() instanceof PotionMeta meta){
                    PotionData data = meta.getBasePotionData();
                    if(data.getType() == potionType){
                        count++;
                    }
                }
            }
        }
        return count;
    }
    private int getPotionColour(PotionType potionType){
        return switch (potionType){
            case INSTANT_HEAL -> 16131622;
            case STRENGTH -> 8388670;
            case SPEED -> 10472959;
            case FIRE_RESISTANCE -> 15302912;
            default -> 16777215;
        };
    }
    public Component getDeathAnnouncement(Player victim){
        TextComponent.Builder builder = Component.text();
        for(int i = 1; i < 4; i++){
            builder.append(getImageRow(i)).append(Component.text("\n"));
        }
        builder.append(getImageRow(4)).append(Component.text(" ")).append(Component.text(RandomizerUtil.randomString(
                plugin.getSjorConfig().getDeathMessages()
        )))
                .append(Component.text("\n"));
        builder.append(getImageRow(5)).append(Component.text(" "))
                .append(Component.text(victim.getName()))
                .append(Component.text(" had kill streak of "))
                .append(Component.text(this.statsHandler.getKillStreak(victim.getUniqueId().toString())))
                .append(Component.text("\n"));
        builder.append(getImageRow(6)).append(Component.text(" "))
                .append(Component.text(victim.getKiller().getName()))
                .append(Component.text(" has kill streak of "))
                .append(Component.text(this.statsHandler.getKillStreak(victim.getKiller().getUniqueId().toString())))
                .append(Component.text("\n"));
        builder.append(getImageRow(7)).append(Component.text("\n"));
        builder.append(getImageRow(8));
        return builder.build();
    }
    private Component getImageRow(int i){
        if(i < 0 || i > 8)
            return Component.text().build();
        List<Component> image = Arrays.asList(
                gcs(16777215, 8),
                gcs(16777215, 8),
                gcs(16777215)
                        .append(gcs(0, 2))
                        .append(gcs(16777215,2))
                        .append(gcs(0,2))
                        .append(gcs(16777215)),
                gcs(16777215)
                        .append(gcs(0, 2))
                        .append(gcs(16777215,2))
                        .append(gcs(0,2))
                        .append(gcs(16777215)),
                gcs(16777215, 8),
                gcs(16777215, 8),
                gcs(0)
                        .append(gcs(16777215, 6))
                        .append(gcs(0)),
                gcs(0)
                        .append(gcs(16777215))
                        .append(gcs(0))
                        .append(gcs(16777215, 2))
                        .append(gcs(0))
                        .append(gcs(16777215))
                        .append(gcs(0))
        );
        return image.get(i - 1);
    }
    private Component gcs(int colour, int count){
        TextComponent.Builder builder = Component.text();
        for (int i = 0; i < count; i++){
            builder.append(gcs(colour));
        }
        return builder.build();
    }
    private Component gcs(int colour){
        return Component.text("⬛").color(TextColor.color(colour));
    }
}
