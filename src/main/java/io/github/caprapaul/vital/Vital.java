package io.github.caprapaul.vital;

import io.github.caprapaul.vital.commands.TeleportCommands;
import org.bukkit.plugin.java.JavaPlugin;

public class Vital extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        this.getCommand("test").setExecutor(new TeleportCommands(this));
    }

    @Override
    public void onDisable()
    {
    }
}
