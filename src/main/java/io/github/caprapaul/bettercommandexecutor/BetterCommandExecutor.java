package io.github.caprapaul.bettercommandexecutor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class BetterCommandExecutor implements CommandExecutor
{
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
                        method.invoke(object, commandSender, args);
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
