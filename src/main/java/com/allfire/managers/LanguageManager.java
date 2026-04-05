package com.allfire.sessiontracker.managers;

import com.allfire.sessiontracker.SessionTracker;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.*;

public class LanguageManager {
    
    private final SessionTracker plugin;
    private FileConfiguration langConfig;
    
    public LanguageManager(SessionTracker plugin) {
        this.plugin = plugin;
        reload();
    }
    
    public void reload() {
        File langFile = new File(plugin.getDataFolder(), "lang.yml");
        if (!langFile.exists()) {
            try (InputStream input = plugin.getResource("lang.yml")) {
                if (input != null) {
                    Files.copy(input, langFile.toPath());
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Не удалось сохранить lang.yml");
            }
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }
    
    public String getMessage(String path) {
        return getMessage(path, null);
    }
    
    public String getMessage(String path, Map<String, String> placeholders) {
        String message = langConfig.getString(path);
        if (message == null) {
            return ChatColor.RED + "Missing: " + path;
        }
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public List<String> getMessageList(String path) {
        return getMessageList(path, null);
    }
    
    public List<String> getMessageList(String path, Map<String, String> placeholders) {
        List<String> messages = langConfig.getStringList(path);
        List<String> result = new ArrayList<>();
        for (String msg : messages) {
            if (placeholders != null) {
                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    msg = msg.replace("{" + entry.getKey() + "}", entry.getValue());
                }
            }
            result.add(ChatColor.translateAlternateColorCodes('&', msg));
        }
        return result;
    }
}
