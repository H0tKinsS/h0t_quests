package org.h0tkinss.h0t_quests;

import com.hazebyte.crate.api.CrateAPI;
import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class SignatureManager {
    private final H0t_quests plugin;
    public SignatureManager(H0t_quests plugin){
        this.plugin = plugin;
    }
    public Boolean isValidSignature(String signatureName) {
        return plugin.getConfig().getConfigurationSection("podpisy").getKeys(false).contains(signatureName);
    }
    public Boolean canUseSignature(CommandSender sender, String signature){
        return plugin.getPerms().has(sender, "podpis." + signature);
    }
    public Boolean canUseSignature(Player player, String signature){
        return plugin.getPerms().has(player, "podpis." + signature);
    }
    public Boolean isItemSigned(ItemStack item) {
        if (item != null && item.getItemMeta() != null) {
            ItemMeta meta = item.getItemMeta();
            NamespacedKey key = new NamespacedKey(plugin, "CustomItem");
            Integer isSigned = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
            return isSigned != null && isSigned == 1;
        }
        return false;
    }
    public boolean canSignItem(ItemStack item) {
        if (CrateAPI.getCrateRegistrar().isCrate(item)){
            return false;
        }
        for (String blacklistedItem : plugin.getConfig().getStringList("podpisy-blacklist")) {
            if (blacklistedItem.startsWith("ANY_")) {
                if (item.getType().toString().contains(blacklistedItem.replaceAll("ANY_", ""))) {
                    return false;
                }
            } else if (blacklistedItem.equals(item.getType().toString())) {
                return false;
            }
        }
        if (isItemSigned(item)) {
            return false;
        }
        if(Bukkit.getPluginManager().isPluginEnabled("ExecutableItems")) {
            if(ExecutableItemsAPI.getExecutableItemObject(item).isValid()) return false;
        }
        return true;
    }
    public ItemMeta signItem(ItemStack item, Component displayName, List<Component> lore) {
        NamespacedKey key = new NamespacedKey(plugin, "CustomItem");
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(displayName);
        itemMeta.lore(lore);
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        pdc.set(key, PersistentDataType.INTEGER, 1);
        return itemMeta;
    }
}
