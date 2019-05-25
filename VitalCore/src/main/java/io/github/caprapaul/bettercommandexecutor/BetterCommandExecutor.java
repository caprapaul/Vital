package io.github.caprapaul.bettercommandexecutor;

import io.github.caprapaul.bettercommandexecutor.listeners.ToggleListener;
import io.github.caprapaul.vitalcore.VitalCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class BetterCommandExecutor implements CommandExecutor, ToggleListener
{
    protected final VitalCore plugin;

    public BetterCommandExecutor(VitalCore plugin)
    {
        this.plugin = plugin;
    }

    public void onEnable() {}

    public void onDisable() {}

    protected void loadCommands(Object object, VitalCore plugin)
    {
        Class<?> clazz = object.getClass();
        for (Method method : clazz.getDeclaredMethods())
        {
            if (method.isAnnotationPresent(BetterCommand.class))
            {
                BetterCommand betterCommand = method.getAnnotation(BetterCommand.class);
                plugin.getCommand(betterCommand.name()).setExecutor(this);
            }
        }
    }

    protected void parseCommand(Object object, CommandSender commandSender, Command command, String commandLabel, String[] args)
    {
        Class<?> clazz = object.getClass();
        for (Method method : clazz.getDeclaredMethods())
        {
            if (method.isAnnotationPresent(BetterCommand.class))
            {
                BetterCommand betterCommand = method.getAnnotation(BetterCommand.class);
                if (command.getName().equalsIgnoreCase(betterCommand.name()))
                {
                    method.setAccessible(true);
                    try
                    {
                        method.invoke(object, commandSender, args, commandLabel);
                    }
                    catch (IllegalAccessException e)
                    {
                        e.printStackTrace();
                    }
                    catch (InvocationTargetException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
