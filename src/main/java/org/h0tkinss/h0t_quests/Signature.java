package org.h0tkinss.h0t_quests;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Signature {
    private final String signatureName;

    public String getSignatureName() {
        return signatureName;
    }

    public Component getSignatureDisplayName() {
        return signatureDisplayName;
    }

    public Integer getCost() {
        return cost;
    }

    public List<Component> getLore() {
        return lore;
    }

    private final Component signatureDisplayName;
    private final Integer cost;
    private final List<Component> lore;

    public Signature(String signatureName, Player player) {
        H0t_quests plugin = H0t_quests.getInstance();
        this.signatureDisplayName = MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, plugin.getConfig().getString("podpisy." + signatureName + ".display_name"))).decoration(TextDecoration.ITALIC, false);
        this.signatureName = signatureName;
        this.cost = plugin.getConfig().getInt("podpisy." + signatureName + ".cost");
        this.lore = new ArrayList<>();
        for (String line : plugin.getConfig().getStringList("podpisy." + signatureName + ".lore")) {
            lore.add(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, line)).decoration(TextDecoration.ITALIC, false));
        }
    }
}
