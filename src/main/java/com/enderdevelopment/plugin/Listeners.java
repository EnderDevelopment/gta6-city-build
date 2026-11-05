package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Listeners implements Listener {

    private final Main plugin;
    private final Map<UUID, UUID> lastDamager = new HashMap<>();

    public Listeners(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCrateOpen(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        ItemStack item = event.getItem();
        if (item == null) return;

        String crateType = plugin.getCrateManager().getCrateType(item);
        if (crateType != null) {
            event.setCancelled(true);
            plugin.getCrateManager().openCrate(event.getPlayer(), item);
            return;
        }

        if (plugin.getAdminPanelManager().isAdminPanel(item)) {
            event.setCancelled(true);
            plugin.getAdminPanelManager().openPanel(event.getPlayer());
        }
    }

    @EventHandler
    public void onShopClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().title().toString();

        if (title.contains("Crate Shop")) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item == null) return;
            String type = plugin.getCrateManager().getCrateType(item);
            if (type != null) {
                player.closeInventory();
                plugin.getCrateManager().buyCrate(player, type);
            }
        } else if (title.contains("Admin Panel Shop")) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item == null) return;
            if (plugin.getAdminPanelManager().isAdminPanel(item)) {
                player.closeInventory();
                plugin.getAdminPanelManager().buy(player);
            }
        } else if (title.contains("Admin Panel")) {
            event.setCancelled(true);
            handleAdminPanelAction(player, event.getSlot());
        }
    }

    private void handleAdminPanelAction(Player player, int slot) {
        switch (slot) {
            case 11:
                player.closeInventory();
                player.sendMessage(Component.text("Use /coinsadmin <add|remove|set> <player> <amount> to give coins.")
                        .color(NamedTextColor.GREEN));
                break;
            case 13:
                player.closeInventory();
                giveGodItems(player);
                break;
            case 15:
                player.closeInventory();
                player.sendMessage(Component.text("Use /setwanted <player> <level> to set a wanted level.")
                        .color(NamedTextColor.GREEN));
                break;
            default:
                break;
        }
    }

    private void giveGodItems(Player player) {
        ItemStack helmet = godTool(Material.NETHERITE_HELMET, "God Helmet");
        ItemStack chest = godTool(Material.NETHERITE_CHESTPLATE, "God Chestplate");
        ItemStack legs = godTool(Material.NETHERITE_LEGGINGS, "God Leggings");
        ItemStack boots = godTool(Material.NETHERITE_BOOTS, "God Boots");
        ItemStack sword = godTool(Material.NETHERITE_SWORD, "God Sword");

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chest);
        player.getInventory().setLeggings(legs);
        player.getInventory().setBoots(boots);
        player.getInventory().addItem(sword);

        player.sendMessage(Component.text("You received the God Kit!").color(NamedTextColor.GREEN));
    }

    private ItemStack godTool(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name).color(NamedTextColor.LIGHT_PURPLE));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Forged by the Admin Panel").color(NamedTextColor.GRAY));
        meta.lore(lore);
        org.bukkit.enchantments.Enchantment protection =
                org.bukkit.Registry.ENCHANTMENT.get(org.bukkit.NamespacedKey.minecraft("protection"));
        org.bukkit.enchantments.Enchantment unbreaking =
                org.bukkit.Registry.ENCHANTMENT.get(org.bukkit.NamespacedKey.minecraft("unbreaking"));
        org.bukkit.enchantments.Enchantment sharpness =
                org.bukkit.Registry.ENCHANTMENT.get(org.bukkit.NamespacedKey.minecraft("sharpness"));
        if (protection != null) meta.addEnchant(protection, 4, true);
        if (unbreaking != null) meta.addEnchant(unbreaking, 3, true);
        if (sharpness != null) meta.addEnchant(sharpness, 5, true);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null && !killer.equals(victim)) {
            // GTA 6 style: killing without a reason raises the wanted level
            plugin.getGTA6Manager().increaseWanted(killer);
            killer.sendMessage(Component.text("You committed a crime! Your wanted level increased.")
                    .color(NamedTextColor.RED));
        }
    }

    @EventHandler
    public void onDamage(org.bukkit.event.entity.EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player victim && event.getDamager() instanceof Player damager) {
            lastDamager.put(victim.getUniqueId(), damager.getUniqueId());
        }
    }
}
