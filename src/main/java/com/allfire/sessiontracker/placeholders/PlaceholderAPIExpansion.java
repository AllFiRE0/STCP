package com.allfire.sessiontracker.placeholders;

import com.allfire.sessiontracker.SessionTracker;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {
    
    private final SessionTracker plugin;
    
    public PlaceholderAPIExpansion(SessionTracker plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public @NotNull String getIdentifier() {
        return "stcp";
    }
    
    @Override
    public @NotNull String getAuthor() {
        return "AllF1RE";
    }
    
    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";
        
        if (params.equalsIgnoreCase("warn_count")) {
            return String.valueOf(plugin.getWarnManager().getWarnCount(player.getUniqueId()));
        }
        
        if (params.toLowerCase().startsWith("warn_count_")) {
            String targetName = params.substring("warn_count_".length());
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            if (target != null) {
                return String.valueOf(plugin.getWarnManager().getWarnCount(target.getUniqueId()));
            }
            return "0";
        }
        
        if (params.equalsIgnoreCase("violations")) {
            return String.valueOf(plugin.getViolationManager().getViolationCount(player.getUniqueId()));
        }
        
        if (params.toLowerCase().startsWith("violations_")) {
            String targetName = params.substring("violations_".length());
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            if (target != null) {
                return String.valueOf(plugin.getViolationManager().getViolationCount(target.getUniqueId()));
            }
            return "0";
        }
        
        return null;
    }
}
