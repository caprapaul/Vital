package io.github.caprapaul.vital;

import io.github.caprapaul.vital.commands.TeleportCommands;
import org.bukkit.plugin.java.JavaPlugin;

public class Vital extends JavaPlugin
{

    private void loadCommands()
    {
        new TeleportCommands(this);
    }

    @Override
    public void onEnable()
    {
        loadCommands();
    }

    @Override
    public void onDisable()
    {
    }
}
