package io.github.caprapaul.bettercommandexecutor;

import io.github.caprapaul.vital.Vital;
import io.github.caprapaul.vital.listeners.ToggleListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class BetterCommandExecutor implements CommandExecutor, ToggleListener
{
    protected final Vital plugin;

    public BetterCommandExecutor(Vital plugin)
    {
        this.plugin = plugin;
    }

    public void onEnable() {}

    public void onDisable() {}

    protected void loadCommands(Object object, Vital plugin)
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
