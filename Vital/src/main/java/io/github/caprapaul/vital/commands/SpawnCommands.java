package io.github.caprapaul.vital.commands;

import io.github.caprapaul.bettercommandexecutor.BetterCommand;
import io.github.caprapaul.bettercommandexecutor.BetterCommandExecutor;
import io.github.caprapaul.bettercommandexecutor.BetterExecutor;
import io.github.caprapaul.bettercommandexecutor.CommandTarget;
import io.github.caprapaul.vital.wrappers.WarpWrapper;
import io.github.caprapaul.vitalcore.VitalCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@BetterExecutor
public class SpawnCommands extends BetterCommandExecutor
{

    public SpawnCommands(VitalCore plugin)
    {
        super(plugin);
        loadCommands(this, this.plugin);
    }

    @Override
    public void onEnable()
    {
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

        World world = player.getWorld();
        Location target = world.getSpawnLocation();

        // Send a message and then teleport the player to the target.
        player.sendMessage(this.plugin.prefix + ChatColor.GRAY + "Teleporting to spawn");
        WarpWrapper.warp(this.plugin, target, player);
    }
}
