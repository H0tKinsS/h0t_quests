package org.h0tkinss.h0t_quests;

import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskTypeManager;
import com.leonardobishop.quests.common.player.QPlayerManager;
import com.leonardobishop.quests.common.plugin.Quests;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.h0tkinss.h0t_quests.listeners.OnPlayerGreet;
import org.h0tkinss.h0t_quests.listeners.PlayerGreetListener;
import org.h0tkinss.h0t_quests.listeners.ServerJoinEvent;
import org.h0tkinss.h0t_quests.tasks.GreetPlayersCustomTask;

import java.util.ArrayList;
import java.util.List;

public final class H0t_quests extends JavaPlugin {

    public QPlayerManager getPlayerManager() {
        return qPlayerManager;
    }

    private QPlayerManager qPlayerManager;
    private List<String> newlyJoinedPlayers;

    public List<String> getNewlyJoinedPlayers() {
        return newlyJoinedPlayers;
    }

    public void addNewlyJoinedPlayer(String player) {

        newlyJoinedPlayers.add(player);
        Bukkit.getLogger().info("Dodano gracza " + player + " do nowych graczy");
    }
    public void removeNewlyJoinedPlayer(String player) {
        newlyJoinedPlayers.remove(player);
        Bukkit.getLogger().info("Usunięto gracza " + player + " do nowych graczy");
    }
    @Override
    public void onEnable() {

        this.newlyJoinedPlayers = new ArrayList<>();

        GreetPlayersCustomTask greetPlayersCustomTask = new GreetPlayersCustomTask(this);
        getServer().getPluginManager().registerEvents(new PlayerGreetListener(this), this);
        getServer().getPluginManager().registerEvents(new ServerJoinEvent(this), this);
        getServer().getPluginManager().registerEvents(new OnPlayerGreet(this), this);

        Quests questsPlugin = (Quests) Bukkit.getPluginManager().getPlugin("Quests");
        BukkitTaskTypeManager taskTypeManager = (BukkitTaskTypeManager) questsPlugin.getTaskTypeManager();

        taskTypeManager.registerTaskType(greetPlayersCustomTask);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
