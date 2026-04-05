package com.allfire.sessiontracker.commands;

import com.allfire.sessiontracker.SessionTracker;
import com.allfire.sessiontracker.models.Violation;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.text.SimpleDateFormat;
import java.util.Date;
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
            sender.sendMessage(plugin.getLanguageManager().getMessage("commands.invalid_usage"));
            return true;
        }
        
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (target == null || !target.hasPlayedBefore()) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("commands.player_not_found"));
            return true;
        }
        
        List<Violation> violations = plugin.getViolationManager().getViolations(target.getUniqueId());
        
        sender.sendMessage(plugin.getLanguageManager().getMessage("commands.view.title",
            Map.of("player", target.getName())));
        
        if (violations.isEmpty()) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("commands.view.empty"));
        } else {
            for (Violation v : violations) {
                sender.sendMessage(plugin.getLanguageManager().getMessage("commands.view.entry",
                    Map.of("time", dateFormat.format(new Date(v.getTimestamp())),
                           "cause", v.getCause(),
                           "victim", v.getVictimName())));
            }
        }
        return true;
    }
}
