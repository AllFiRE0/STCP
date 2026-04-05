package com.allfire.sessiontracker;

import com.allfire.sessiontracker.commands.MainCommand;
import com.allfire.sessiontracker.listeners.InteractionListener;
import com.allfire.sessiontracker.managers.*;
import com.allfire.sessiontracker.placeholders.PlaceholderAPIExpansion;
import org.bukkit.plugin.java.JavaPlugin;

public class SessionTracker extends JavaPlugin {
    
    private static SessionTracker instance;
    private ConfigManager configManager;
    private LanguageManager languageManager;
    private SessionManager sessionManager;
    private ViolationManager violationManager;
    private WarnManager warnManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        configManager = new ConfigManager(this);
        languageManager = new LanguageManager(this);
        sessionManager = new SessionManager(this);
        violationManager = new ViolationManager(this);
        warnManager = new WarnManager(this);
        
        getCommand("stcp").setExecutor(new MainCommand(this));
        getServer().getPluginManager().registerEvents(new InteractionListener(this), this);
        
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIExpansion(this).register();
            getLogger().info(languageManager.getMessage("console.placeholderapi_hooked"));
        }
        
        getLogger().info(languageManager.getMessage("console.plugin_enabled"));
    }
    
    @Override
    public void onDisable() {
        getLogger().info(languageManager.getMessage("console.plugin_disabled"));
    }
    
    public void reload() {
        configManager.reload();
        languageManager.reload();
        getLogger().info(languageManager.getMessage("commands.reloaded"));
    }
    
    public static SessionTracker getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public LanguageManager getLanguageManager() { return languageManager; }
    public SessionManager getSessionManager() { return sessionManager; }
    public ViolationManager getViolationManager() { return violationManager; }
    public WarnManager getWarnManager() { return warnManager; }
}
