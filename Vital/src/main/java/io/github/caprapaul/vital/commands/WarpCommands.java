package io.github.caprapaul.vital.commands;

import io.github.caprapaul.bettercommandexecutor.BetterCommand;
import io.github.caprapaul.bettercommandexecutor.BetterCommandExecutor;
import io.github.caprapaul.bettercommandexecutor.BetterExecutor;
import io.github.caprapaul.bettercommandexecutor.CommandTarget;
import io.github.caprapaul.vital.data.Warp;
import io.github.caprapaul.vital.systems.TeleportSystem;
import io.github.caprapaul.vitalcore.VitalCore;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@BetterExecutor
public class WarpCommands extends BetterCommandExecutor
{
    private FileConfiguration warpsConfig;
    private File warpsFile;

    private ArrayList<Warp> warps;

    public WarpCommands(VitalCore plugin)
    {
        super(plugin);
        loadCommands(this, this.plugin);
    }

    @Override
    public void onEnable()
    {
        ConfigurationSerialization.registerClass(Warp.class, "Warp");

        this.warpsFile = new File(plugin.getDataFolder(), "warps.yml");
        this.warpsConfig = YamlConfiguration.loadConfiguration(warpsFile);
        this.warps = new ArrayList<Warp>();
        loadWarps();
    }

    @Override
    public void onDisable()
    {
        saveWarps();
    }

    private void saveWarps()
    {
        try
        {
            this.warpsConfig.save(warpsFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void loadWarps()
    {
        this.warps = (ArrayList<Warp>) warpsConfig.get("warps", new ArrayList<Warp>());
    }

    private void addWarp(Warp warp)
    {
        warps.add(warp);
        warpsConfig.set("warps", warps);
    }

    private void removeWarp(Warp warp)
    {
        warps.remove(warp);
        warpsConfig.set("warps", warps);
    }

    private boolean containsName(String name, Warp returnWarp)
    {
        for (Warp warp : warps)
        {
            if (warp != null && warp.getName().equalsIgnoreCase(name))
            {
                returnWarp.setName(warp.getName());
                returnWarp.setWorld(warp.getWorld());
                returnWarp.setX(warp.getX());
                returnWarp.setY(warp.getY());
                returnWarp.setZ(warp.getZ());
                return true;
            }
        }
        return false;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args)
    {
        parseCommand(this, commandSender, command, commandLabel, args);
        return false;
    }

    @BetterCommand(name = "warp", target = CommandTarget.PLAYER)
    public void warp(CommandSender commandSender, String[] args, String commandLabel)
    {
        Player player = (Player) commandSender;

        if (args.length == 0)
        {
            player.sendMessage(plugin.prefix + "Set a warp location.");
            player.sendMessage(ChatColor.GRAY + "Usage: /setwarp <name>");
            return;
        }

        String name = args[0];
        Warp warp = new Warp();
        if (!(containsName(name, warp)))
        {
            player.sendMessage(plugin.prefix + ChatColor.RED + "Error: Invalid warp name!");
            return;
        }

        player.sendMessage(plugin.prefix + ChatColor.GRAY + "Teleporting to " + ChatColor.GOLD + warp.getName() + ChatColor.GRAY + "...");
        WarpWrapper.warp(plugin, warp, player);
    }

    @BetterCommand(name = "warps")
    public void warps(CommandSender commandSender, String[] args, String commandLabel)
    {
        String warpsString = "";
        for (int i = 0; i < warps.size(); i++)
        {
            warpsString += ChatColor.GOLD + warps.get(i).getName();
            if (i < warps.size() - 1)
            {
                warpsString += ChatColor.GRAY + ", ";
            }
        }
        commandSender.sendMessage(plugin.prefix + "Warps: " + warpsString);
    }

    @BetterCommand(name = "setwarp", target = CommandTarget.PLAYER)
    public void setwarp(CommandSender commandSender, String[] args, String commandLabel)
    {
        Player player = (Player) commandSender;

        if (args.length == 0)
        {
            player.sendMessage(plugin.prefix + "Set a warp location.");
            player.sendMessage(ChatColor.GRAY + "Usage: /setwarp <name>");
            return;
        }

        if (args.length > 1)
        {
            player.sendMessage(plugin.prefix + ChatColor.RED + "Error: Too many arguments!");
            return;
        }

        String name = args[0];

        Location location = player.getLocation();
        addWarp(new Warp(name, location.getWorld().getName(), location.getX(), location.getY(), location.getZ()));
        player.sendMessage(plugin.prefix + ChatColor.GRAY + "Warp " + ChatColor.GOLD + name + ChatColor.GRAY + " has been " + ChatColor.GREEN + "created.");
    }

    @BetterCommand(name = "delwarp")
    public void delwarp(CommandSender commandSender, String[] args, String commandLabel)
    {
        if (args.length == 0)
        {
            commandSender.sendMessage(plugin.prefix + "Delete a warp location.");
            commandSender.sendMessage(ChatColor.GRAY + "Usage: /delwarp <name>");
            return;
        }

        if (args.length > 1)
        {
            commandSender.sendMessage(plugin.prefix + ChatColor.RED + "Error: Too many arguments!");
            return;
        }

        String name = args[0];
        Warp warp = new Warp();
        if (!(containsName(name, warp)))
        {
            commandSender.sendMessage(plugin.prefix + ChatColor.RED + "Error: Invalid warp name!");
            return;
        }

        removeWarp(warp);
        if (containsName(name, warp))
        {
            commandSender.sendMessage(plugin.prefix + ChatColor.RED + "Error: Warp wasn't deleted!");
            return;
        }

        commandSender.sendMessage(plugin.prefix + ChatColor.GRAY + "Warp " + ChatColor.GOLD + name + ChatColor.GRAY + " has been " + ChatColor.RED + "deleted.");
    }
}
