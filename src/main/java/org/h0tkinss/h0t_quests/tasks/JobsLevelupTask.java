package org.h0tkinss.h0t_quests.tasks;

import com.gamingmesh.jobs.api.JobsLevelUpEvent;
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
import org.h0tkinss.h0t_quests.H0t_quests;

import java.util.UUID;

public class JobsLevelupTask extends BukkitTaskType {

    public JobsLevelupTask(H0t_quests plugin) {
        super("jobsrebornlvl", "h0tkinss", "JobsReborn levelup task");
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "name"));
    }

    @EventHandler
    public void onJobsRebornPlayerLvlUp(JobsLevelUpEvent e) {
        Player player = e.getPlayer().getPlayer();
        UUID playerId = player.getUniqueId();
        if (player.hasMetadata("NPC")) {
            return;
        }
        BukkitQuestsPlugin questsPlugin = (BukkitQuestsPlugin) Bukkit.getPluginManager().getPlugin("Quests");

        assert questsPlugin != null;
        QPlayer qPlayer = questsPlugin.getPlayerManager().getPlayer(playerId);
        if (qPlayer == null) {
            return;
        }
        for (TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(player, qPlayer, this)) {
            Quest quest = pendingTask.quest();
            Task task = pendingTask.task();
            TaskProgress taskProgress = pendingTask.taskProgress();

            super.debug(player.getName() + " leveled up " + e.getJob().getJobDisplayName(), quest.getId(), task.getId(), playerId);

            int amount = (int) task.getConfigValue("amount");
            String type = (String) task.getConfigValue("name");
            if(!e.getJob().getName().equalsIgnoreCase(type)) continue;
            int level = e.getLevel();
            super.debug("Updating task progress (now " + level + ")", quest.getId(), task.getId(), playerId);
            taskProgress.setProgress(level);
            if (level >= amount) {
                super.debug("Marking task as complete", quest.getId(), task.getId(), playerId);
                taskProgress.setCompleted(true);
            }
        }
    }

}
