package com.kingironman.sethome.gui;

import com.kingironman.sethome.SetHome;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class HomeGuiListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView() == null) return;
        if (!(e.getWhoClicked() instanceof Player)) return;
        
        Player clicker = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        InventoryHolder holder = inv.getHolder();
        ItemStack item = e.getCurrentItem();
        
        // Handle player homes menu
        if (holder instanceof PlayerMenuHolder) {
            e.setCancelled(true);
            handlePlayerMenu(clicker, e, item);
            return;
        }
        
        // Handle admin menu
        if (holder instanceof AdminMenuHolder) {
            e.setCancelled(true);
            handleAdminMenu(clicker, e, item, (AdminMenuHolder) holder);
            return;
        }
        
        // Handle confirmation dialog
        if (holder instanceof ConfirmationMenuHolder) {
            e.setCancelled(true);
            handleConfirmationMenu(clicker, e, item, (ConfirmationMenuHolder) holder);
            return;
        }
    }

    private void handlePlayerMenu(Player clicker, InventoryClickEvent e, ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return;
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
        
        String displayName = item.getItemMeta().getDisplayName();
        
        // Close button
        if (item.getType() == Material.BARRIER && displayName.contains("Close")) {
            clicker.closeInventory();
            return;
        }
        
        // Glass panes - ignore
        if (item.getType().toString().contains("GLASS_PANE")) {
            return;
        }
        
        // Home item (RED_BED)
        if (item.getType() == Material.RED_BED) {
            String homeName = ChatColor.stripColor(displayName);
            
            switch (e.getClick()) {
                case LEFT:
                case SHIFT_LEFT:
                    // Teleport to home
                    if (!SetHome.getInstance().homeUtils.homeExists(clicker, homeName, true)) {
                        clicker.sendMessage(ChatColor.RED + "Home not found: " + homeName);
                        return;
                    }
                    org.bukkit.Location loc = SetHome.getInstance().homeUtils.getPlayerHome(clicker, homeName);
                    if (loc == null) {
                        clicker.sendMessage(ChatColor.RED + "Could not load home location.");
                        return;
                    }
                    clicker.closeInventory();
                    clicker.teleport(loc);
                    clicker.sendMessage(ChatColor.GREEN + "Teleported to home '" + homeName + "'.");
                    break;
                    
                case RIGHT:
                case SHIFT_RIGHT:
                    // Open confirmation dialog
                    clicker.closeInventory();
                    HomeGui.openConfirmationDialog(clicker, homeName, clicker.getName());
                    break;
                    
                default:
                    break;
            }
        }
    }

    private void handleAdminMenu(Player admin, InventoryClickEvent e, ItemStack item, AdminMenuHolder holder) {
        if (item == null || item.getType() == Material.AIR) return;
        if (!item.hasItemMeta()) return;
        
        // Close button
        if (item.getType() == Material.BARRIER) {
            admin.closeInventory();
            return;
        }
        
        // Glass panes - ignore
        if (item.getType().toString().contains("GLASS_PANE")) {
            return;
        }
        
        // Player head clicked
        if (item.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            if (meta.getOwningPlayer() != null) {
                OfflinePlayer target = meta.getOwningPlayer();
                if (target.isOnline()) {
                    admin.closeInventory();
                    HomeGui.open(admin, (Player) target);
                } else {
                    admin.sendMessage(ChatColor.RED + "Player " + target.getName() + " is not online.");
                }
            }
            return;
        }
        
        // Home item in admin view (viewing a specific player's homes)
        if (item.getType() == Material.RED_BED && holder.getTargetPlayerName() != null) {
            String homeName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            String targetName = holder.getTargetPlayerName();
            Player target = org.bukkit.Bukkit.getPlayerExact(targetName);
            
            if (target == null) {
                admin.sendMessage(ChatColor.RED + "Player " + targetName + " is not online.");
                return;
            }
            
            switch (e.getClick()) {
                case LEFT:
                case SHIFT_LEFT:
                    // Teleport to target's home
                    if (!SetHome.getInstance().homeUtils.homeExists(target, homeName, true)) {
                        admin.sendMessage(ChatColor.RED + "Home not found: " + homeName);
                        return;
                    }
                    org.bukkit.Location loc = SetHome.getInstance().homeUtils.getPlayerHome(target, homeName);
                    if (loc == null) {
                        admin.sendMessage(ChatColor.RED + "Could not load home location.");
                        return;
                    }
                    admin.closeInventory();
                    admin.teleport(loc);
                    admin.sendMessage(ChatColor.GREEN + "Teleported to " + targetName + "'s home '" + homeName + "'.");
                    break;
                    
                case RIGHT:
                case SHIFT_RIGHT:
                    // Open confirmation dialog for deletion (admin)
                    if (!admin.isOp() && !admin.hasPermission("sethome.admin")) {
                        admin.sendMessage(ChatColor.RED + "You don't have permission to delete homes.");
                        return;
                    }
                    admin.closeInventory();
                    HomeGui.openConfirmationDialog(admin, homeName, targetName);
                    break;
                    
                default:
                    break;
            }
        }
    }

    private void handleConfirmationMenu(Player clicker, InventoryClickEvent e, ItemStack item, ConfirmationMenuHolder holder) {
        if (item == null || item.getType() == Material.AIR) return;
        if (!item.hasItemMeta()) return;
        
        String homeName = holder.getHomeName();
        String targetPlayerName = holder.getTargetPlayerName();
        
        // Confirm button (green wool)
        if (item.getType() == Material.GREEN_WOOL) {
            clicker.closeInventory();
            
            // Determine the target player
            Player targetPlayer;
            if (targetPlayerName != null && !targetPlayerName.equals(clicker.getName())) {
                // Admin deleting another player's home
                targetPlayer = org.bukkit.Bukkit.getPlayerExact(targetPlayerName);
                if (targetPlayer == null) {
                    clicker.sendMessage(ChatColor.RED + "Player " + targetPlayerName + " is not online.");
                    return;
                }
                
                if (!clicker.isOp() && !clicker.hasPermission("sethome.admin")) {
                    clicker.sendMessage(ChatColor.RED + "You don't have permission to delete other players' homes.");
                    return;
                }
            } else {
                // Player deleting their own home
                targetPlayer = clicker;
            }
            
            // Delete the home
            SetHome.getInstance().homeUtils.deletePlayerHome(targetPlayer, homeName);
            
            if (targetPlayer.equals(clicker)) {
                clicker.sendMessage(ChatColor.GREEN + "Deleted home '" + homeName + "'.");
            } else {
                clicker.sendMessage(ChatColor.GREEN + "Deleted home '" + homeName + "' for " + targetPlayer.getName() + ".");
            }
            return;
        }
        
        // Cancel button (red wool)
        if (item.getType() == Material.RED_WOOL) {
            clicker.closeInventory();
            clicker.sendMessage(ChatColor.GRAY + "Deletion cancelled.");
            return;
        }
        
        // Glass panes or info item - do nothing
    }

}
