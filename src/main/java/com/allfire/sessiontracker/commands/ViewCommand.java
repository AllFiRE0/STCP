package com.allfire.sessiontracker.commands;

import com.allfire.sessiontracker.SessionTracker;
import com.allfire.sessiontracker.models.Violation;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewCommand {
    
    private final SessionTracker plugin;
    private final SimpleDateFormat dateFormat;
    
    public ViewCommand(SessionTracker plugin) {
        this.plugin = plugin;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("sessiontracker.staff")) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("commands.no_permission"));
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§c/stcp view <игрок>");
            return true;
        }
        
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (target == null || !target.hasPlayedBefore()) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("commands.player_not_found"));
            return true;
        }
        
        List<Violation> violations = plugin.getViolationManager().getViolations(target.getUniqueId());
        
        Map<String, String> titlePlaceholders = new HashMap<>();
        titlePlaceholders.put("player", target.getName() != null ? target.getName() : "Unknown");
        sender.sendMessage(plugin.getLanguageManager().getMessage("commands.view.title", titlePlaceholders));
        
        if (violations.isEmpty()) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("commands.view.empty"));
        } else {
            for (Violation v : violations) {
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("time", dateFormat.format(new Date(v.getTimestamp())));
                placeholders.put("cause", v.getCause());
                placeholders.put("victim", v.getVictimName());
                placeholders.put("x", String.valueOf(v.getX()));
                placeholders.put("y", String.valueOf(v.getY()));
                placeholders.put("z", String.valueOf(v.getZ()));
                placeholders.put("world", v.getWorld());
                placeholders.put("player", v.getPlayerName());
                placeholders.put("uuid", v.getPlayerUuid().toString());
                placeholders.put("ip", v.getIp());
                
                sender.sendMessage(plugin.getLanguageManager().getMessage("commands.view.entry", placeholders));
            }
        }
        return true;
    }
}
