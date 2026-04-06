package com.allfire.sessiontracker.utils;

import com.allfire.sessiontracker.SessionTracker;

import java.util.Map;

public class ConditionChecker {
    
    private final SessionTracker plugin;
    
    public ConditionChecker(SessionTracker plugin) {
        this.plugin = plugin;
    }
    
    public boolean checkCondition(String condition, Map<String, String> placeholders) {
        if (condition == null || condition.isEmpty()) {
            return true;
        }
        
        String processedCondition = condition;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            processedCondition = processedCondition.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        
        try {
            if (processedCondition.contains(">=")) {
                return compare(processedCondition, ">=");
            } else if (processedCondition.contains("<=")) {
                return compare(processedCondition, "<=");
            } else if (processedCondition.contains("!=")) {
                return compare(processedCondition, "!=");
            } else if (processedCondition.contains("==")) {
                return compare(processedCondition, "==");
            } else if (processedCondition.contains(">")) {
                return compare(processedCondition, ">");
            } else if (processedCondition.contains("<")) {
                return compare(processedCondition, "<");
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Ошибка парсинга условия: " + processedCondition);
            return false;
        }
        
        return false;
    }
    
    private boolean compare(String condition, String operator) {
        String[] parts;
        if (operator.equals(">=")) {
            parts = condition.split(">=");
        } else if (operator.equals("<=")) {
            parts = condition.split("<=");
        } else if (operator.equals("!=")) {
            parts = condition.split("!=");
        } else if (operator.equals("==")) {
            parts = condition.split("==");
        } else if (operator.equals(">")) {
            parts = condition.split(">");
        } else if (operator.equals("<")) {
            parts = condition.split("<");
        } else {
            return false;
        }
        
        if (parts.length != 2) return false;
        
        double left = Double.parseDouble(parts[0].trim());
        double right = Double.parseDouble(parts[1].trim());
        
        switch (operator) {
            case ">=": return left >= right;
            case "<=": return left <= right;
            case "!=": return left != right;
            case "==": return left == right;
            case ">": return left > right;
            case "<": return left < right;
            default: return false;
        }
    }
}
