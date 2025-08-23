package com.kingironman.sethome.gui;

import com.kingironman.sethome.SetHome;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class HomeGuiListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView() == null) return;
        if (e.getView().getTitle() == null) return;
        if (!e.getView().getTitle().startsWith(HomeGui.TITLE_PREFIX)) return;
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player clicker = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
        String homeName = ChatColor.stripColor(item.getItemMeta().getDisplayName());

        // Determine target player (if admin viewing another player's GUI)
        Player target = clicker;
        String title = e.getView().getTitle();
        if (title.contains(" - ")) {
            String suffix = title.substring(title.indexOf(" - ") + 3);
            Player p = org.bukkit.Bukkit.getPlayerExact(suffix);
            if (p != null && p.isOnline()) target = p;
        }

        // Left click = teleport
        switch (e.getClick()) {
            case LEFT:
            case SHIFT_LEFT:
                if (!SetHome.getInstance().homeUtils.homeExists(target, homeName, true)) return;
                Player subject = (target == clicker) ? clicker : target;
                org.bukkit.Location loc = SetHome.getInstance().homeUtils.getPlayerHome(subject, homeName);
                if (loc == null) return;
                clicker.closeInventory();
                clicker.teleport(loc);
                clicker.sendMessage(ChatColor.GREEN + "Teleported to " + subject.getName() + "'s home '" + homeName + "'.");
                break;
            case RIGHT:
            case SHIFT_RIGHT:
                // Right click = delete (only allowed if clicker is owner or is OP)
                if (target.equals(clicker) || clicker.isOp()) {
                    SetHome.getInstance().homeUtils.deletePlayerHome(target, homeName);
                    clicker.sendMessage(ChatColor.GREEN + "Deleted home '" + homeName + "' for " + target.getName());
                    clicker.closeInventory();
                } else {
                    clicker.sendMessage(ChatColor.RED + "You don't have permission to delete that home.");
                }
                break;
            default:
                break;
        }
    }

}
