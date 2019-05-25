package io.github.caprapaul.vitaltemplate.commands;

import io.github.caprapaul.bettercommandexecutor.BetterCommand;
import io.github.caprapaul.bettercommandexecutor.BetterCommandExecutor;
import io.github.caprapaul.bettercommandexecutor.BetterExecutor;
import io.github.caprapaul.vitalcore.VitalCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@BetterExecutor
public class ExampleCommand extends BetterCommandExecutor
{
    public ExampleCommand(VitalCore plugin)
    {
        super(plugin);
        loadCommands(this, this.plugin);
    }

    @Override
    public void onEnable()
    {
        // Do stuff on enable...
    }

    @Override
    public void onDisable()
    {
        // Do stuff on disable...
    }

    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args)
    {
        parseCommand(this, commandSender, command, commandLabel, args);
        return false;
    }


    @BetterCommand(name = "test")
    public void test(CommandSender commandSender, String[] args, String commandLabel)
    {
        plugin.getLogger().info("TEST!");
    }
}
