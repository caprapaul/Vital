package io.github.caprapaul.vital.commands;

import io.github.caprapaul.bettercommandexecutor.BetterCommand;
import io.github.caprapaul.bettercommandexecutor.BetterCommandExecutor;
import io.github.caprapaul.vital.Vital;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;

public class BackCommand extends BetterCommandExecutor implements Listener
{
    public BackCommand(Vital plugin)
    {
        super(plugin);
        loadCommands(this, this.plugin);
    }

    private HashMap<String, Location> previousLocations;
    private HashMap<String, Long> backCooldowns = new HashMap<String, Long>();

    private void loadConfig()
    {
        plugin.getConfig().addDefault("back-cooldown", 60);
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
    }

    @Override
    public void onEnable()
    {
        previousLocations = new HashMap<String, Location>();
        backCooldowns = new HashMap<String, Long>();
        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(this, plugin);
        loadConfig();
    }

    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args)
    {
        parseCommand(this, commandSender, command, commandLabel, args);
        return false;
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        Player player = event.getPlayer();
        previousLocations.put(player.getUniqueId().toString(), event.getFrom());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        previousLocations.put(player.getUniqueId().toString(), player.getLocation());
    }

    @BetterCommand(name = "back")
    public void back(CommandSender commandSender, String[] args, String commandLabel)
    {
        if (!(commandSender instanceof Player))
        {
            commandSender.sendMessage(plugin.prefix + ChatColor.RED + "Error: The console can't teleport!");
            return;
        }

        Player player = (Player) commandSender;

        if (!player.hasPermission("vital.back.overridecooldown"))
        {
            int cooldown = plugin.getConfig().getInt("back-cooldown");
            if (backCooldowns.containsKey(player.getUniqueId().toString()))
            {
                long diff = (System.currentTimeMillis() - backCooldowns.get(player.getUniqueId().toString())) / 1000;
                if (diff < cooldown)
                {
                    player.sendMessage(plugin.prefix + ChatColor.RED + "Error: You must wait " + cooldown + " seconds before you can do that again!");
                    return;
                }
            }
        }

        if(!(previousLocations.containsKey(player.getUniqueId().toString())))
        {
            player.sendMessage(plugin.prefix + ChatColor.RED + "Error: You have nowhere to go!");
            return;
        }

        player.sendMessage(plugin.prefix + ChatColor.GRAY + "Teleporting...");

        player.teleport(previousLocations.get(player.getUniqueId().toString()));
        backCooldowns.put(player.getUniqueId().toString(), System.currentTimeMillis());
    }
}
