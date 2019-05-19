package io.github.caprapaul.vital.commands;

import io.github.caprapaul.bettercommandexecutor.BetterCommandExecutor;
import io.github.caprapaul.vital.Vital;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class WarpCommands extends BetterCommandExecutor
{
    private final Vital plugin;

    public WarpCommands(Vital plugin)
    {
        this.plugin = plugin;
        loadCommands(this, this.plugin);
    }

    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args)
    {
        parseCommand(this, commandSender, command, commandLabel, args);
        return false;
    }
}
