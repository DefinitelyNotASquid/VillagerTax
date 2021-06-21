package com.mith.VillagerTax.commands;

import com.mith.VillagerTax.VillagerTax;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandReload implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("VillagerTax.admin")) {
            VillagerTax.getInstance().reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "Configuration successfully reloaded!");
            return true;
        }
        return false;
    }
}
