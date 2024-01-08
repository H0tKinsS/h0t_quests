package org.h0tkinss.h0t_quests;

import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskTypeManager;
import com.leonardobishop.quests.common.player.QPlayerManager;
import com.leonardobishop.quests.common.plugin.Quests;
import me.NoChance.PvPManager.PvPManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.h0tkinss.h0t_quests.cmd.ReloadCommand;
import org.h0tkinss.h0t_quests.cmd.SignCommand;
import org.h0tkinss.h0t_quests.hook.SignaturePAPI;
import org.h0tkinss.h0t_quests.listeners.PlayerGreetListener;
import org.h0tkinss.h0t_quests.listeners.ServerJoinEvent;
import org.h0tkinss.h0t_quests.tasks.*;



public final class H0t_quests extends JavaPlugin {

    private static Economy econ = null;

    public static H0t_quests getInstance() {
        return plugin;
    }

    private static H0t_quests plugin;
    Plugin executableItems;
    private static Permission perms = null;
    private static Chat chat = null;
    public FileConfiguration config = getConfig();
    public Chat getChat(){
        return chat;
    }
    public Permission getPerms(){
        return perms;
    }
    public Economy getEcon(){
        return econ;
    }
    public QPlayerManager getPlayerManager() {
        return qPlayerManager;
    }
    private QPlayerManager qPlayerManager;
    private static GreetManager greetManager;
    private static SignatureManager signatureManager;

    private PvPManager pvpmanager;
    public PvPManager getPvpmanager(){
        return pvpmanager;
    }
    public GreetManager getGreetManager(){
        return greetManager;
    }
    public SignatureManager getSignatureManager(){
        return signatureManager;
    }
    @Override
    public void onEnable() {
        plugin = this;
        this.saveDefaultConfig();
        /*Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                Bukkit.getLogger().info("Nowi gracze: " + newlyJoinedPlayers.size());
                newlyJoinedPlayers.forEach((name, greeters) -> {
                    Bukkit.getLogger().info(name + ": ");
                    StringBuilder msg = new StringBuilder("Przywitany przez: ");
                    greeters.forEach((greeter) -> {
                       msg.append(greeter + ' ');
                    });
                    Bukkit.getLogger().info(String.valueOf(msg));
                });
            }
        }, 0, 3 * 20);*/
        greetManager = new GreetManager(this);
        signatureManager = new SignatureManager(this);
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        setupChat();
        if (Bukkit.getPluginManager().isPluginEnabled("PvPManager"))
            pvpmanager = (PvPManager) Bukkit.getPluginManager().getPlugin("PvPManager");

        Quests questsPlugin = (Quests) Bukkit.getPluginManager().getPlugin("Quests");
        BukkitTaskTypeManager taskTypeManager = (BukkitTaskTypeManager) questsPlugin.getTaskTypeManager();
        if(Bukkit.getPluginManager().isPluginEnabled("ExecutableItems")) {
            executableItems = Bukkit.getPluginManager().getPlugin("ExecutableItems");
            Bukkit.getLogger().info("ExecutableItems hooked !");
        }

        MythicDungeonsCompleteTask mythicDungeonsCompleteTask = new MythicDungeonsCompleteTask(this);
        ProtectionStonesCreateTask protectionStonesCreateTask = new ProtectionStonesCreateTask(this);
        JobsJoinTask jobsJoinTask = new JobsJoinTask(this);
        JobsLevelupTask jobsLevelupTask = new JobsLevelupTask(this);
        AfkRewardTask afkRewardTask = new AfkRewardTask(this);
        GreetPlayersCustomTask greetPlayersCustomTask = new GreetPlayersCustomTask(this);
        CrateOpenCustomTask crateOpenCustomTask = new CrateOpenCustomTask(this);
        DailyRewardsTask dailyRewardsTask = new DailyRewardsTask(this);
        getServer().getPluginManager().registerEvents(new PlayerGreetListener(this), this);
        getServer().getPluginManager().registerEvents(new ServerJoinEvent(this), this);
        try {
            taskTypeManager.registerTaskType(afkRewardTask);
            taskTypeManager.registerTaskType(dailyRewardsTask);
            taskTypeManager.registerTaskType(greetPlayersCustomTask);
            taskTypeManager.registerTaskType(crateOpenCustomTask);
            taskTypeManager.registerTaskType(protectionStonesCreateTask);
            if (Bukkit.getPluginManager().isPluginEnabled("Jobs")) {
                taskTypeManager.registerTaskType(jobsJoinTask);
                taskTypeManager.registerTaskType(jobsLevelupTask);
            }
            if (Bukkit.getPluginManager().isPluginEnabled("MythicDungeons")) {
                taskTypeManager.registerTaskType(mythicDungeonsCompleteTask);
            }
        } catch (IllegalStateException e){
            Bukkit.getLogger().info(e.getMessage());
        }

        this.getCommand("podpis").setExecutor(new SignCommand(this));
        this.getCommand("h0t_quests").setExecutor(new ReloadCommand(this));
        this.getCommand("podpis").setTabCompleter(new SignCommand(this));
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new SignaturePAPI(this).register();
        }
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    @Override
    public void reloadConfig() {
        super.reloadConfig();

        saveDefaultConfig();
        config = super.getConfig();
        config.options().copyDefaults(true);
        saveConfig();
    }
}
