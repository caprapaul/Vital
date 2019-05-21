package io.github.caprapaul.vital;

import io.github.caprapaul.vital.commands.HomeCommands;
import io.github.caprapaul.vital.commands.TeleportCommands;
import io.github.caprapaul.vital.commands.WarpCommands;
import io.github.caprapaul.vital.data.Warp;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Vital extends JavaPlugin
{
    static
    {
        ConfigurationSerialization.registerClass(Warp.class, "Warp");
    }

    public String prefix = ChatColor.GOLD + "[" + ChatColor.AQUA + "V" + ChatColor.GOLD + "] " + ChatColor.RESET;

    private FileConfiguration warps;
    private File warpsFile;
    public FileConfiguration getWarps()
    {
        return this.warps;
    }

    private FileConfiguration homes;
    private File homesFile;
    public FileConfiguration getPlayerHomes()
    {
        return this.homes;
    }

    private void loadCommands()
    {
        new TeleportCommands(this);
        new WarpCommands(this);
        new HomeCommands(this);
    }

    private void loadFiles()
    {
        this.warpsFile = new File(getDataFolder(), "warps.yml");
        this.homesFile = new File(getDataFolder(), "homes.yml");
    }

    private void loadYamls()
    {
        this.warps = YamlConfiguration.loadConfiguration(this.warpsFile);
        this.homes = YamlConfiguration.loadConfiguration(this.homesFile);
    }

    private void saveYamls()
    {
        try
        {
            this.warps.save(this.warpsFile);
            this.homes.save(this.homesFile);
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
