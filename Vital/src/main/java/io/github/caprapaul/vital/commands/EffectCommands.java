package io.github.caprapaul.vital.commands;

import io.github.caprapaul.bettercommandexecutor.BetterCommand;
import io.github.caprapaul.bettercommandexecutor.BetterCommandExecutor;
import io.github.caprapaul.bettercommandexecutor.BetterExecutor;
import io.github.caprapaul.vital.Vital;
import io.github.caprapaul.vitalcore.VitalCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@BetterExecutor
public class EffectCommands extends BetterCommandExecutor
{
    private static final int MAX_HEALTH = 20;
    
    public EffectCommands(VitalCore plugin)
    {
        super(plugin);
        loadCommands(this, this.plugin);
    }

    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args)
    {
        parseCommand(this, commandSender, command, commandLabel, args);
        return false;
    }

    @BetterCommand(name = "heal")
    public void heal(CommandSender commandSender, String[] args, String commandLabel)
    {
        if (!(commandSender instanceof Player))
        {
            commandSender.sendMessage(plugin.prefix + ChatColor.RED + "Error: The console can't heal itself!");
            return;
        }

        Player player = (Player) commandSender;
        player.setHealth(MAX_HEALTH);
    }

    @BetterCommand(name = "feed")
    public void feed(CommandSender commandSender, String[] args, String commandLabel)
    {
        if (!(commandSender instanceof Player))
        {
            commandSender.sendMessage(plugin.prefix + ChatColor.RED + "Error: The console can't feed itself!");
            return;
        }

        Player player = (Player) commandSender;
        player.setFoodLevel(MAX_HEALTH);
    }
}
