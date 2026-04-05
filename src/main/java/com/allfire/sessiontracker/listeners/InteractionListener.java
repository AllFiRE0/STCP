package com.allfire.sessiontracker.listeners;

import com.allfire.sessiontracker.SessionTracker;
import com.allfire.sessiontracker.managers.SessionManager;
import com.allfire.sessiontracker.managers.ViolationManager;
import com.allfire.sessiontracker.models.Session;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractionListener implements Listener {
    
    private final SessionTracker plugin;
    
    public InteractionListener(SessionTracker plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        handleInteraction(event.getPlayer(), "BLOCK_BREAK", event.getBlock().getLocation());
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        handleInteraction(event.getPlayer(), "BLOCK_PLACE", event.getBlock().getLocation());
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            handleInteraction(event.getPlayer(), event.getAction().name(), event.getClickedBlock().getLocation());
        }
    }
    
    private void handleInteraction(Player player, String eventType, Location location) {
        plugin.getSessionManager().updateActivity(player);
        plugin.getSessionManager().addInteraction(player, eventType, location);
        
        // Проверка на нарушение (проверяем другие сессии)
        Session currentSession = plugin.getSessionManager().getSession(player.getUniqueId());
        int radius = plugin.getConfigManager().getCheckRadius();
        long timeWindow = plugin.getConfigManager().getTimeWindowSeconds() * 1000L;
        
        for (Session other : plugin.getSessionManager().getSessions()) {
            if (other.getPlayerName().equals(player.getName())) continue;
            
            for (Session.Interaction interaction : other.getInteractions()) {
                if (System.currentTimeMillis() - interaction.getTimestamp() > timeWindow) continue;
                if (location.distance(interaction.getLocation()) <= radius) {
                    
                    plugin.getViolationManager().addViolation(
                        player.getUniqueId(),
                        player.getName(),
                        other.getPlayerName(),
                        eventType,
                        "Подозрительное взаимодействие рядом с действиями " + other.getPlayerName(),
                        location,
                        player.getAddress().getAddress().getHostAddress()
                    );
                    return;
                }
            }
        }
    }
}
