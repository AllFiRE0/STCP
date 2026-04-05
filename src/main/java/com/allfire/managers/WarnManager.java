package com.allfire.sessiontracker.commands;

import com.allfire.sessiontracker.SessionTracker;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.UUID;

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
        
        // Определяем цель
        Player targetPlayer = null;
        OfflinePlayer targetOffline = null;
        
        if (args.length >= 4) {
            targetPlayer = Bukkit.getPlayer(args[3]);
            if (targetPlayer == null) {
                targetOffline = Bukkit.getOfflinePlayer(args[3]);
            }
        } else if (sender instanceof Player) {
            targetPlayer = (Player) sender;
        } else {
            sender.sendMessage("Укажите игрока");
            return true;
        }
        
        if (action.equals("add")) {
            if (targetPlayer == null) {
                sender.sendMessage(plugin.getLanguageManager().getMessage("commands.player_not_found"));
                return true;
            }
            plugin.getWarnManager().addWarn(targetPlayer, amount);
            int total = plugin.getWarnManager().getWarnCount(targetPlayer.getUniqueId());
            sender.sendMessage(plugin.getLanguageManager().getMessage("commands.warn.added",
                Map.of("amount", String.valueOf(amount), "player", targetPlayer.getName(), "total", String.valueOf(total))));
        } else if (action.equals("remove")) {
            UUID targetUuid;
            String targetName;
            if (targetPlayer != null) {
                targetUuid = targetPlayer.getUniqueId();
                targetName = targetPlayer.getName();
            } else if (targetOffline != null && targetOffline.hasPlayedBefore()) {
                targetUuid = targetOffline.getUniqueId();
                targetName = targetOffline.getName();
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
}            }
        }
        
        warnsConfig = YamlConfiguration.loadConfiguration(warnsFile);
        
        for (String key : warnsConfig.getKeys(false)) {
            try {
                warns.put(UUID.fromString(key), warnsConfig.getInt(key));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Неверный UUID в warns.yml: " + key);
            }
        }
    }
    
    private void saveWarns() {
        for (Map.Entry<UUID, Integer> entry : warns.entrySet()) {
            warnsConfig.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            warnsConfig.save(warnsFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Ошибка сохранения warns.yml: " + e.getMessage());
        }
    }
    
    public int getMaxWarns(Player player) {
        String permission = plugin.getConfigManager().getMaxWarnsPermission();
        int maxWarns = plugin.getConfigManager().getDefaultMaxWarns();
        
        for (int i = 100; i >= 1; i--) {
            if (player.hasPermission(permission + i)) {
                return i;
            }
        }
        return maxWarns;
    }
    
    public void addWarn(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        int current = warns.getOrDefault(uuid, 0);
        int newAmount = Math.min(current + amount, 100);
        warns.put(uuid, newAmount);
        saveWarns();
        
        // Проверка на достижение лимита
        int maxWarns = getMaxWarns(player);
        if (newAmount >= maxWarns) {
            plugin.getViolationManager().executeMaxWarnsCommands(player, newAmount, maxWarns);
        }
    }
    
    public void removeWarn(UUID uuid, int amount) {
        int current = warns.getOrDefault(uuid, 0);
        int newAmount = Math.max(current - amount, 0);
        if (newAmount == 0) {
            warns.remove(uuid);
        } else {
            warns.put(uuid, newAmount);
        }
        saveWarns();
    }
    
    public int getWarnCount(UUID uuid) {
        return warns.getOrDefault(uuid, 0);
    }
}
