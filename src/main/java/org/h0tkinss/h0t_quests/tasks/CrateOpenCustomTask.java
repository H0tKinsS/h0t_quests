package org.h0tkinss.h0t_quests.tasks;

import com.hazebyte.crate.api.event.CrateRewardEvent;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.h0tkinss.h0t_quests.H0t_quests;

import java.util.UUID;

public class CrateOpenCustomTask extends BukkitTaskType {
    private final H0t_quests plugin;
    public CrateOpenCustomTask(H0t_quests plugin) {
        super("opencrate", "h0tkinss", "Open CrateReloaded Crate");
        this.plugin = plugin;
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "crate_name"));
    }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onPlayerGreetTask(CrateRewardEvent e) {
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

                super.debug(player.getName() + " opened a crate " + e.getCrate().getCrateName(), quest.getId(), task.getId(), playerId);

                int amount = (int) task.getConfigValue("amount");
                String crate_name = (String) task.getConfigValue("crate_name");
                if(!e.getCrate().getCrateName().equalsIgnoreCase(crate_name)) continue;
                int progress = TaskUtils.getIntegerTaskProgress(taskProgress);
                int newProgress = progress + 1;
                taskProgress.setProgress(newProgress);
                super.debug("Updating task progress (now " + newProgress + ")", quest.getId(), task.getId(), playerId);

                if (newProgress >= amount) {
                    super.debug("Marking task as complete", quest.getId(), task.getId(), playerId);
                    taskProgress.setProgress(amount);
                    taskProgress.setCompleted(true);
                }
            }
        }

}

