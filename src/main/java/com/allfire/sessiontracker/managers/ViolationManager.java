package com.allfire.sessiontracker.managers;

import com.allfire.sessiontracker.SessionTracker;
import com.allfire.sessiontracker.models.Violation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ViolationManager {
    
    private final SessionTracker plugin;
    private final Map<UUID, List<Violation>> violations;
    private final Map<UUID, Long> lastCommandExecution;
    private final Map<UUID, Long> lastMaxWarnsExecution;
    private final Map<UUID, Boolean> staffNotificationsEnabled;
    private final SimpleDateFormat dateFormat;
    
    public ViolationManager(SessionTracker plugin) {
        this.plugin = plugin;
        this.violations = new ConcurrentHashMap<>();
        this.lastCommandExecution = new ConcurrentHashMap<>();
        this.lastMaxWarnsExecution = new ConcurrentHashMap<>();
        this.staffNotificationsEnabled = new ConcurrentHashMap<>();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    
    public void addViolation(UUID playerUuid, String playerName, String victimName, 
                             String cause, String reason, Location location, String ip) {
        Violation violation = new Violation(
            playerUuid, playerName, victimName, cause, reason,
            location.getBlockX(), location.getBlockY(), location.getBlockZ(),
            location.getWorld().getName(), ip, System.currentTimeMillis()
        );
        
        violations.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(violation);
        
        if (plugin.getConfigManager().isLoggingEnabled()) {
            logViolation(violation);
        }
        
        executeViolationCommands(violation);
        notifyStaff(violation);
        
        // Консольное сообщение о нарушении (можно отключить)
        if (plugin.getConfigManager().isConsoleViolationMessageEnabled()) {
            plugin.getLogger().info(plugin.getLanguageManager().getMessage("console.violation_detected",
                Map.of("player", playerName, "victim", victimName, "cause", cause)));
        }
    }
    
    private void executeViolationCommands(Violation violation) {
        if (!plugin.getConfigManager().isViolationCommandsEnabled()) {
            return;
        }
        
        long now = System.currentTimeMillis();
        Long lastExec = lastCommandExecution.get(violation.getPlayerUuid());
        if (lastExec != null && (now - lastExec) < plugin.getConfigManager().getViolationCommandCooldown() * 1000L) {
            return;
        }
        
        lastCommandExecution.put(violation.getPlayerUuid(), now);
        
        List<String> commands = plugin.getConfigManager().getViolationCommands();
        Map<String, String> placeholders = new HashMap<>();
        
        // Базовые заполнители
        placeholders.put("player", violation.getPlayerName());
        placeholders.put("victim", violation.getVictimName());
        placeholders.put("cause", violation.getCause());
        placeholders.put("reason", violation.getReason());
        placeholders.put("x", String.valueOf(violation.getX()));
        placeholders.put("y", String.valueOf(violation.getY()));
        placeholders.put("z", String.valueOf(violation.getZ()));
        placeholders.put("world", violation.getWorld());
        placeholders.put("uuid", violation.getPlayerUuid().toString());
        placeholders.put("ip", violation.getIp());
        
        // warn и maxwarn
        int warns = plugin.getWarnManager().getWarnCount(violation.getPlayerUuid());
        placeholders.put("warns", String.valueOf(warns));
        
        Player player = Bukkit.getPlayer(violation.getPlayerUuid());
        int maxWarns = plugin.getConfigManager().getDefaultMaxWarns();
        if (player != null) {
            maxWarns = plugin.getWarnManager().getMaxWarns(player);
        }
        placeholders.put("maxwarns", String.valueOf(maxWarns));
        
        for (String command : commands) {
            String processed = command;
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                processed = processed.replace("{" + entry.getKey() + "}", entry.getValue());
            }
            
            if (processed.startsWith("asConsole!")) {
                String cmd = processed.substring("asConsole!".length()).trim();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            } else if (processed.startsWith("asPlayer!")) {
                String cmd = processed.substring("asPlayer!".length()).trim();
                Player target = Bukkit.getPlayer(violation.getPlayerUuid());
                if (target != null) {
                    target.performCommand(cmd);
                }
            }
        }
    }
    
    public void executeMaxWarnsCommands(Player player, int warns, int maxWarns, String victimName) {
        if (!plugin.getConfigManager().isMaxWarnsCommandsEnabled()) {
            return;
        }
        
        long now = System.currentTimeMillis();
        Long lastExec = lastMaxWarnsExecution.get(player.getUniqueId());
        if (lastExec != null && (now - lastExec) < plugin.getConfigManager().getMaxWarnsCommandCooldown() * 1000L) {
            return;
        }
        
        lastMaxWarnsExecution.put(player.getUniqueId(), now);
        
        List<String> commands = plugin.getConfigManager().getMaxWarnsCommands();
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", player.getName());
        placeholders.put("victim", victimName != null ? victimName : "unknown");
        placeholders.put("warns", String.valueOf(warns));
        placeholders.put("maxwarns", String.valueOf(maxWarns));
        placeholders.put("uuid", player.getUniqueId().toString());
        if (player.getAddress() != null) {
            placeholders.put("ip", player.getAddress().getAddress().getHostAddress());
        } else {
            placeholders.put("ip", "unknown");
        }
        
        for (String command : commands) {
            String processed = command;
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                processed = processed.replace("{" + entry.getKey() + "}", entry.getValue());
            }
            
            if (processed.startsWith("asConsole!")) {
                String cmd = processed.substring("asConsole!".length()).trim();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            } else if (processed.startsWith("asPlayer!")) {
                String cmd = processed.substring("asPlayer!".length()).trim();
                player.performCommand(cmd);
            }
        }
    }
    
    private void notifyStaff(Violation violation) {
        List<String> format = plugin.getConfigManager().getStaffNotificationFormat();
        String permission = plugin.getConfigManager().getStaffPermission();
        
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", violation.getPlayerName());
        placeholders.put("victim", violation.getVictimName());
        placeholders.put("cause", violation.getCause());
        placeholders.put("x", String.valueOf(violation.getX()));
        placeholders.put("y", String.valueOf(violation.getY()));
        placeholders.put("z", String.valueOf(violation.getZ()));
        placeholders.put("world", violation.getWorld());
        placeholders.put("time", dateFormat.format(new Date(violation.getTimestamp())));
        
        List<String> lines = new ArrayList<>();
        for (String line : format) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                line = line.replace("{" + entry.getKey() + "}", entry.getValue());
            }
            lines.add(org.bukkit.ChatColor.translateAlternateColorCodes('&', line));
        }
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(permission) && isStaffNotificationsEnabled(player.getUniqueId())) {
                for (String line : lines) {
                    player.sendMessage(line);
                }
            }
        }
    }
    
    private void logViolation(Violation violation) {
        try {
            File logFile = new File(plugin.getDataFolder(), plugin.getConfigManager().getLogFile() + ".log");
            String format = plugin.getConfigManager().getLogFormat();
            String entry = format
                .replace("{player}", violation.getPlayerName())
                .replace("{victim}", violation.getVictimName())
                .replace("{cause}", violation.getCause())
                .replace("{uuid}", violation.getPlayerUuid().toString())
                .replace("{ip}", violation.getIp())
                .replace("{x}", String.valueOf(violation.getX()))
                .replace("{y}", String.valueOf(violation.getY()))
                .replace("{z}", String.valueOf(violation.getZ()))
                .replace("{reason}", violation.getReason());
            
            try (FileWriter fw = new FileWriter(logFile, true);
                 PrintWriter pw = new PrintWriter(fw)) {
                pw.println("[" + dateFormat.format(new Date(violation.getTimestamp())) + "] " + entry);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Ошибка записи лога: " + e.getMessage());
        }
    }
    
    public List<Violation> getViolations(UUID uuid) {
        return violations.getOrDefault(uuid, new ArrayList<>());
    }
    
    public int getViolationCount(UUID uuid) {
        return violations.getOrDefault(uuid, new ArrayList<>()).size();
    }
    
    public int getTotalViolationCount() {
        int total = 0;
        for (List<Violation> list : violations.values()) {
            total += list.size();
        }
        return total;
    }
    
    public void clearViolations(UUID uuid) {
        violations.remove(uuid);
    }
    
    public void clearAllViolations() {
        violations.clear();
    }
    
    public void setStaffNotificationsEnabled(UUID uuid, boolean enabled) {
        staffNotificationsEnabled.put(uuid, enabled);
    }
    
    public boolean isStaffNotificationsEnabled(UUID uuid) {
        return staffNotificationsEnabled.getOrDefault(uuid, plugin.getConfigManager().isStaffNotificationsDefaultEnabled());
    }
    
    public void reload() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!staffNotificationsEnabled.containsKey(player.getUniqueId())) {
                staffNotificationsEnabled.put(player.getUniqueId(), plugin.getConfigManager().isStaffNotificationsDefaultEnabled());
            }
        }
    }
}
