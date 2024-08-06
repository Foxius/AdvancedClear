package com.saikonohack;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerInteractListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND) {
            AdvancedClear plugin = (AdvancedClear) event.getPlayer().getServer().getPluginManager().getPlugin("AdvancedClear");
            if (plugin != null) {
                plugin.cancelCleanup();
                event.getPlayer().sendMessage(ChatColor.GOLD + "[AdvancedClear]" + ChatColor.GREEN + "Очистка предметов была отменена!");
            }
        }
    }
}