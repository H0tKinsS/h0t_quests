package org.h0tkinss.h0t_quests;

import org.bukkit.Bukkit;

import java.util.*;

public class GreetManager {
    private final H0t_quests plugin;
    private Map<String, List<String>> newlyJoinedPlayers;
    public GreetManager(H0t_quests plugin){
        this.plugin = plugin;
        newlyJoinedPlayers = new HashMap<>();
    }    public Map<String, List<String>> getNewlyJoinedPlayers() {
        return newlyJoinedPlayers;
    }
    public List<String> getNewlyJoinedPlayersList() {
        List<String> players = new ArrayList<>();
        newlyJoinedPlayers.forEach((key, value) -> {
            players.add(key);
        });
        return players;
    }
    public boolean playerAlreadyGreeted(String greeter, String greeted){
        return newlyJoinedPlayers.get(greeted).contains(greeter);
    }
    public void addNewlyJoinedPlayerGreeter(String greeter, String player) {
        List<String> greetersList = newlyJoinedPlayers.get(player);
        greetersList.add(greeter);
        Bukkit.getLogger().info("Gracz " + greeter + " dodany do listy witających gracza " + player);
    }

    public void addNewlyJoinedPlayer(String player) {

        newlyJoinedPlayers.put(player, new ArrayList<>());
        Bukkit.getLogger().info("Dodano gracza " + player + " do nowych graczy");
    }
    public void removeNewlyJoinedPlayer(String player) {
        newlyJoinedPlayers.remove(player);
        Bukkit.getLogger().info("Usunięto gracza " + player + " z nowych graczy");
    }
}
