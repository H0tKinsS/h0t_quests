package org.h0tkinss.h0t_quests.cmd;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.h0tkinss.h0t_quests.H0t_quests;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.List;

public class SignCommand implements CommandExecutor, TabExecutor {
    private H0t_quests plugin;
    public SignCommand(H0t_quests plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        MiniMessage mm = MiniMessage.miniMessage();
        Player player = (Player) sender;
        if (args.length == 0) {
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "dm open signatures " + player.getName());
            return true;
        }
        if (plugin.getConfig().getString("podpisy." + args[0]) == null) {
            player.sendMessage(mm.deserialize("<red>Nie znaleziono takiego podpisu!"));
            return true;
        }
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType().equals(Material.AIR)) {
            player.sendMessage(mm.deserialize("<red>Nie możesz podpisać powietrza!"));
            return true;
        }
        for (String blacklistedItem : plugin.getConfig().getStringList("podpisy-blacklist")) {
            if (blacklistedItem.startsWith("ANY_")) {
                if (itemStack.getType().toString().contains(blacklistedItem.replaceAll("ANY_", ""))) {
                    player.sendMessage(mm.deserialize("<red>Nie możesz podpisać tego przedmiotu!"));
                    return true;
                }
            } else if (blacklistedItem.equals(itemStack.getType().toString())) {
                player.sendMessage(mm.deserialize("<red>Nie możesz podpisać tego przedmiotu!"));
                return true;
            }
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "CustomItem");
        Integer customTagValue = pdc.get(key, PersistentDataType.INTEGER);
        if (itemMeta.hasDisplayName() || customTagValue != null) {
            player.sendMessage(mm.deserialize("<red>Nie możesz podpisać już tego przedmiotu!"));
            return true;
        }
        String signatureName = args[0];
        if(!(plugin.getPerms().has(player, "podpis." + signatureName))){
            player.sendMessage(mm.deserialize("<red>Nie możesz użyć tego podpisu!"));
            return true;
        }
        double cena = itemStack.getAmount() * plugin.getConfig().getInt("podpisy." + signatureName + ".cost");
        if(!(plugin.getEcon().has(Bukkit.getOfflinePlayer(player.getUniqueId()), cena))) {
            player.sendMessage(mm.deserialize("<red>Nie posiadasz gotówki!"));
            return true;
        }


        List<Component> lore = new ArrayList<>();
        for (String line : plugin.getConfig().getStringList("podpisy." + signatureName + ".lore")) {
            lore.add(mm.deserialize(PlaceholderAPI.setPlaceholders(player, line))
                .decoration(TextDecoration.ITALIC, false));
        }
        Component displayName = mm.deserialize(PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(player.getUniqueId()), plugin.getConfig().getString("podpisy." + signatureName + ".display_name"))).decoration(TextDecoration.ITALIC, false);
        itemMeta.displayName(displayName);

        pdc.set(key, PersistentDataType.INTEGER, 1);

        itemMeta.lore(lore);
        itemStack.setItemMeta(itemMeta);
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 10, 29);
        plugin.getEcon().withdrawPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), cena);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        final List<String> completions = new ArrayList<>();
        //StringUtil.copyPartialMatches(args[0], plugin.getConfig().getConfigurationSection("podpisy").getKeys(false), completions);
        if (args.length == 1) {
            for (String completion : plugin.getConfig().getConfigurationSection("podpisy").getKeys(false)) {
                if (commandSender.hasPermission("podpis." + completion)) {
                    completions.add(completion);
                }
            }
        }
        Collections.sort(completions);
        return completions;
    }
}
