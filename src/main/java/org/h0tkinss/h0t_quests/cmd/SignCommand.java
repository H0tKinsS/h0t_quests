package org.h0tkinss.h0t_quests.cmd;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.h0tkinss.h0t_quests.H0t_quests;
import org.h0tkinss.h0t_quests.Signature;
import org.h0tkinss.h0t_quests.SignatureManager;
import org.h0tkinss.h0t_quests.event.PlayerSignatureEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.List;

public class SignCommand implements CommandExecutor, TabExecutor {
    private final H0t_quests plugin;
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

        String signatureName = args[0];
        SignatureManager signatureManager = plugin.getSignatureManager();

        if(!signatureManager.isValidSignature(signatureName)) {
            player.sendMessage(mm.deserialize("<red>Nie znaleziono takiego podpisu!"));
            return true;
        }

        Signature signature = new Signature(signatureName, player);

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType().equals(Material.AIR)) {
            player.sendMessage(mm.deserialize("<red>Nie możesz podpisać powietrza!"));
            return true;
        }
        if(!(plugin.getSignatureManager().canUseSignature(player, signature.getSignatureName()))){
            player.sendMessage(mm.deserialize("<red>Nie możesz użyć tego podpisu!"));
            return true;
        }
        double cena = itemStack.getAmount() * signature.getCost();
        if(!(plugin.getEcon().has(Bukkit.getOfflinePlayer(player.getUniqueId()), cena)) && !plugin.getPerms().has(player, "podpis.admin")) {
            player.sendMessage(mm.deserialize("<red>Nie posiadasz gotówki!"));
            return true;
        }

        if (!plugin.getSignatureManager().canSignItem(itemStack)) {
            player.sendMessage(mm.deserialize("<red>Nie możesz podpisać tego przedmiotu!"));
            return true;
        }
        PlayerSignatureEvent e = new PlayerSignatureEvent(player, signature, itemStack);
        plugin.getServer().getPluginManager().callEvent(e);
        if (!e.isCancelled()) {
            ItemMeta itemMeta = plugin.getSignatureManager().signItem(itemStack, signature.getSignatureDisplayName(), signature.getLore());
            itemStack.setItemMeta(itemMeta);
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 10, 29);
            plugin.getEcon().withdrawPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), cena);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        //StringUtil.copyPartialMatches(args[0], plugin.getConfig().getConfigurationSection("podpisy").getKeys(false), completions);
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            plugin.getConfig().getConfigurationSection("podpisy").getKeys(false).forEach(completion -> {
                if (plugin.getSignatureManager().canUseSignature(commandSender, completion)) {
                    if (completion.toLowerCase().startsWith(args[0].toLowerCase())) {
                        completions.add(completion);
                    }
                }
            });
        }
        Collections.sort(completions);
        return completions;
    }
}
