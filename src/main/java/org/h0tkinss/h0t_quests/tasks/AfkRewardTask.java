package org.h0tkinss.h0t_quests.tasks;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import me.gethertv.afkrewards.event.AfkRewardsDone;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.h0tkinss.h0t_quests.H0t_quests;

import java.util.UUID;

public class AfkRewardTask  extends BukkitTaskType {
    private final H0t_quests plugin;

    public AfkRewardTask(H0t_quests plugin) {
        super("afkreward", "h0tkinss", "getAfkZone reward");
        this.plugin = plugin;
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
    }

    @EventHandler
    public void onPlayerAfkReward(AfkRewardsDone e) {
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

            super.debug(player.getName() + " received afk reward!", quest.getId(), task.getId(), playerId);

            int amount = (int) task.getConfigValue("amount");
            String type = (String) task.getConfigValue("name");
            //if (!e.getCrate().getCrateName().equalsIgnoreCase(crate_name)) return;
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
