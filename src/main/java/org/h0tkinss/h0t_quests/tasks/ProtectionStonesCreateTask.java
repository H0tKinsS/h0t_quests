package org.h0tkinss.h0t_quests.tasks;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import dev.espi.protectionstones.event.PSCreateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.h0tkinss.h0t_quests.H0t_quests;

import java.util.UUID;

public class ProtectionStonesCreateTask extends BukkitTaskType {
    private final H0t_quests plugin;

    public ProtectionStonesCreateTask(H0t_quests plugin) {
        super("pscreate", "h0tkinss", "ProtectionStones create task");
        this.plugin = plugin;
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "name"));
    }

    @EventHandler
    public void onJobsRebornPlayerLvlUp(PSCreateEvent e) {
        Player player = e.getPlayer();
        UUID playerId = player.getUniqueId();
        if (player.hasMetadata("NPC")) {
            return;
        }
        BukkitQuestsPlugin questsPlugin = (BukkitQuestsPlugin) Bukkit.getPluginManager().getPlugin("Quests");

        QPlayer qPlayer = questsPlugin.getPlayerManager().getPlayer(playerId);
        if (qPlayer == null) {
            return;
        }
        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug(player.getName() + " leveled up ", quest.getId(), task.getId(), playerId);

            String type = (String) task.getConfigValue("name");
            //if (!e.getCrate().getCrateName().equalsIgnoreCase(crate_name)) return;
            super.debug("Completing task", quest.getId(), task.getId(), playerId);
            taskProgress.setCompleted(true);
        }
    }
}
