package com.allfire.sessiontracker.commands;

import com.allfire.sessiontracker.SessionTracker;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

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
        
        // Определяем цель для ADD (нужен онлайн игрок)
        Player targetPlayer = null;
        OfflinePlayer targetOffline = null;
        String targetName = null;
        
        if (args.length >= 4) {
            targetPlayer = Bukkit.getPlayer(args[3]);
            if (targetPlayer == null) {
                targetOffline = Bukkit.getOfflinePlayer(args[3]);
                targetName = args[3];
            } else {
                targetName = targetPlayer.getName();
            }
        } else if (sender instanceof Player) {
            targetPlayer = (Player) sender;
            targetName = sender.getName();
        } else {
            sender.sendMessage("Укажите игрока");
            return true;
        }
        
        if (action.equals("add")) {
            // ADD требует онлайн игрока
            if (targetPlayer == null) {
                sender.sendMessage("§cИгрок должен быть онлайн для добавления предупреждений!");
                return true;
            }
            plugin.getWarnManager().addWarn(targetPlayer, amount);
            int total = plugin.getWarnManager().getWarnCount(targetPlayer.getUniqueId());
            sender.sendMessage(plugin.getLanguageManager().getMessage("commands.warn.added",
                Map.of("amount", String.valueOf(amount), "player", targetName, "total", String.valueOf(total))));
            
        } else if (action.equals("remove")) {
            // REMOVE работает с оффлайн игроками
            UUID targetUuid;
            if (targetPlayer != null) {
                targetUuid = targetPlayer.getUniqueId();
                targetName = targetPlayer.getName();
            } else if (targetOffline != null && targetOffline.hasPlayedBefore()) {
                targetUuid = targetOffline.getUniqueId();
                if (targetName == null) targetName = targetOffline.getName();
            } else {
                sender.sendMessage(plugin.getLanguageManager().getMessage("commands.player_not_found"));
                return true;
            }
            
            int current = plugin.getWarnManager().getWarnCount(targetUuid);
            if (current < amount) {
                sender.sendMessage(plugin.getLanguageManager().getMessage("commands.warn.not_enough",
                    Map.of("amount", String.valueOf(amount))));
                return true;
            }
            plugin.getWarnManager().removeWarn(targetUuid, amount);
            int total = plugin.getWarnManager().getWarnCount(targetUuid);
            sender.sendMessage(plugin.getLanguageManager().getMessage("commands.warn.removed",
                Map.of("amount", String.valueOf(amount), "player", targetName, "total", String.valueOf(total))));
        } else {
            sender.sendMessage("§cИспользуйте add или remove");
        }
        return true;
    }
}
