package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CrateManager {

    private final Main plugin;
    private final Random random = new Random();
    private final NamespacedKey crateKey;

    public static final int BASIC_CRATE_PRICE = 20;
    public static final int ADVANCED_CRATE_PRICE = 60;
    public static final int LEGENDARY_CRATE_PRICE = 150;

    public CrateManager(Main plugin) {
        this.plugin = plugin;
        this.crateKey = new NamespacedKey(plugin, "crate_type");
    }

    public void openShop(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("Crate Shop").color(NamedTextColor.GOLD));

        inv.setItem(11, crateItem(Material.CHEST, "Basic Crate", "A basic crate with common rewards.", BASIC_CRATE_PRICE, "basic"));
        inv.setItem(13, crateItem(Material.TRAPPED_CHEST, "Advanced Crate", "An advanced crate with rare rewards.", ADVANCED_CRATE_PRICE, "advanced"));
        inv.setItem(15, crateItem(Material.ENDER_CHEST, "Legendary Crate", "A legendary crate with epic rewards.", LEGENDARY_CRATE_PRICE, "legendary"));

        player.openInventory(inv);
    }

    private ItemStack crateItem(Material material, String name, String lore, int price, String type) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name).color(NamedTextColor.GOLD));
        List<Component> loreLines = new ArrayList<>();
        loreLines.add(Component.text(lore).color(NamedTextColor.GRAY));
        loreLines.add(Component.text("Price: " + price + " Coins").color(NamedTextColor.YELLOW));
        meta.lore(loreLines);
        meta.getPersistentDataContainer().set(crateKey, org.bukkit.persistence.PersistentDataType.STRING, type);
        item.setItemMeta(meta);
        return item;
    }

    public String getCrateType(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer().get(crateKey, org.bukkit.persistence.PersistentDataType.STRING);
    }

    public int getPrice(String type) {
        switch (type) {
            case "basic": return BASIC_CRATE_PRICE;
            case "advanced": return ADVANCED_CRATE_PRICE;
            case "legendary": return LEGENDARY_CRATE_PRICE;
            default: return 0;
        }
    }

    public void buyCrate(Player player, String type) {
        int price = getPrice(type);
        if (price <= 0) {
            player.sendMessage(Component.text("Invalid crate type.").color(NamedTextColor.RED));
            return;
        }
        if (!plugin.getEconomyManager().has(player, price)) {
            player.sendMessage(Component.text("You do not have enough coins! You need " + price + " coins.")
                    .color(NamedTextColor.RED));
            return;
        }
        plugin.getEconomyManager().removeBalance(player, price);
        player.getInventory().addItem(makeCrateItem(type));
        player.sendMessage(Component.text("You bought a " + type + " crate for " + price + " coins!")
                .color(NamedTextColor.GREEN));
    }

    private ItemStack makeCrateItem(String type) {
        Material mat;
        String name;
        switch (type) {
            case "advanced":
                mat = Material.TRAPPED_CHEST;
                name = "Advanced Crate";
                break;
            case "legendary":
                mat = Material.ENDER_CHEST;
                name = "Legendary Crate";
                break;
            default:
                mat = Material.CHEST;
                name = "Basic Crate";
                break;
        }
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name).color(NamedTextColor.GOLD));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Right-click to open this crate!").color(NamedTextColor.GRAY));
        meta.lore(lore);
        meta.getPersistentDataContainer().set(crateKey, org.bukkit.persistence.PersistentDataType.STRING, type);
        item.setItemMeta(meta);
        return item;
    }

    public void openCrate(Player player, ItemStack item) {
        String type = getCrateType(item);
        if (type == null) return;

        item.setAmount(item.getAmount() - 1);
        if (item.getAmount() <= 0) {
            player.getInventory().remove(item);
        }

        player.sendMessage(Component.text("Opening " + type + " crate...").color(NamedTextColor.GOLD));

        Reward reward = getRandomReward(type);
        player.getInventory().addItem(reward.item);
        player.sendMessage(Component.text("You received: " + reward.name + "!").color(NamedTextColor.GREEN));
    }

    private Reward getRandomReward(String type) {
        switch (type) {
            case "advanced":
                return advancedReward();
            case "legendary":
                return legendaryReward();
            default:
                return basicReward();
        }
    }

    private Reward basicReward() {
        int roll = random.nextInt(100);
        if (roll < 50) {
            return new Reward("32x Iron", new ItemStack(Material.IRON_INGOT, 32));
        } else if (roll < 75) {
            return new Reward("16x Gold", new ItemStack(Material.GOLD_INGOT, 16));
        } else if (roll < 90) {
            return new Reward("8x Diamond", new ItemStack(Material.DIAMOND, 8));
        } else {
            return new Reward("25 Coins", null);
        }
    }

    private Reward advancedReward() {
        int roll = random.nextInt(100);
        if (roll < 30) {
            return new Reward("16x Diamond", new ItemStack(Material.DIAMOND, 16));
        } else if (roll < 50) {
            return new Reward("8x Emerald", new ItemStack(Material.EMERALD, 8));
        } else if (roll < 70) {
            return new Reward("Enchanted Diamond Sword", enchantedSword());
        } else if (roll < 85) {
            return new Reward("50 Coins", null);
        } else {
            return new Reward("Netherite Ingot", new ItemStack(Material.NETHERITE_INGOT, 3));
        }
    }

    private Reward legendaryReward() {
        int roll = random.nextInt(100);
        if (roll < 25) {
            return new Reward("Netherite Sword", netheriteSword());
        } else if (roll < 45) {
            return new Reward("12x Netherite Ingot", new ItemStack(Material.NETHERITE_INGOT, 12));
        } else if (roll < 65) {
            return new Reward("Elytra", new ItemStack(Material.ELYTRA));
        } else if (roll < 80) {
            return new Reward("100 Coins", null);
        } else if (roll < 90) {
            return new Reward("Totem of Undying", new ItemStack(Material.TOTEM_OF_UNDYING, 2));
        } else {
            return new Reward("Dragon Egg", new ItemStack(Material.DRAGON_EGG));
        }
    }

    private ItemStack enchantedSword() {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = sword.getItemMeta();
        Enchantment sharpness = Registry.ENCHANTMENT.get(NamespacedKey.minecraft("sharpness"));
        if (sharpness != null) {
            meta.addEnchant(sharpness, 3, true);
        }
        sword.setItemMeta(meta);
        return sword;
    }

    private ItemStack netheriteSword() {
        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = sword.getItemMeta();
        Enchantment sharpness = Registry.ENCHANTMENT.get(NamespacedKey.minecraft("sharpness"));
        Enchantment fireAspect = Registry.ENCHANTMENT.get(NamespacedKey.minecraft("fire_aspect"));
        if (sharpness != null) meta.addEnchant(sharpness, 5, true);
        if (fireAspect != null) meta.addEnchant(fireAspect, 2, true);
        sword.setItemMeta(meta);
        return sword;
    }

    private static class Reward {
        final String name;
        final ItemStack item;

        Reward(String name, ItemStack item) {
            this.name = name;
            this.item = item;
        }
    }
}
