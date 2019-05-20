package io.github.caprapaul.vital.commands;

import io.github.caprapaul.bettercommandexecutor.BetterCommand;
import io.github.caprapaul.bettercommandexecutor.BetterCommandExecutor;
import io.github.caprapaul.vital.Vital;
import io.github.caprapaul.vital.data.Warp;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WarpCommands extends BetterCommandExecutor
{
    private final Vital plugin;
    private ArrayList<Warp> warps;

    public WarpCommands(Vital plugin)
    {
        this.plugin = plugin;
        this.warps = new ArrayList<Warp>();
        loadCommands(this, this.plugin);
        loadWarps();
    }

    private void loadWarps()
    {
        this.warps = (ArrayList<Warp>)plugin.getWarps().get("warps", new ArrayList<Warp>());
    }

    private void addWarp(Warp warp)
    {
        warps.add(warp);
        plugin.getWarps().set("warps", warps);
    }

    private boolean containsName(String name, Warp returnWarp)
    {
        for(Warp warp : warps)
        {
            if(warp != null && warp.getName().equalsIgnoreCase(name))
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

    @BetterCommand(name = "warp")
    public void warp(CommandSender commandSender, String[] args, String commandLabel)
    {
        if (!(commandSender instanceof Player))
        {
            commandSender.sendMessage(plugin.prefix + ChatColor.RED + "Error: The console can't go anywhere!");
            return;
        }

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
        player.teleport(new Location(plugin.getServer().getWorld(warp.getWorld()), warp.getX(), warp.getY(), warp.getZ()));
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

    @BetterCommand(name = "setwarp")
    public void setwarp(CommandSender commandSender, String[] args, String commandLabel)
    {
        if (!(commandSender instanceof Player))
        {
            commandSender.sendMessage(plugin.prefix + ChatColor.RED + "Error: The console can't set warps!");
            return;
        }

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
        player.sendMessage(plugin.prefix + ChatColor.GRAY + "Warp " + ChatColor.GOLD + name + ChatColor.GRAY + " has been created.");
    }
}
