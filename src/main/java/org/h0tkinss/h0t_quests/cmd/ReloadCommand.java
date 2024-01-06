package org.h0tkinss.h0t_quests.cmd;


import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.h0tkinss.h0t_quests.H0t_quests;
import org.jetbrains.annotations.NotNull;
public class ReloadCommand implements CommandExecutor {
    private H0t_quests plugin;
    public ReloadCommand(H0t_quests plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            if (sender instanceof Player) {
                sender.sendMessage("Użyj /h0t_quests <reload>");
            } else {
                Bukkit.getLogger().info("Użyj /h0t_quests <reload>");
            }
        } else if (args[0].equalsIgnoreCase("reload")) {
            if (sender instanceof Player) {
                if (plugin.getPerms().has(((Player) sender).getPlayer(), "h0t_quests.admin")) {
                    plugin.reloadConfig();
                } else {
                    sender.sendMessage("Nie masz uprawnień");
                }
            } else {
                plugin.reloadConfig();
            }
        }
        return true;
    }

}
