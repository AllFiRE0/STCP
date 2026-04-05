package com.allfire.sessiontracker.commands;

import com.allfire.sessiontracker.SessionTracker;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class MainCommand implements CommandExecutor, TabCompleter {
    
    private final SessionTracker plugin;
    private final NotificationsCommand notificationsCommand;
    private final ViewCommand viewCommand;
    private final WarnCommand warnCommand;
    private final InfoCommand infoCommand;
    
    public MainCommand(SessionTracker plugin) {
        this.plugin = plugin;
        this.notificationsCommand = new NotificationsCommand(plugin);
        this.viewCommand = new ViewCommand(plugin);
        this.warnCommand = new WarnCommand(plugin);
        this.infoCommand = new InfoCommand(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "reload":
                return handleReload(sender);
            case "notifications":
            case "notify":
                return notificationsCommand.execute(sender, args);
            case "view":
                return viewCommand.execute(sender, args);
            case "warn":
                return warnCommand.execute(sender, args);
            case "info":
                return infoCommand.execute(sender, args);
            default:
                sendHelp(sender);
                return true;
        }
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6=== SessionTrackerCP Команды ===");
        sender.sendMessage("§e/stcp reload §7- Перезагрузить плагин");
        sender.sendMessage("§e/stcp notifications §7- Вкл/Выкл уведомления");
        sender.sendMessage("§e/stcp view <игрок> §7- Просмотр нарушений");
        sender.sendMessage("§e/stcp warn add/remove <1-100> [игрок] §7- Предупреждения");
        sender.sendMessage("§e/stcp info [игрок] §7- Информация об игроке");
    }
    
    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("sessiontracker.admin")) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("commands.no_permission"));
            return true;
        }
        plugin.reload();
        sender.sendMessage(plugin.getLanguageManager().getMessage("commands.reloaded"));
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "notifications", "view", "warn", "info").stream()
                .filter(c -> c.startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
