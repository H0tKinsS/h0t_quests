package org.h0tkinss.h0t_quests.listeners;


import fr.xephi.authme.events.RegisterEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitScheduler;
import org.h0tkinss.h0t_quests.GreetManager;
import org.h0tkinss.h0t_quests.H0t_quests;


public class ServerJoinEvent implements Listener {
    private final H0t_quests plugin;
    private final GreetManager greetManager;

    public ServerJoinEvent(H0t_quests plugin){
        this.plugin = plugin;
        this.greetManager = plugin.getGreetManager();
    }
    @EventHandler
    public void onServerJoin(RegisterEvent event){
        Player joinedPlayer = event.getPlayer();
        greetManager.addNewlyJoinedPlayer(joinedPlayer.getName());
        //String msg = plugin.getConfig().getString("new-player-message");
        //Bukkit.getServer().broadcastMessage(msg.replaceAll("%player%", joinedPlayer.getName()).replaceAll("&", "§"));
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskLater(plugin, () -> greetManager.removeNewlyJoinedPlayer(joinedPlayer.getName()), 20L * 10L /*<-- the delay */);
    }
//    @EventHandler
//    public void onDungeonEnd(DungeonEndEvent event){
//        List<Player> players = event.getPlayers();
//        Bukkit.getLogger().info("" + players.toString() + " ukonczyli " + event.getDungeon().getWorldName() + " " + event.getDungeon().getDisplayName());
//    }

    @EventHandler
    public void onInventoryClick(PrepareAnvilEvent event) {
        if (event.getResult() != null) {
            ItemStack item = event.getResult();
            ItemMeta meta = item.getItemMeta();
            NamespacedKey key = new NamespacedKey(plugin, "CustomItem");
            Integer customTagValue = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
            if ((customTagValue != null && customTagValue == 1)) {
                event.setResult(null);
            }
        }
    }
}
