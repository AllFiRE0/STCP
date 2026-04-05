package com.allfire.sessiontracker.managers;

import com.allfire.sessiontracker.SessionTracker;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WarnManager {
    
    private final SessionTracker plugin;
    private final Map<UUID, Integer> warns;
    private File warnsFile;
    private FileConfiguration warnsConfig;
    
    public WarnManager(SessionTracker plugin) {
        this.plugin = plugin;
        this.warns = new ConcurrentHashMap<>();
        loadWarns();
    }
    
    private void loadWarns() {
        warnsFile = new File(plugin.getDataFolder(), "warns.yml");
        
        if (!warnsFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                warnsFile.createNewFile();
                plugin.getLogger().info("Создан файл warns.yml");
            } catch (IOException e) {
                plugin.getLogger().severe("Не удалось создать warns.yml: " + e.getMessage());
            }
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
