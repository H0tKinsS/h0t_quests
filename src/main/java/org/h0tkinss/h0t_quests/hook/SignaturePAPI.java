package org.h0tkinss.h0t_quests.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.h0tkinss.h0t_quests.H0t_quests;

import java.util.Arrays;
import java.util.List;

public class SignaturePAPI extends PlaceholderExpansion {

    private final List<String> placeholders = Arrays.asList("%h0tquests_signature_display_<name>%", "%h0tquests_signature_can_use_<name>%", "%h0tquests_signature_cost_<name>%");
    private final H0t_quests plugin;
    public SignaturePAPI(H0t_quests plugin){
        this.plugin = plugin;
    }
    @Override
    public List<String> getPlaceholders() {return placeholders;}
    @Override
    public String getAuthor() {
        return "H0tKinsS";
    }

    @Override
    public String getIdentifier() {
        return "h0tquests";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if(params.startsWith("signature_display_")) {
            String signatureName = params.replace("signature_display_", "");
            for(String param : plugin.getConfig().getConfigurationSection("podpisy").getKeys(false)) {
                if (signatureName.equals(param)) {
                    MiniMessage mm = MiniMessage.miniMessage();
                    return LegacyComponentSerializer.legacyAmpersand().serialize(mm.deserialize(PlaceholderAPI.setPlaceholders(player,plugin.getConfig().getString("podpisy." + signatureName + ".display_name"))));
                }
            }
        }
        if(params.startsWith("signature_cost_")) {
            String signatureName = params.replace("signature_cost_", "");
            for(String param : plugin.getConfig().getConfigurationSection("podpisy").getKeys(false)) {
                if (signatureName.equals(param)) {
                    return String.valueOf(plugin.getConfig().getInt("podpisy." + signatureName + ".cost"));
                }
            }
        }
        if(params.startsWith("signature_can_use_")) {
            String signatureName = params.replace("signature_can_use_", "");
            return bool(plugin.getSignatureManager().canUseSignature(Bukkit.getPlayer(player.getUniqueId()), signatureName));
        }

        return null; // Placeholder is unknown by the Expansion
    }
    public String bool(boolean b) {
        return b ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
    }
}
