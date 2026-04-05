package com.allfire.sessiontracker.managers;

import com.allfire.sessiontracker.SessionTracker;
import com.allfire.sessiontracker.models.Session;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    
    private final SessionTracker plugin;
    private final Map<UUID, Session> sessions;
    
    public SessionManager(SessionTracker plugin) {
        this.plugin = plugin;
        this.sessions = new ConcurrentHashMap<>();
    }
    
    public Session getOrCreateSession(Player player) {
        return sessions.computeIfAbsent(player.getUniqueId(), uuid -> new Session(player));
    }
    
    public Session getSession(UUID uuid) {
        return sessions.get(uuid);
    }
    
    public void updateActivity(Player player) {
        Session session = getOrCreateSession(player);
        session.updateLastActivity();
    }
    
    public void addInteraction(Player player, String eventType, org.bukkit.Location location) {
        Session session = getOrCreateSession(player);
        session.addInteraction(eventType, location);
    }
    
    public long getPlaytime(UUID uuid) {
        Session session = sessions.get(uuid);
        return session != null ? session.getTotalPlayTime() : 0;
    }
    
    public void cleanupOldSessions() {
        long timeout = plugin.getConfigManager().getSessionTimeoutSeconds() * 1000L;
        long now = System.currentTimeMillis();
        sessions.values().removeIf(session -> now - session.getLastActivity() > timeout);
    }
}
