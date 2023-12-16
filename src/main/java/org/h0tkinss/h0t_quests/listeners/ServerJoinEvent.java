package org.h0tkinss.h0t_quests.listeners;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.h0tkinss.h0t_quests.H0t_quests;

public class ServerJoinEvent implements Listener {
    private final H0t_quests plugin;

    public ServerJoinEvent(H0t_quests plugin){
        this.plugin = plugin;
    }
    @EventHandler
    public void onServerJoin(PlayerJoinEvent event){
        Player joinedPlayer = event.getPlayer();
        plugin.addNewlyJoinedPlayer(joinedPlayer.getName());
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskLater(plugin, () -> {
            plugin.removeNewlyJoinedPlayer(joinedPlayer.getName());
        }, 20L * 10L /*<-- the delay */);
    }
}
