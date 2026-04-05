package com.allfire.sessiontracker.managers;

import com.allfire.sessiontracker.SessionTracker;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class ConfigManager {
    
    private final SessionTracker plugin;
    private FileConfiguration config;
    private File configFile;
    
    public ConfigManager(SessionTracker plugin) {
        this.plugin = plugin;
        reload();
    }
    
    public void reload() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }
    
    public FileConfiguration getConfig() { return config; }
    
    public boolean isViolationCommandsEnabled() {
        return config.getBoolean("violation-commands.enabled", false);
    }
    
    public int getViolationCommandCooldown() {
        return config.getInt("violation-commands.cooldown-seconds", 120);
    }
    
    public List<String> getViolationCommands() {
        return config.getStringList("violation-commands.commands");
    }
    
    public int getCheckRadius() {
        return config.getInt("checks.radius", 60);
    }
    
    public int getTimeWindowSeconds() {
        return config.getInt("checks.time-window-seconds", 10800);
    }
    
    public int getSessionTimeoutSeconds() {
        return config.getInt("session.timeout-seconds", 1800);
    }
    
    public boolean isSoftCheckEnabled() {
        return config.getBoolean("soft-check.enabled", true);
    }
    
    public double getSimilarityThreshold() {
        return config.getDouble("soft-check.similarity-threshold", 0.75);
    }
    
    public int getSoftCheckRadius() {
        return config.getInt("soft-check.new-radius", 20);
    }
    
    public int getSoftCheckTimeWindowSeconds() {
        return config.getInt("soft-check.new-time-window-seconds", 21600);
    }
    
    public boolean isStaffNotificationsDefaultEnabled() {
        return config.getBoolean("staff-notifications.default-enabled", false);
    }
    
    public String getStaffPermission() {
        return config.getString("staff-notifications.permission", "sessiontracker.staff");
    }
    
    public List<String> getStaffNotificationFormat() {
        return config.getStringList("staff-notifications.format");
    }
    
    public boolean isLoggingEnabled() {
        return config.getBoolean("logging.enabled", true);
    }
    
    public String getLogFile() {
        return config.getString("logging.log-file", "violations");
    }
    
    public String getLogFormat() {
        return config.getString("logging.format", "! {player} {cause} {uuid} {ip} {x},{y},{z} {reason}");
    }

    public int getDefaultMaxWarns() {
    return config.getInt("max-warns.default", 10);
   }

   public String getMaxWarnsPermission() {
   return config.getString("max-warns.permission", "stcp.maxwarns.");
   }

   public boolean isMaxWarnsCommandsEnabled() {
   return config.getBoolean("max-warns-commands.enabled", true);
   }

   public int getMaxWarnsCommandCooldown() {
   return config.getInt("max-warns-commands.cooldown-seconds", 300);
   }

   public List<String> getMaxWarnsCommands() {
   return config.getStringList("max-warns-commands.commands");
   }

   public String getBypassPermission() {
   return config.getString("bypass-permission", "stcp.bypass");
   }
}
