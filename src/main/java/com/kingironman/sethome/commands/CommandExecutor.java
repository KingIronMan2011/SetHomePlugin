package com.kingironman.sethome.commands;

import com.kingironman.sethome.SetHome;
import com.kingironman.sethome.utilities.MessageUtils;
import com.kingironman.sethome.utilities.LoggingUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class CommandExecutor implements org.bukkit.command.CommandExecutor {

    public enum COMMAND_TYPE {
        SETHOME,
        HOME,
        DELETEHOME,
        LISTHOME
    }

    private HashMap<COMMAND_TYPE, Integer> cooldownTime;
    private HashMap<COMMAND_TYPE, Integer> warmupTime;

    private static HashMap<UUID, HashMap<COMMAND_TYPE, Long>> cooldownTask;
    private static HashMap<UUID, HashMap<COMMAND_TYPE, Boolean>> warmupInEffect;
    private static HashMap<UUID, HashMap<COMMAND_TYPE, BukkitTask>> warmupTask;

    public CommandExecutor() {
        initializeHashMaps();
    }

    private void initializeHashMaps() {
        cooldownTime = new HashMap<>();
        warmupTime = new HashMap<>();
        cooldownTask = new HashMap<>();
        warmupInEffect = new HashMap<>();
        warmupTask = new HashMap<>();
        cooldownTime.put(COMMAND_TYPE.SETHOME, SetHome.getInstance().configUtils.CMD_SETHOME_COOLDOWN);
        cooldownTime.put(COMMAND_TYPE.HOME, SetHome.getInstance().configUtils.CMD_HOME_COOLDOWN);
        cooldownTime.put(COMMAND_TYPE.DELETEHOME, SetHome.getInstance().configUtils.CMD_DELETEHOME_COOLDOWN);
        cooldownTime.put(COMMAND_TYPE.LISTHOME, 0);
        warmupTime.put(COMMAND_TYPE.SETHOME, SetHome.getInstance().configUtils.CMD_SETHOME_WARMUP);
        warmupTime.put(COMMAND_TYPE.HOME, SetHome.getInstance().configUtils.CMD_HOME_WARMUP);
        warmupTime.put(COMMAND_TYPE.DELETEHOME, SetHome.getInstance().configUtils.CMD_DELETEHOME_WARMUP);
        warmupTime.put(COMMAND_TYPE.LISTHOME, 0);
    }

    public static HashMap<UUID, HashMap<COMMAND_TYPE, Long>> getCooldownTask() {
        return cooldownTask;
    }

    public static HashMap<UUID, HashMap<COMMAND_TYPE, Boolean>> getWarmupInEffect() {
        return warmupInEffect;
    }

    public static HashMap<UUID, HashMap<COMMAND_TYPE, BukkitTask>> getWarmupTask() {
        return warmupTask;
    }

    public void executeCmd(Player player, COMMAND_TYPE commandType, String[] commandArgs) {
        String homeName = "main";
        if (commandArgs.length > 0){
            homeName = commandArgs[0];
        }
        try {
            if (commandType == COMMAND_TYPE.SETHOME){
                SetHome.getInstance().commands.cmdSetHome(player, homeName);
            } else if (commandType == COMMAND_TYPE.HOME){
                SetHome.getInstance().commands.cmdHome(player, homeName);
            } else if (commandType == COMMAND_TYPE.DELETEHOME) {
                SetHome.getInstance().commands.cmdDeleteHome(player, homeName);
            } else if (commandType == COMMAND_TYPE.LISTHOME){
                SetHome.getInstance().commands.cmdListHome(player);
            }
        } catch (Exception e) {
            com.kingironman.sethome.utilities.LoggingUtils.error("Error executing command: " + commandType, e);
            player.sendMessage("\u00a7cAn internal error occurred while executing the command. See server logs.");
        }
    }

    public boolean executeCooldown(Player player, COMMAND_TYPE commandType, int seconds) {
        if (cooldownTask.containsKey(player.getUniqueId())) {
            if (cooldownTask.get(player.getUniqueId()).containsKey(commandType)) {
                long secondsLeft = ((cooldownTask.get(player.getUniqueId()).get(commandType) / 1000) + seconds) - (System.currentTimeMillis() / 1000);
                if (secondsLeft > 0) {
                    SetHome.getInstance().messageUtils.displayMessage(player, MessageUtils.MESSAGE_TYPE.COOLDOWN, (int) secondsLeft);
                    return true;
                }
            }
        }
        HashMap<COMMAND_TYPE, Long> cooldownTaskData = new HashMap<>();
        cooldownTaskData.put(commandType, System.currentTimeMillis());
        cooldownTask.put(player.getUniqueId(), cooldownTaskData);
        return false;
    }

    public void executeWarmup(Player player, String[] commandArgs, COMMAND_TYPE commandType, int seconds) {
        SetHome.getInstance().messageUtils.displayMessage(player, MessageUtils.MESSAGE_TYPE.WARMUP, seconds);
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                executeCmd(player, commandType, commandArgs);
                warmupInEffect.get(player.getUniqueId()).put(commandType, false);
            }
        };
        HashMap<COMMAND_TYPE, Boolean> warmupInEffectData = new HashMap<>();
        warmupInEffectData.put(commandType, true);
        warmupInEffect.put(player.getUniqueId(), warmupInEffectData);

        HashMap<COMMAND_TYPE, BukkitTask> warmupTaskData = new HashMap<>();
        warmupTaskData.put(commandType, runnable.runTaskLater(SetHome.getInstance(), 20L * seconds));
        warmupTask.put(player.getUniqueId(), warmupTaskData);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            SetHome.getInstance().messageUtils.displayMessage(sender, MessageUtils.MESSAGE_TYPE.DENY_CONSOLE, null);
            return false;
        }

        Player player = (Player) sender;
        COMMAND_TYPE commandType = null;

        if (command.getName().equals("sethome")) {
            commandType = COMMAND_TYPE.SETHOME;
        }
        else if (command.getName().equals("home")) {
            commandType = COMMAND_TYPE.HOME;
        }
        else if (command.getName().equals("deletehome")) {
            commandType = COMMAND_TYPE.DELETEHOME;
        } else {
            commandType = COMMAND_TYPE.LISTHOME;
        }

    int cooldownSeconds = cooldownTime.get(commandType);
        int warmupSeconds = warmupTime.get(commandType);
        // Both cooldown and warmup enabled
    try {
    if (cooldownSeconds > 0 && warmupSeconds > 0) {
            boolean running = executeCooldown(player, commandType, cooldownSeconds);
            if (running)
                return false;
            executeWarmup(player, args, commandType, warmupSeconds);
        }
        // Just cooldown enabled
        else if (cooldownSeconds > 0) {
            boolean running = executeCooldown(player, commandType, cooldownSeconds);
            if (running)
                return false;
            executeCmd(player, commandType, args);
        }
        // Just warmup enabled
        else if (warmupSeconds > 0) {
            executeWarmup(player, args, commandType, warmupSeconds);
        }
        // Both cooldown and warmup disabled
        else {
            executeCmd(player, commandType, args);
        }
        } catch (Exception e) {
            LoggingUtils.error("Unhandled error in command execution", e);
            player.sendMessage("\u00a7cAn internal error occurred. See server logs.");
            return true;
        }
        return false;
    }

}