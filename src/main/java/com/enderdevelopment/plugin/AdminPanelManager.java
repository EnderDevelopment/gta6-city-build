package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class AdminPanelManager {

    public static final int ADMIN_PANEL_PRICE = 200;
    private final Main plugin;
    private final NamespacedKey adminKey;

    public AdminPanelManager(Main plugin) {
        this.plugin = plugin;
        this.adminKey = new NamespacedKey(plugin, "admin_panel");
    }

    public void openShop(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("Admin Panel Shop").color(NamedTextColor.RED));

        ItemStack adminPanel = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta meta = adminPanel.getItemMeta();
        meta.displayName(Component.text("Admin Panel").color(NamedTextColor.RED));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Unlock powerful admin tools.").color(NamedTextColor.GRAY));
        lore.add(Component.text("Price: 200 Coins").color(NamedTextColor.YELLOW));
        meta.lore(lore);
        meta.getPersistentDataContainer().set(adminKey, PersistentDataType.STRING, "admin_panel");
        adminPanel.setItemMeta(meta);

        inv.setItem(13, adminPanel);
        player.openInventory(inv);
    }

    public void buy(Player player) {
        if (!plugin.getEconomyManager().has(player, ADMIN_PANEL_PRICE)) {
            player.sendMessage(Component.text("You do not have enough coins! You need 200 coins.")
                    .color(NamedTextColor.RED));
            return;
        }
        plugin.getEconomyManager().removeBalance(player, ADMIN_PANEL_PRICE);
        player.getInventory().addItem(adminPanelItem());
        player.sendMessage(Component.text("You bought the Admin Panel for 200 coins! Use it to open the panel.")
                .color(NamedTextColor.GREEN));
    }

    private ItemStack adminPanelItem() {
        ItemStack panel = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta meta = panel.getItemMeta();
        meta.displayName(Component.text("Admin Panel").color(NamedTextColor.RED));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Right-click to open the admin panel.").color(NamedTextColor.GRAY));
        meta.lore(lore);
        meta.getPersistentDataContainer().set(adminKey, PersistentDataType.STRING, "admin_panel");
        panel.setItemMeta(meta);
        return panel;
    }

    public boolean isAdminPanel(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        String val = item.getItemMeta().getPersistentDataContainer()
                .get(adminKey, PersistentDataType.STRING);
        return "admin_panel".equals(val);
    }

    public void openPanel(Player player) {
        if (!player.hasPermission("citybuild.admin")) {
            player.sendMessage(Component.text("Only admins can open the admin panel.").color(NamedTextColor.RED));
            return;
        }
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("Admin Panel").color(NamedTextColor.RED));
        inv.setItem(11, makeButton(Material.NETHERITE_INGOT, "Give Coins", "Give coins to a player."));
        inv.setItem(13, makeButton(Material.DIAMOND_SWORD, "Give God Items", "Give powerful gear."));
        inv.setItem(15, makeButton(Material.CLOCK, "Set Wanted Level", "Set a player's wanted level."));
        player.openInventory(inv);
    }

    private ItemStack makeButton(Material material, String name, String loreText) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name).color(NamedTextColor.GOLD));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(loreText).color(NamedTextColor.GRAY));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
