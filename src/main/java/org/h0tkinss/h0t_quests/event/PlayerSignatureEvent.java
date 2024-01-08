package org.h0tkinss.h0t_quests.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.h0tkinss.h0t_quests.Signature;

public class PlayerSignatureEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;
    private final Player player;
    private final Signature signature;
    private final ItemStack item;
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    @Override
    public void setCancelled(boolean bln) {
        this.cancelled = bln;
    }

    public Player getPlayer() {
        return player;
    }
    public ItemStack getItem() {
        return item;
    }
    public Signature getSignature() {
        return signature;
    }


    public PlayerSignatureEvent(Player player, Signature signature, ItemStack item){
        this.player = player;
        this.signature = signature;
        this.item = item;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
