package org.h0tkinss.h0t_quests.listeners;


import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import fr.xephi.authme.events.RegisterEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
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
//    @EventHandler
//    public void onPlayerSignatureItem(PlayerSignatureEvent e) {
//        Player player = e.getPlayer();
//        if (player.getGameMode().equals(GameMode.CREATIVE)){
//            player.sendMessage("Nie możesz tego zrobić w trybie creative");
//            e.setCancelled(true);
//            return;
//        }
//        player.sendMessage("Pomyślnie podpisano przedmiot!");
//    }
    @EventHandler
    public void onInventoryClick(PrepareAnvilEvent event) {
        if (event.getResult() != null) {
            ItemStack item = event.getResult();
            if (plugin.getSignatureManager().isItemSigned(item)){
                event.setResult(null);
            }
        }
    }
    @EventHandler
    public void onInventoryClick(PrepareItemCraftEvent event) {
        CraftingInventory inv = event.getInventory();

        for (ItemStack item : inv.getStorageContents()) {
            if (plugin.getSignatureManager().isItemSigned(item)) {
                inv.setResult(null);
            }
        }
    }
    @EventHandler
    public void onFurnace(FurnaceBurnEvent e) {
        if (plugin.getSignatureManager().isItemSigned(e.getFuel())) e.setCancelled(true);
    }
    @EventHandler
    public void onFurnaceSource(FurnaceSmeltEvent e) {
        if (plugin.getSignatureManager().isItemSigned(e.getSource())) e.setCancelled(true);
    }
    @EventHandler
    public void onGrindStone(PrepareGrindstoneEvent e) {
        GrindstoneInventory inv = e.getInventory();

        for (ItemStack item : inv.getStorageContents()) {
            if (plugin.getSignatureManager().isItemSigned(item)) {
                inv.setResult(null);
            }
        }
    }
    @EventHandler
    public void onThrow(PlayerLaunchProjectileEvent e) {
        if (plugin.getSignatureManager().isItemSigned(e.getItemStack())) e.setCancelled(true);
    }
    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent e) {
        if (plugin.getSignatureManager().isItemSigned(e.getItem())) e.setCancelled(true);
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (plugin.getSignatureManager().isItemSigned(e.getItemInHand())) e.setCancelled(true);
    }
    @EventHandler
    public void onPlayerInteractEventDrag(InventoryDragEvent e) {
        e.getNewItems().forEach((slot, item) -> {
            if (plugin.getSignatureManager().isItemSigned(e.getOldCursor()) &&
                    !(slot >= e.getView().getTopInventory().getSize())) {
                e.setCancelled(true);
            }
        });

    }
    @EventHandler
    public void onBowShot(EntityShootBowEvent e) {
        if (plugin.getSignatureManager().isItemSigned(e.getConsumable())) {
            e.setConsumeItem(false);
            e.setCancelled(true);
            if (e.getEntity() instanceof Player) {
                Player p = ((Player) e.getEntity()).getPlayer();
                assert p != null;
                p.updateInventory();
                p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Nie możesz wystrzelić tej strzały"));
            }
        }
    }
//    @EventHandler
//    public void onRightClick(PlayerInteractEvent e) {
//        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
//            if (plugin.getSignatureManager().isItemSigned(e.getPlayer().getInventory().getItemInMainHand()))
//                if (!e.getClickedBlock().getBlockData().getMaterial().equals(Material.CHEST)) e.setCancelled(true);
//        }
//    }

}
