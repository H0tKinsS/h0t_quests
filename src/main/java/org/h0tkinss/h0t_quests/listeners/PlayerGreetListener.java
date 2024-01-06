package org.h0tkinss.h0t_quests.listeners;


import dev.revivalo.dailyrewards.api.events.PlayerClaimRewardEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;
import org.h0tkinss.h0t_quests.GreetManager;
import org.h0tkinss.h0t_quests.H0t_quests;
import org.h0tkinss.h0t_quests.event.PlayerGreetEvent;

import java.util.List;
import java.util.logging.Logger;


public class PlayerGreetListener implements Listener {
    private final H0t_quests plugin;
    private final Logger logger;
    private final FileConfiguration config;
    private final GreetManager greetManager;

    public PlayerGreetListener(H0t_quests plugin){
        this.plugin = plugin;
        this.greetManager = plugin.getGreetManager();
        this.logger = plugin.getLogger();
        this.config = plugin.getConfig();
    }    @EventHandler
    public void onPlayerGreet(PlayerGreetEvent e){
        Bukkit.getLogger().info("Wykonano event PlayerGreetEvent przez " + e.getGreeter().getName());
    }
    @EventHandler
    public void onPlayerClaimReward(PlayerClaimRewardEvent e){
        Bukkit.getLogger().info("Gracz" + e.getClaimer().getName() + " odebrał " + e.getClaimedReward().toString() + ".");
    }
    @EventHandler
    public void onChatMessage(AsyncChatEvent event) {
        String message = event.message().toString().toLowerCase(); // Get the chat message in lowercase
        Player sender = event.getPlayer();
        if(greetManager.getNewlyJoinedPlayers().isEmpty()) {
            logger.info("Brak nowych graczy");
            return;
        }
        List<String> newPlayers = greetManager.getNewlyJoinedPlayersList();
        newPlayers.forEach((n) -> {
            logger.info(n);
            if(message.contains(n.toLowerCase())){
                logger.info("Nowy gracz w wiadomosci");
                for(String greeting : config.getStringList("greet-valid-words")){
                    if(message.contains(greeting) && !sender.getName().equalsIgnoreCase(n)){
                        logger.info("Already Greeted: "+greetManager.playerAlreadyGreeted(sender.getName(), n));
                        if(greetManager.playerAlreadyGreeted(sender.getName(), n)) {
                            logger.info(String.format("Gracz %1$s przywitał już gracza %2$s", sender.getName(), n));
                            return;
                        }
                        logger.info("Wiadomosc zawiera przywitanie");
                        BukkitScheduler scheduler = plugin.getServer().getScheduler();
                        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                greetManager.addNewlyJoinedPlayerGreeter(sender.getName(), n);
                                PlayerGreetEvent e = new PlayerGreetEvent(sender, n);
                                plugin.getServer().getPluginManager().callEvent(e);
                                if (!config.getString("greeter-message").isEmpty()) sender.sendMessage(config.getString("greeter-message"));
                                ConsoleCommandSender console = plugin.getServer().getConsoleSender();
                                for (String cmd : config.getStringList("commands")) {
                                    Bukkit.dispatchCommand(console, cmd.replaceAll("%player%", sender.getName()));
                                }
                            }
                        });

                        return;
                    }
                }
            }
        });


        // Check if the message contains a greeting word
        /*for (String greeting : greetings) {
            if (message.contains(greeting)) {
                // Check if the message contains any newly joined player's nickname
                for (String nowyGracz : plugin.getNewlyJoinedPlayers().keySet()) {
                    if (message.contains(nowyGracz.toLowerCase())) {
                        if (sender.getName().equalsIgnoreCase(nowyGracz)) {
                            return;
                        }
                        // Call an event to handle the greeting
                        if (plugin.getNewlyJoinedPlayers().get(nowyGracz).contains(sender.getName())) {
                            plugin.addNewlyJoinedPlayerGreeter(sender.getName(), nowyGracz);
                            PlayerGreetEvent e = new PlayerGreetEvent(sender, nowyGracz);
                            plugin.getServer().getPluginManager().callEvent(e);
                        }
                        break;
                    }
                }
            }
        }*/
    }
}
