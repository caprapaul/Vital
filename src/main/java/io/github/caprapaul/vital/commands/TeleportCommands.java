package io.github.caprapaul.vital.commands;

import io.github.caprapaul.bettercommandexecutor.BetterCommand;
import io.github.caprapaul.bettercommandexecutor.BetterCommandExecutor;
import io.github.caprapaul.vital.Vital;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TeleportCommands extends BetterCommandExecutor
{
    private final Vital plugin;

    public TeleportCommands(Vital plugin) {
        this.plugin = plugin; // Store the plugin in situations where you need it.
    }

    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args)
    {
        parseCommand(this, commandSender, command, commandLabel, args);
        return false;
    }

    @BetterCommand(name = "test")
    public void test(CommandSender commandSender, String[] args)
    {
        plugin.getLogger().info("TEST!");
    }
}
