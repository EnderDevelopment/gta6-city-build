package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor {

    private final Main plugin;

    public Commands(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        switch (command.getName().toLowerCase()) {
            case "coins":
                return handleCoins(sender);
            case "coinspay":
                return handlePay(sender, args);
            case "coinsadmin":
                return handleCoinsAdmin(sender, args);
            case "spawn":
                return handleSpawn(sender);
            case "buycrate":
            case "crateshop":
                return handleCrateShop(sender);
            case "adminpanel":
                return handleAdminPanelShop(sender);
            case "wanted":
                return handleWanted(sender);
            case "setwanted":
                return handleSetWanted(sender, args);
            default:
                return false;
        }
    }

    private boolean handleCoins(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.").color(NamedTextColor.RED));
            return true;
        }
        int balance = plugin.getEconomyManager().getBalance(player);
        player.sendMessage(Component.text("Your balance: " + balance + " coins").color(NamedTextColor.GOLD));
        return true;
    }

    private boolean handlePay(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.").color(NamedTextColor.RED));
            return true;
        }
        if (args.length != 2) {
            player.sendMessage(Component.text("Usage: /coinspay <player> <amount>").color(NamedTextColor.RED));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(Component.text("Player not found.").color(NamedTextColor.RED));
            return true;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("Invalid amount.").color(NamedTextColor.RED));
            return true;
        }
        if (amount <= 0) {
            player.sendMessage(Component.text("Amount must be positive.").color(NamedTextColor.RED));
            return true;
        }
        if (!plugin.getEconomyManager().has(player, amount)) {
            player.sendMessage(Component.text("You do not have enough coins.").color(NamedTextColor.RED));
            return true;
        }
        plugin.getEconomyManager().removeBalance(player, amount);
        plugin.getEconomyManager().addBalance(target, amount);
        player.sendMessage(Component.text("You paid " + amount + " coins to " + target.getName() + ".")
                .color(NamedTextColor.GREEN));
        target.sendMessage(Component.text(player.getName() + " paid you " + amount + " coins.")
                .color(NamedTextColor.GREEN));
        return true;
    }

    private boolean handleCoinsAdmin(CommandSender sender, String[] args) {
        if (!sender.hasPermission("citybuild.coins.admin")) {
            sender.sendMessage(Component.text("You do not have permission.").color(NamedTextColor.RED));
            return true;
        }
        if (args.length != 3) {
            sender.sendMessage(Component.text("Usage: /coinsadmin <add|remove|set> <player> <amount>")
                    .color(NamedTextColor.RED));
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("Player not found.").color(NamedTextColor.RED));
            return true;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Invalid amount.").color(NamedTextColor.RED));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "add":
                plugin.getEconomyManager().addBalance(target, amount);
                break;
            case "remove":
                plugin.getEconomyManager().removeBalance(target, amount);
                break;
            case "set":
                plugin.getEconomyManager().setBalance(target, amount);
                break;
            default:
                sender.sendMessage(Component.text("Unknown action.").color(NamedTextColor.RED));
                return true;
        }
        sender.sendMessage(Component.text("Updated " + target.getName() + "'s balance.")
                .color(NamedTextColor.GREEN));
        return true;
    }

    private boolean handleSpawn(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.").color(NamedTextColor.RED));
            return true;
        }
        Location spawn = player.getWorld().getSpawnLocation();
        player.teleport(spawn);
        player.sendMessage(Component.text("Welcome to the City Build Spawn!").color(NamedTextColor.GOLD));
        return true;
    }

    private boolean handleCrateShop(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.").color(NamedTextColor.RED));
            return true;
        }
        // Only allow the crate shop from spawn
        Location spawn = player.getWorld().getSpawnLocation();
        if (player.getLocation().distance(spawn) > 30) {
            player.sendMessage(Component.text("You must be at spawn to buy crates! Use /spawn first.")
                    .color(NamedTextColor.RED));
            return true;
        }
        plugin.getCrateManager().openShop(player);
        return true;
    }

    private boolean handleAdminPanelShop(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.").color(NamedTextColor.RED));
            return true;
        }
        plugin.getAdminPanelManager().openShop(player);
        return true;
    }

    private boolean handleWanted(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.").color(NamedTextColor.RED));
            return true;
        }
        int level = plugin.getGTA6Manager().getWantedLevel(player);
        player.sendMessage(Component.text("Your wanted level: " + "*".repeat(level) + " (" + level + "/5)")
                .color(level > 0 ? NamedTextColor.RED : NamedTextColor.GREEN));
        return true;
    }

    private boolean handleSetWanted(CommandSender sender, String[] args) {
        if (!sender.hasPermission("citybuild.admin")) {
            sender.sendMessage(Component.text("You do not have permission.").color(NamedTextColor.RED));
            return true;
        }
        if (args.length != 2) {
            sender.sendMessage(Component.text("Usage: /setwanted <player> <level>").color(NamedTextColor.RED));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Component.text("Player not found.").color(NamedTextColor.RED));
            return true;
        }
        int level;
        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Invalid level.").color(NamedTextColor.RED));
            return true;
        }
        plugin.getGTA6Manager().setWantedLevel(target, level);
        sender.sendMessage(Component.text("Set " + target.getName() + "'s wanted level to " + level + ".")
                .color(NamedTextColor.GREEN));
        return true;
    }
}
