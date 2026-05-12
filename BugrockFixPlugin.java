package com.example.bugrockfix;

import org.bukkit.plugin.java.JavaPlugin;

public final class BugrockFixPlugin extends JavaPlugin {

    private static BugrockFixPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new BlockUpdateListener(this), this);
        getLogger().info("BugrockChunkUpdateFix enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("BugrockChunkUpdateFix disabled");
    }

    public static BugrockFixPlugin getInstance() {
        return instance;
    }
}
