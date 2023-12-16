package org.h0tkinss.h0t_quests.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.h0tkinss.h0t_quests.H0t_quests;
import org.h0tkinss.h0t_quests.event.PlayerGreetEvent;



public class PlayerGreetListener implements Listener {
    private final H0t_quests plugin;

    public PlayerGreetListener(H0t_quests plugin){
        this.plugin = plugin;
    }
    @EventHandler
    public void onChatMessage(PlayerChatEvent event) {
        String message = event.getMessage().toLowerCase(); // Get the chat message in lowercase
        Player sender = event.getPlayer();
        sender.sendMessage(event.getMessage());
        // Check if the message contains a greeting word
        if (message.contains("hello") || message.contains("hi")) {
            sender.sendMessage("Zawiera przywitanie");
            // Check if the message contains any newly joined player's nickname
            for (String player : plugin.getNewlyJoinedPlayers()) {
                sender.sendMessage(player.toLowerCase());
                if (message.contains(player.toLowerCase())) {
                    // Call an event to handle the greeting
                    sender.sendMessage("Znaleziono nowego gracza, wykonywanie eventu");
                    PlayerGreetEvent e = new PlayerGreetEvent(sender, player);
                    plugin.getServer().getPluginManager().callEvent(e);
                    break;
                }
            }
        }
    }
}
