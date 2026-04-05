package com.allfire.sessiontracker.commands;

import com.allfire.sessiontracker.SessionTracker;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class WarnCommand {
    
    private final SessionTracker plugin;
    
    public WarnCommand(SessionTracker plugin) {
        this.plugin = plugin;
    }
    
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("sessiontracker.admin")) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("commands.no_permission"));
            return true;
        }
        
        if (args.length < 3) {
            sender.sendMessage("§c/stcp warn add/remove <1-100> [игрок]");
            return true;
        }
        
        String action = args[1].toLowerCase();
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
            if (amount < 1 || amount > 100) {
                sender.sendMessage(plugin.getLanguageManager().getMessage("commands.invalid_number"));
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("commands.invalid_number"));
            return true;
        }
        
        OfflinePlayer target;
        if (args.length >= 4) {
            target = Bukkit.getOfflinePlayer(args[3]);
        } else if (sender instanceof org.bukkit.entity.Player) {
            target = (OfflinePlayer) sender;
        } else {
            sender.sendMessage("Укажите игрока");
            return true;
        }
        
        if (target == null || !target.hasPlayedBefore()) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("commands.player_not_found"));
            return true;
        }
        
        if (action.equals("add")) {
            plugin.getWarnManager().addWarn(target.getUniqueId(), amount);
            int total = plugin.getWarnManager().getWarnCount(target.getUniqueId());
            sender.sendMessage(plugin.getLanguageManager().getMessage("commands.warn.added",
                Map.of("amount", String.valueOf(amount), "player", target.getName(), "total", String.valueOf(total))));
        } else if (action.equals("remove")) {
            int current = plugin.getWarnManager().getWarnCount(target.getUniqueId());
            if (current < amount) {
                sender.sendMessage(plugin.getLanguageManager().getMessage("commands.warn.not_enough",
                    Map.of("amount", String.valueOf(amount))));
                return true;
            }
            plugin.getWarnManager().removeWarn(target.getUniqueId(), amount);
            int total = plugin.getWarnManager().getWarnCount(target.getUniqueId());
            sender.sendMessage(plugin.getLanguageManager().getMessage("commands.warn.removed",
                Map.of("amount", String.valueOf(amount), "player", target.getName(), "total", String.valueOf(total))));
        } else {
            sender.sendMessage("§cИспользуйте add или remove");
        }
        return true;
    }
}
