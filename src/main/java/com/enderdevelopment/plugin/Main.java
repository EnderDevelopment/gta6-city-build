package com.enderdevelopment.plugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;
    private EconomyManager economyManager;
    private CrateManager crateManager;
    private AdminPanelManager adminPanelManager;
    private GTA6Manager gta6Manager;

    @Override
    public void onEnable() {
        instance = this;

        this.economyManager = new EconomyManager(this);
        this.crateManager = new CrateManager(this);
        this.adminPanelManager = new AdminPanelManager(this);
        this.gta6Manager = new GTA6Manager(this);

        getServer().getPluginManager().registerEvents(new Listeners(this), this);

        getCommand("coins").setExecutor(new Commands(this));
        getCommand("coinspay").setExecutor(new Commands(this));
        getCommand("coinsadmin").setExecutor(new Commands(this));
        getCommand("spawn").setExecutor(new Commands(this));
        getCommand("buycrate").setExecutor(new Commands(this));
        getCommand("crateshop").setExecutor(new Commands(this));
        getCommand("adminpanel").setExecutor(new Commands(this));
        getCommand("wanted").setExecutor(new Commands(this));
        getCommand("setwanted").setExecutor(new Commands(this));

        // GTA 6 wanted level ticker
        getServer().getScheduler().runTaskTimer(this, gta6Manager::tick, 0L, 20L);

        getLogger().info("CityBuild plugin enabled.");
    }

    @Override
    public void onDisable() {
        if (economyManager != null) {
            economyManager.save();
        }
        getLogger().info("CityBuild plugin disabled.");
    }

    public static Main getInstance() {
        return instance;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public CrateManager getCrateManager() {
        return crateManager;
    }

    public AdminPanelManager getAdminPanelManager() {
        return adminPanelManager;
    }

    public GTA6Manager getGTA6Manager() {
        return gta6Manager;
    }
}
