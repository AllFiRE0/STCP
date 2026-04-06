package com.allfire.sessiontracker.placeholders;

import com.allfire.sessiontracker.SessionTracker;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
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
        
        // %stcp_warns%
        if (params.equalsIgnoreCase("warns")) {
            return String.valueOf(plugin.getWarnManager().getWarnCount(player.getUniqueId()));
        }
        
        // %stcp_warns_{player}%
        if (params.toLowerCase().startsWith("warns_")) {
            String targetName = params.substring("warns_".length());
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            if (target != null) {
                return String.valueOf(plugin.getWarnManager().getWarnCount(target.getUniqueId()));
            }
            return "0";
        }
        
        // %stcp_maxwarns%
        if (params.equalsIgnoreCase("maxwarns")) {
            Player onlinePlayer = player.getPlayer();
            if (onlinePlayer != null) {
                return String.valueOf(plugin.getWarnManager().getMaxWarns(onlinePlayer));
            }
            return String.valueOf(plugin.getConfigManager().getDefaultMaxWarns());
        }
        
        // %stcp_maxwarns_{player}%
        if (params.toLowerCase().startsWith("maxwarns_")) {
            String targetName = params.substring("maxwarns_".length());
            Player target = Bukkit.getPlayer(targetName);
            if (target != null) {
                return String.valueOf(plugin.getWarnManager().getMaxWarns(target));
            }
            return String.valueOf(plugin.getConfigManager().getDefaultMaxWarns());
        }
        
        // %stcp_violations%
        if (params.equalsIgnoreCase("violations")) {
            return String.valueOf(plugin.getViolationManager().getViolationCount(player.getUniqueId()));
        }
        
        // %stcp_violations_{player}%
        if (params.toLowerCase().startsWith("violations_")) {
            String targetName = params.substring("violations_".length());
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            if (target != null) {
                return String.valueOf(plugin.getViolationManager().getViolationCount(target.getUniqueId()));
            }
            return "0";
        }
        
        // %stcp_player%
        if (params.equalsIgnoreCase("player")) {
            return player.getName();
        }
        
        // %stcp_player_uuid%
        if (params.equalsIgnoreCase("player_uuid")) {
            return player.getUniqueId().toString();
        }
        
        // %stcp_player_ip%
        if (params.equalsIgnoreCase("player_ip")) {
            Player onlinePlayer = player.getPlayer();
            if (onlinePlayer != null && onlinePlayer.getAddress() != null) {
                return onlinePlayer.getAddress().getAddress().getHostAddress();
            }
            return "unknown";
        }
        
        return null;
    }
}
