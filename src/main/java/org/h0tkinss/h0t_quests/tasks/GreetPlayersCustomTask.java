package org.h0tkinss.h0t_quests.tasks;

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
import org.h0tkinss.h0t_quests.event.PlayerGreetEvent;

import java.util.UUID;

public class GreetPlayersCustomTask extends BukkitTaskType {
    private final H0t_quests plugin;
    public GreetPlayersCustomTask(H0t_quests plugin) {
        super("greetplayer", "h0tkinss", "Greet new players");
        this.plugin = plugin;
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
    }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerGreetTask(PlayerGreetEvent e) {
        Player player = e.getGreeter();
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

            super.debug(player.getName() + " greeted a player!", quest.getId(), task.getId(), playerId);

            int amount = (int) task.getConfigValue("amount");

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
