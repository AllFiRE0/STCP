package com.allfire.sessiontracker.commands;

import com.allfire.sessiontracker.SessionTracker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class NotificationsCommand {
    
    private final SessionTracker plugin;
    
    public NotificationsCommand(SessionTracker plugin) {
        this.plugin = plugin;
    }
    
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эта команда только для игроков");
            return true;
        }
        
        if (!sender.hasPermission("sessiontracker.staff")) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("commands.no_permission"));
            return true;
        }
        
        Player player = (Player) sender;
        boolean current = plugin.getViolationManager().isStaffNotificationsEnabled(player.getUniqueId());
        boolean newState = !current;
        plugin.getViolationManager().setStaffNotificationsEnabled(player.getUniqueId(), newState);
        
        String status = plugin.getLanguageManager().getMessage("commands.notifications." + (newState ? "enabled" : "disabled"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("commands.notifications.toggled",
            Map.of("status", status)));
        return true;
    }
}
