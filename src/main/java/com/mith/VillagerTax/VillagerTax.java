package com.mith.VillagerTax;

import com.mith.VillagerTax.commands.CommandReload;
import com.mith.VillagerTax.listeners.VillagerListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class VillagerTax extends JavaPlugin {

    private static VillagerTax instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        registerListeners();
        getCommand("villagertaxreload").setExecutor(new CommandReload());
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new VillagerListener(), this);
    }

    public static VillagerTax getInstance() {
        return instance;
    }
}
