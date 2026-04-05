package com.allfire.sessiontracker.managers;

import com.allfire.sessiontracker.SessionTracker;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
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
            plugin.saveResource("warns.yml", false);
        }
        warnsConfig = YamlConfiguration.loadConfiguration(warnsFile);
        
        for (String key : warnsConfig.getKeys(false)) {
            warns.put(UUID.fromString(key), warnsConfig.getInt(key));
        }
    }
    
    private void saveWarns() {
        for (Map.Entry<UUID, Integer> entry : warns.entrySet()) {
            warnsConfig.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            warnsConfig.save(warnsFile);
        } catch (Exception e) {
            plugin.getLogger().warning("Ошибка сохранения warns.yml");
        }
    }
    
    public void addWarn(UUID uuid, int amount) {
        int current = warns.getOrDefault(uuid, 0);
        warns.put(uuid, Math.min(current + amount, 100));
        saveWarns();
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
