package org.h0tkinss.h0t_quests.tasks;


import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import net.playavalon.mythicdungeons.api.events.dungeon.DungeonEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.h0tkinss.h0t_quests.H0t_quests;

import java.util.List;
import java.util.UUID;

public class MythicDungeonsCompleteTask extends BukkitTaskType {
    private final H0t_quests plugin;
    public MythicDungeonsCompleteTask(H0t_quests plugin) {
        super("dungeoncomplete", "h0tkinss", "Complete MythicDungeon dungeon");
        this.plugin = plugin;
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "name"));
    }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onDungeonEndEvent(DungeonEndEvent e) {
            List<Player> players = e.getPlayers();
            for (Player player : players) {
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

                    super.debug(player.getName() + " finished dungeon " + e.getDungeon().getDisplayName(), quest.getId(), task.getId(), playerId);

                    int amount = (int) task.getConfigValue("amount");
                    String dung = (String) task.getConfigValue("name");
                    if (!e.getDungeon().getWorldName().contains(dung)) continue;
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

}

