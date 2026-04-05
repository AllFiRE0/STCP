package com.allfire.sessiontracker.models;

import java.util.UUID;

public class Violation {
    private final UUID playerUuid;
    private final String playerName;
    private final String victimName;
    private final String cause;
    private final String reason;
    private final int x, y, z;
    private final String world;
    private final String ip;
    private final long timestamp;
    
    public Violation(UUID playerUuid, String playerName, String victimName, String cause, 
                     String reason, int x, int y, int z, String world, String ip, long timestamp) {
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.victimName = victimName;
        this.cause = cause;
        this.reason = reason;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.ip = ip;
        this.timestamp = timestamp;
    }
    
    public UUID getPlayerUuid() { return playerUuid; }
    public String getPlayerName() { return playerName; }
    public String getVictimName() { return victimName; }
    public String getCause() { return cause; }
    public String getReason() { return reason; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }
    public String getWorld() { return world; }
    public String getIp() { return ip; }
    public long getTimestamp() { return timestamp; }
}
