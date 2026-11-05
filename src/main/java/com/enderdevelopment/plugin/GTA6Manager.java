package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GTA6Manager {

    private final Main plugin;
    private final Map<UUID, Integer> wantedLevels = new HashMap<>();

    public GTA6Manager(Main plugin) {
        this.plugin = plugin;
    }

    public int getWantedLevel(Player player) {
        return wantedLevels.getOrDefault(player.getUniqueId(), 0);
    }

    public void setWantedLevel(Player player, int level) {
        int clamped = Math.max(0, Math.min(5, level));
        wantedLevels.put(player.getUniqueId(), clamped);

        if (clamped == 0) {
            player.sendMessage(Component.text("Wanted level cleared. You are no longer wanted.")
                    .color(NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("Your wanted level is now " + clamped + " stars!")
                    .color(NamedTextColor.RED));
        }
    }

    public void increaseWanted(Player player) {
        int current = getWantedLevel(player);
        if (current < 5) {
            setWantedLevel(player, current + 1);
        }
    }

    public void tick() {
        for (UUID uuid : wantedLevels.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) continue;
            int level = getWantedLevel(player);
            if (level <= 0) continue;

            // Show wanted status periodically
            String stars = "*".repeat(level);
            player.sendActionBar(Component.text("WANTED: " + stars)
                    .color(level >= 3 ? NamedTextColor.RED : NamedTextColor.YELLOW));

            // Police chase visual effect at level 3+
            if (level >= 3) {
                player.getWorld().strikeLightningEffect(player.getLocation());
            }
        }
    }
}
