package com.allfire.sessiontracker.models;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class Session {
    private final UUID uuid;
    private final String playerName;
    private final String ip;
    private long lastActivity;
    private long totalPlayTime;
    private final List<Interaction> interactions;
    
    public Session(Player player) {
        this.uuid = player.getUniqueId();
        this.playerName = player.getName();
        this.ip = player.getAddress().getAddress().getHostAddress();
        this.lastActivity = System.currentTimeMillis();
        this.totalPlayTime = 0;
        this.interactions = new ArrayList<>();
    }
    
    public void updateLastActivity() {
        long now = System.currentTimeMillis();
        totalPlayTime += (now - lastActivity);
        lastActivity = now;
    }
    
    public void addInteraction(String eventType, Location location) {
        interactions.add(new Interaction(eventType, location, System.currentTimeMillis()));
        if (interactions.size() > 100) {
            interactions.remove(0);
        }
    }
    
    public long getLastActivity() { return lastActivity; }
    public long getTotalPlayTime() { return totalPlayTime; }
    public List<Interaction> getInteractions() { return Collections.unmodifiableList(interactions); }
    public String getPlayerName() { return playerName; }
    public String getIp() { return ip; }
    
    public static class Interaction {
        private final String eventType;
        private final Location location;
        private final long timestamp;
        
        public Interaction(String eventType, Location location, long timestamp) {
            this.eventType = eventType;
            this.location = location.clone();
            this.timestamp = timestamp;
        }
        
        public String getEventType() { return eventType; }
        public Location getLocation() { return location; }
        public long getTimestamp() { return timestamp; }
    }
}
