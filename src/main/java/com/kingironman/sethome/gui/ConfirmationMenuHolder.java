package com.kingironman.sethome.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * InventoryHolder for confirmation dialog
 */
public class ConfirmationMenuHolder implements InventoryHolder {
    private final String homeName;
    private final String targetPlayerName;
    
    public ConfirmationMenuHolder(String homeName, String targetPlayerName) {
        this.homeName = homeName;
        this.targetPlayerName = targetPlayerName;
    }
    
    public String getHomeName() {
        return homeName;
    }
    
    public String getTargetPlayerName() {
        return targetPlayerName;
    }
    
    @Override
    public Inventory getInventory() {
        return null;
    }
}
