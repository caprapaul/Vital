package io.github.caprapaul.vital.commands;

import io.github.caprapaul.bettercommandexecutor.BetterCommand;
import io.github.caprapaul.bettercommandexecutor.BetterCommandExecutor;
import io.github.caprapaul.bettercommandexecutor.BetterExecutor;
import io.github.caprapaul.bettercommandexecutor.CommandTarget;
import io.github.caprapaul.vital.data.Warp;
import io.github.caprapaul.vital.systems.TeleportSystem;
import io.github.caprapaul.vitalcore.VitalCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@BetterExecutor
public class SpawnCommands extends BetterCommandExecutor
{
    private final String FILE_NAME = "spawn.yml";
    private final String CONFIG_KEY = "spawn";
    private final String WARP_NAME = "spawn";

    private File spawnFile;
    private FileConfiguration spawnConfig;

    // There will only be one member
    private Warp worldSpawn;

    public SpawnCommands(VitalCore plugin)
    {
        super(plugin);
        loadCommands(this, this.plugin);
    }

    @Override
    public void onEnable()
    {
        this.spawnFile = new File(plugin.getDataFolder(), FILE_NAME);
        this.spawnConfig = YamlConfiguration.loadConfiguration(spawnFile);
        loadSpawn();
    }

    @Override
    public void onDisable()
    {
        saveSpawn();
    }

    private void saveSpawn()
    {
        try
        {
            this.spawnConfig.save(spawnFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void loadSpawn()
    {
        this.worldSpawn = (Warp) spawnConfig.get(CONFIG_KEY, new Warp());
    }

    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args)
    {
        parseCommand(this, commandSender, command, commandLabel, args);
        return false;
    }

    @BetterCommand(name = "spawn", target = CommandTarget.PLAYER)
    public void spawn(CommandSender commandSender, String[] args, String commandLabel)
    {
        // Get the player from commandSender
        Player player = (Player) commandSender;

        if(!worldSpawn.getName().equals(WARP_NAME))
        {
            player.sendMessage(this.plugin.prefix + ChatColor.RED + "Error: There is no set world spawn");
            return;
        }

        // Send a message and then teleport the player to the target.
        player.sendMessage(this.plugin.prefix + ChatColor.GRAY + "Teleporting to world spawn");
        TeleportSystem.teleport(this.plugin, player, worldSpawn.toLocation(plugin));
    }

    @BetterCommand(name = "spawndefault", target = CommandTarget.PLAYER)
    public void spawndefault(CommandSender commandSender, String[] args, String commandLabel)
    {
        // Get the player from commandSender
        Player player = (Player) commandSender;

        World world = Bukkit.getWorld("world");

        // Send a message and then teleport the player to the target.
        player.sendMessage(this.plugin.prefix + ChatColor.GRAY + "Teleporting to original world spawn");
        TeleportSystem.teleport(this.plugin, player, world.getSpawnLocation());
    }

    @BetterCommand(name = "setspawn", target = CommandTarget.PLAYER)
    public void setspawn(CommandSender commandSender, String[] args, String commandLabel)
    {
        // Get the player from commandSender
        Player player = (Player) commandSender;

        worldSpawn = new Warp(WARP_NAME, player.getLocation());
        spawnConfig.set(CONFIG_KEY, worldSpawn);

        player.sendMessage(plugin.prefix + ChatColor.GRAY + "World spawn has been set");
    }
}
