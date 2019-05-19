package io.github.caprapaul.vital;

import io.github.caprapaul.vital.commands.TeleportCommands;
import io.github.caprapaul.vital.commands.WarpCommands;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Vital extends JavaPlugin
{
    public String prefix = ChatColor.GOLD + "[" + ChatColor.AQUA + "V" + ChatColor.GOLD + "] " + ChatColor.RESET;

    private FileConfiguration warps;
    private File warpsFile;

    public FileConfiguration getWarps()
    {
        return warps;
    }

    private void loadCommands()
    {
        new TeleportCommands(this);
        new WarpCommands(this);
    }

    private void loadFiles()
    {
        this.warpsFile = new File(getDataFolder(), "data.yml");
    }

    private void loadYamls()
    {
        this.warps = YamlConfiguration.loadConfiguration(warpsFile);
    }

    private void saveYamls()
    {
        try
        {
            this.warps.save(warpsFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable()
    {
        loadFiles();
        loadYamls();
        loadCommands();
    }

    @Override
    public void onDisable()
    {
        saveYamls();
    }
}
