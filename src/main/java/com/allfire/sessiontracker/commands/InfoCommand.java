package com.allfire.sessiontracker.commands;

import com.allfire.sessiontracker.SessionTracker;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class InfoCommand {
    
    private final SessionTracker plugin;
    
    public InfoCommand(SessionTracker plugin) {
        this.plugin = plugin;
    }
    
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("sessiontracker.user")) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("commands.no_permission"));
            return true;
        }
        
        OfflinePlayer target;
        if (args.length >= 2) {
            target = Bukkit.getOfflinePlayer(args[1]);
        } else if (sender instanceof Player) {
            target = (OfflinePlayer) sender;
        } else {
            sender.sendMessage("Укажите игрока");
            return true;
        }
        
        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("commands.player_not_found"));
            return true;
        }
        
        int warns = plugin.getWarnManager().getWarnCount(target.getUniqueId());
        int violations = plugin.getViolationManager().getViolationCount(target.getUniqueId());
        
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", target.getName() != null ? target.getName() : "Unknown");
        placeholders.put("uuid", target.getUniqueId().toString());
        placeholders.put("warns", String.valueOf(warns));
        placeholders.put("violations", String.valueOf(violations));
        
        sender.sendMessage(plugin.getLanguageManager().getMessage("commands.info.title", placeholders));
        sender.sendMessage(plugin.getLanguageManager().getMessage("commands.info.uuid", placeholders));
        sender.sendMessage(plugin.getLanguageManager().getMessage("commands.info.warns", placeholders));
        sender.sendMessage(plugin.getLanguageManager().getMessage("commands.info.violations", placeholders));
        
        return true;
    }
}
