package com.kingironman.sethome.gui;

import com.kingironman.sethome.SetHome;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class HomeGui {

    public static final String TITLE_PREFIX = ChatColor.GREEN + "Your Homes";

    /**
     * Open the homes GUI for viewer. If target is non-null and different, shows target's homes (admin mode).
     */
    public static void open(Player viewer, Player target) {
        Player subject = (target != null) ? target : viewer;
        List<String> homes = SetHome.getInstance().homeUtils.getHomeNames(subject);
        int size = 9;
        while (size < homes.size()) size += 9; // round up to nearest row
        if (size == 0) size = 9;

        Inventory inv = Bukkit.createInventory(null, size, TITLE_PREFIX + (target != null && target != viewer ? " - " + subject.getName() : ""));

        for (int i = 0; i < homes.size() && i < size; i++) {
            String home = homes.get(i);
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + home);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Left-click to teleport");
            lore.add(ChatColor.GRAY + "Right-click to delete (if allowed)");
            if (target != null && target != viewer) lore.add(ChatColor.GRAY + "Admin view: will act on " + subject.getName());
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }

        viewer.openInventory(inv);
    }

}
