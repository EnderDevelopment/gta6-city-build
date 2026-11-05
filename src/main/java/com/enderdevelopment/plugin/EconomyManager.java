package com.enderdevelopment.plugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyManager {

    private final Main plugin;
    private final File coinsFile;
    private FileConfiguration coinsConfig;
    private final Map<UUID, Integer> balances = new HashMap<>();

    public EconomyManager(Main plugin) {
        this.plugin = plugin;
        this.coinsFile = new File(plugin.getDataFolder(), "coins.yml");
        load();
    }

    public int getBalance(Player player) {
        return getBalance(player.getUniqueId());
    }

    public int getBalance(UUID uuid) {
        return balances.getOrDefault(uuid, 0);
    }

    public void setBalance(Player player, int amount) {
        balances.put(player.getUniqueId(), Math.max(0, amount));
    }

    public void addBalance(Player player, int amount) {
        balances.put(player.getUniqueId(), getBalance(player) + amount);
    }

    public boolean removeBalance(Player player, int amount) {
        if (getBalance(player) < amount) {
            return false;
        }
        balances.put(player.getUniqueId(), getBalance(player) - amount);
        return true;
    }

    public boolean has(Player player, int amount) {
        return getBalance(player) >= amount;
    }

    private void load() {
        if (!coinsFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                coinsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create coins.yml");
            }
        }
        coinsConfig = YamlConfiguration.loadConfiguration(coinsFile);
        for (String key : coinsConfig.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                balances.put(uuid, coinsConfig.getInt(key, 0));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public void save() {
        for (Map.Entry<UUID, Integer> entry : balances.entrySet()) {
            coinsConfig.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            coinsConfig.save(coinsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save coins.yml");
        }
    }
}
