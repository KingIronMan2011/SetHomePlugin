package com.kingironman.sethome.gui;

import com.kingironman.sethome.SetHome;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class HomeGui {

    public static final String TITLE_PREFIX = ChatColor.GREEN + "Your Homes";
    public static final String ADMIN_TITLE = ChatColor.DARK_PURPLE + "Admin Menu";
    public static final String CONFIRM_TITLE = ChatColor.RED + "Confirm Deletion";

    /**
     * Open the homes GUI for viewer. If target is non-null and different, shows target's homes (admin mode).
     */
    public static void open(Player viewer, Player target) {
        Player subject = (target != null) ? target : viewer;
        List<String> homes = SetHome.getInstance().homeUtils.getHomeNames(subject);
        
        // Calculate size - at least 27 for decoration, round up to multiple of 9
        int homeSlots = homes.size();
        int size = 27; // Minimum size with decoration
        if (homeSlots > 18) {
            size = ((homeSlots + 8) / 9) * 9 + 9; // Add extra row for bottom decoration
            if (size > 54) size = 54;
        }

        boolean isAdminView = (target != null && target != viewer);
        String title = TITLE_PREFIX + (isAdminView ? " - " + subject.getName() : "");
        
        Inventory inv = Bukkit.createInventory(
            isAdminView ? new AdminMenuHolder(subject.getName()) : new PlayerMenuHolder(), 
            size, 
            title
        );

        // Add decorative glass panes on top row
        ItemStack glassPane = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glassPane.getItemMeta();
        glassMeta.setDisplayName(" ");
        glassPane.setItemMeta(glassMeta);
        
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, glassPane);
        }

        // Add homes starting from slot 10 (second row, second slot)
        int slot = 10;
        for (String home : homes) {
            if (slot >= size - 9) break; // Leave room for bottom row
            
            // Skip glass pane border slots
            if (slot % 9 == 0 || slot % 9 == 8) {
                ItemStack borderPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                ItemMeta borderMeta = borderPane.getItemMeta();
                borderMeta.setDisplayName(" ");
                borderPane.setItemMeta(borderMeta);
                inv.setItem(slot, borderPane);
                slot++;
            }
            
            if (slot >= size - 9) break;
            
            ItemStack item = new ItemStack(Material.RED_BED);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + home);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Left-click to teleport");
            lore.add(ChatColor.RED + "Right-click to delete");
            if (isAdminView) {
                lore.add("");
                lore.add(ChatColor.GOLD + "Admin Mode: " + subject.getName());
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(slot, item);
            slot++;
        }

        // Fill remaining middle slots with glass panes
        for (int i = 9; i < size - 9; i++) {
            if (inv.getItem(i) == null) {
                if (i % 9 == 0 || i % 9 == 8) {
                    ItemStack borderPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                    ItemMeta borderMeta = borderPane.getItemMeta();
                    borderMeta.setDisplayName(" ");
                    borderPane.setItemMeta(borderMeta);
                    inv.setItem(i, borderPane);
                } else {
                    inv.setItem(i, new ItemStack(Material.AIR));
                }
            }
        }

        // Add decorative glass panes on bottom row
        for (int i = size - 9; i < size; i++) {
            inv.setItem(i, glassPane);
        }

        // Add close button in the center of bottom row
        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        closeMeta.setDisplayName(ChatColor.RED + "Close");
        closeButton.setItemMeta(closeMeta);
        inv.setItem(size - 5, closeButton);

        viewer.openInventory(inv);
    }

    /**
     * Open admin menu showing all players
     */
    public static void openAdminMenu(Player admin) {
        if (!admin.hasPermission("sethome.admin") && !admin.isOp()) {
            admin.sendMessage(ChatColor.RED + "You don't have permission to use the admin menu.");
            return;
        }

        List<OfflinePlayer> players = new ArrayList<>(Arrays.asList(Bukkit.getOfflinePlayers()));
        players.sort(Comparator.comparing(p -> p.getName() != null ? p.getName() : ""));

        int size = 54; // Max size for player heads
        Inventory inv = Bukkit.createInventory(new AdminMenuHolder(), size, ADMIN_TITLE);

        // Add decorative glass panes on top row
        ItemStack glassPane = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glassPane.getItemMeta();
        glassMeta.setDisplayName(" ");
        glassPane.setItemMeta(glassMeta);
        
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, glassPane);
        }

        // Add player heads
        int slot = 10;
        int count = 0;
        for (OfflinePlayer player : players) {
            if (count >= 35) break; // Limit to 35 players (5 rows of 7)
            if (player.getName() == null) continue;
            
            // Skip border slots
            if (slot % 9 == 0 || slot % 9 == 8) {
                ItemStack borderPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                ItemMeta borderMeta = borderPane.getItemMeta();
                borderMeta.setDisplayName(" ");
                borderPane.setItemMeta(borderMeta);
                inv.setItem(slot, borderPane);
                slot++;
            }
            
            if (slot >= 45) break; // Stop before last row
            
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwningPlayer(player);
            skullMeta.setDisplayName(ChatColor.YELLOW + player.getName());
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Click to view homes");
            skullMeta.setLore(lore);
            skull.setItemMeta(skullMeta);
            inv.setItem(slot, skull);
            slot++;
            count++;
        }

        // Fill remaining slots with glass panes
        for (int i = 9; i < 45; i++) {
            if (inv.getItem(i) == null) {
                if (i % 9 == 0 || i % 9 == 8) {
                    ItemStack borderPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                    ItemMeta borderMeta = borderPane.getItemMeta();
                    borderMeta.setDisplayName(" ");
                    borderPane.setItemMeta(borderMeta);
                    inv.setItem(i, borderPane);
                }
            }
        }

        // Add decorative glass panes on bottom row
        for (int i = 45; i < 54; i++) {
            inv.setItem(i, glassPane);
        }

        // Add close button
        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        closeMeta.setDisplayName(ChatColor.RED + "Close");
        closeButton.setItemMeta(closeMeta);
        inv.setItem(49, closeButton);

        admin.openInventory(inv);
    }

    /**
     * Open confirmation dialog before deleting a home
     */
    public static void openConfirmationDialog(Player viewer, String homeName, String targetPlayerName) {
        Inventory inv = Bukkit.createInventory(
            new ConfirmationMenuHolder(homeName, targetPlayerName), 
            27, 
            CONFIRM_TITLE
        );

        // Fill with red glass panes
        ItemStack redPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta redMeta = redPane.getItemMeta();
        redMeta.setDisplayName(" ");
        redPane.setItemMeta(redMeta);
        
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, redPane);
        }

        // Confirm button (green wool)
        ItemStack confirmButton = new ItemStack(Material.GREEN_WOOL);
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        confirmMeta.setDisplayName(ChatColor.GREEN + "Confirm Delete");
        List<String> confirmLore = new ArrayList<>();
        confirmLore.add(ChatColor.GRAY + "Delete: " + ChatColor.YELLOW + homeName);
        if (targetPlayerName != null && !targetPlayerName.equals(viewer.getName())) {
            confirmLore.add(ChatColor.GRAY + "Player: " + ChatColor.YELLOW + targetPlayerName);
        }
        confirmMeta.setLore(confirmLore);
        confirmButton.setItemMeta(confirmMeta);
        inv.setItem(11, confirmButton);

        // Cancel button (red wool)
        ItemStack cancelButton = new ItemStack(Material.RED_WOOL);
        ItemMeta cancelMeta = cancelButton.getItemMeta();
        cancelMeta.setDisplayName(ChatColor.RED + "Cancel");
        List<String> cancelLore = new ArrayList<>();
        cancelLore.add(ChatColor.GRAY + "Keep the home");
        cancelMeta.setLore(cancelLore);
        cancelButton.setItemMeta(cancelMeta);
        inv.setItem(15, cancelButton);

        // Info item in center
        ItemStack infoItem = new ItemStack(Material.RED_BED);
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.setDisplayName(ChatColor.YELLOW + homeName);
        List<String> infoLore = new ArrayList<>();
        infoLore.add(ChatColor.RED + "Are you sure you want to");
        infoLore.add(ChatColor.RED + "delete this home?");
        infoMeta.setLore(infoLore);
        infoItem.setItemMeta(infoMeta);
        inv.setItem(13, infoItem);

        viewer.openInventory(inv);
    }

}
