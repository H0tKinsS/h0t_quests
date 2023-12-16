package org.h0tkinss.h0t_quests.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerGreetEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;
    private final String joined;
    private final String greeter;
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    @Override
    public void setCancelled(boolean bln) {
        this.cancelled = bln;
    }

    public Player getGreeter() {
        return Bukkit.getServer().getPlayer(greeter);
    }


    public PlayerGreetEvent(Player greeter, String joined){
        this.greeter = greeter.getName();
        this.joined = joined;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
