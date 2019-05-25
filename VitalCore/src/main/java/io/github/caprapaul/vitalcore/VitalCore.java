package io.github.caprapaul.vitalcore;

import io.github.caprapaul.bettercommandexecutor.BetterCommandExecutor;
import io.github.caprapaul.bettercommandexecutor.BetterExecutor;
import io.github.caprapaul.bettercommandexecutor.initiators.ToggleInitiator;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;

public class VitalCore extends JavaPlugin
{
    public String prefix = ChatColor.GOLD + "[" + ChatColor.AQUA + "V" + ChatColor.GOLD + "] " + ChatColor.RESET;

    protected ToggleInitiator toggleInitiator;

    protected void loadCommands()
    {
        try
        {
            try (ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages(this.getClass().getPackage().getName() + ".commands")
                    .scan())
            {
                ClassInfoList commandClasses = scanResult.getClassesWithAnnotation(BetterExecutor.class.getName());

                for (Class<?> commandClass : commandClasses.loadClasses())
                {
                    Constructor constructor = commandClass.getConstructor(VitalCore.class);
                    BetterCommandExecutor commandExecutor = (BetterCommandExecutor) constructor.newInstance(this);
                    toggleInitiator.addListener(commandExecutor);
                }

            }
        }
        catch (NoSuchMethodException e)
        {
            getLogger().info(e.getMessage());
            getLogger().info(Arrays.toString(e.getStackTrace()));
        }
        catch (IllegalAccessException e)
        {
            getLogger().info(e.getMessage());
            getLogger().info(Arrays.toString(e.getStackTrace()));
        }
        catch (InstantiationException e)
        {
            getLogger().info(e.getMessage());
            getLogger().info(Arrays.toString(e.getStackTrace()));
        }
        catch (InvocationTargetException e)
        {
            getLogger().info(e.getMessage());
            getLogger().info(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void onEnable()
    {
        toggleInitiator = new ToggleInitiator();
        onEnabled();
        toggleInitiator.enable();
    }

    @Override
    public void onDisable()
    {
        onDisabled();
        toggleInitiator.disable();
        toggleInitiator.clearListeners();
    }

    public void onEnabled(){}

    public void onDisabled(){}

}
