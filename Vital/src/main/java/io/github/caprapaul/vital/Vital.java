package io.github.caprapaul.vital;

import io.github.caprapaul.bettercommandexecutor.BetterCommandExecutor;
import io.github.caprapaul.vital.commands.*;
import io.github.caprapaul.vital.initiators.ToggleInitiator;
import io.github.caprapaul.vital.listeners.ToggleListener;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;

public class Vital extends JavaPlugin
{
    public String prefix = ChatColor.GOLD + "[" + ChatColor.AQUA + "V" + ChatColor.GOLD + "] " + ChatColor.RESET;

    private ToggleInitiator toggleInitiator;

    private void loadCommands()
    {
        try
        {
            Reflections reflections = new Reflections("io.github.caprapaul.vital.commands");
            Set<Class<? extends BetterCommandExecutor>> commandClasses = reflections.getSubTypesOf(BetterCommandExecutor.class);

            for (Class<?> commandClass: commandClasses)
            {
                Constructor constructor = commandClass.getConstructor(Vital.class);
                BetterCommandExecutor commandExecutor = (BetterCommandExecutor)constructor.newInstance(this);
                toggleInitiator.addListener(commandExecutor);
            }
        }
        catch (NoSuchMethodException e)
        {
            getLogger().info(Arrays.toString(e.getStackTrace()));
        }
        catch (IllegalAccessException e)
        {
            getLogger().info(Arrays.toString(e.getStackTrace()));
        }
        catch (InstantiationException e)
        {
            getLogger().info(Arrays.toString(e.getStackTrace()));
        }
        catch (InvocationTargetException e)
        {
            getLogger().info(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void onEnable()
    {
        toggleInitiator = new ToggleInitiator();
        loadCommands();
        toggleInitiator.enable();
    }

    @Override
    public void onDisable()
    {
        toggleInitiator.disable();
        toggleInitiator.clearListeners();
    }
}
