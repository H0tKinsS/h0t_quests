package org.h0tkinss.h0t_quests.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.h0tkinss.h0t_quests.H0t_quests;
import org.h0tkinss.h0t_quests.event.PlayerGreetEvent;

public class OnPlayerGreet implements Listener {
    H0t_quests plugin;
    public OnPlayerGreet(H0t_quests plugin){
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerGreet(PlayerGreetEvent e){
        Bukkit.getLogger().info("Wykonano event PlayerGreetEvent przez " + e.getGreeter().getName());
    }
}
