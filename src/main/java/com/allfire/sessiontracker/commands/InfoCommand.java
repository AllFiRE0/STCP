package com.allfire.sessiontracker.commands;

import com.allfire.sessiontracker.SessionTracker;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        
        sender.sendMessage(plugin.getLanguageManager().getMessage("commands.info.title",
            Map.of("player", target.getName() != null ? target.getName() : "Unknown")));
        sender.sendMessage(plugin.getLanguageManager().getMessage("commands.info.uuid",
            Map.of("uuid", target.getUniqueId().toString())));
        sender.sendMessage(plugin.getLanguageManager().getMessage("commands.info.warns",
            Map.of("warns", String.valueOf(warns))));
        sender.sendMessage(plugin.getLanguageManager().getMessage("commands.info.violations",
            Map.of("violations", String.valueOf(violations))));
        
        return true;
    }
}
