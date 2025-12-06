package com.kingironman.sethome.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * InventoryHolder for admin menu (viewing all players)
 */
public class AdminMenuHolder implements InventoryHolder {
    private final String targetPlayerName;
    
    public AdminMenuHolder() {
        this.targetPlayerName = null;
    }
    
    public AdminMenuHolder(String targetPlayerName) {
        this.targetPlayerName = targetPlayerName;
    }
    
    public String getTargetPlayerName() {
        return targetPlayerName;
    }
    
    @Override
    public Inventory getInventory() {
        return null;
    }
}
